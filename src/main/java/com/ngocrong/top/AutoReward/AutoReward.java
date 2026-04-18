/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.top.AutoReward;

import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.user.Player;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Administrator
 */
public class AutoReward {

    public List<TopReward> list = new ArrayList<>();
    public static AutoReward instance;

    public static AutoReward gI() {
        if (instance == null) {
            instance = new AutoReward();
        }
        return instance;
    }

    private static final String FIND_ITEMS_QUERY = "SELECT * FROM nr_rewardtop ORDER BY `id` DESC";

    public static List<Item> jsontoItems(String json) {
        List<Item> items = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject itemObj = jsonArray.getJSONObject(i);

                int idItem = itemObj.optInt("idItem");
                try {
                    Item item = new Item(idItem);
                    item.quantity = itemObj.optInt("quantity", 1);
                    if (itemObj.has("options")) {
                        JSONArray optionsArray = itemObj.getJSONArray("options");
                        for (int j = 0; j < optionsArray.length(); j++) {
                            JSONObject optionObj = optionsArray.getJSONObject(j);
                            ItemOption option = new ItemOption(optionObj.optInt("id"), optionObj.optInt("param"));
                            item.options.add(option);
                        }
                    }
                    if (item.options.isEmpty()) {
                        item.setDefaultOptions();
                    }
                    items.add(item);
                } catch (Exception e) {
                    System.err.println("Error at id : " + idItem);
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return items;
    }

    public static String ItemstoJson(List<Item> items) {
        try {
            JSONArray jsonArray = new JSONArray();

            for (Item item : items) {
                JSONObject itemObj = new JSONObject();
                itemObj.put("idItem", item.template.id); // Giả sử có getter method
                itemObj.put("quantity", item.quantity);

                if (item.options != null && !item.options.isEmpty()) {
                    JSONArray optionsArray = new JSONArray();
                    for (ItemOption option : item.options) {
                        JSONObject optionObj = new JSONObject();
                        optionObj.put("id", option.optionTemplate.id); // Giả sử có getter method
                        optionObj.put("param", option.param); // Giả sử có getter method
                        optionsArray.put(optionObj);
                    }
                    itemObj.put("options", optionsArray);
                }

                jsonArray.put(itemObj);
            }

            return jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "[]"; // Trả về mảng JSON rỗng nếu có lỗi
        }
    }

    public void loadList() {
        try {
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(FIND_ITEMS_QUERY);
            ResultSet rs = ps.executeQuery();
            list.clear();
            try {
                while (rs.next()) {
                    TopReward top = new TopReward();
                    top.id = rs.getInt("id");
                    top.namePlayer = rs.getString("player_name");
                    top.isReward = rs.getByte("isReward") == 1;
                    top.infoTop = rs.getString("infoTop");
                    top.item = jsontoItems(rs.getString("item"));
                    System.err.println("Load List Top " + top.infoTop);

                    list.add(top);
                }
                System.err.println("Load List Top" + list.size());
            } catch (Exception e) {
                
                e.printStackTrace();

            } finally {
                rs.close();
                ps.close();
            }
        } catch (Exception ex) {
            
        }
    }
    private static final String UPDATE_STATUS_QUERY = "UPDATE nr_rewardtop SET isReward = 1 WHERE id = ?";

    public void setReward(int id) {
        try {
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(UPDATE_STATUS_QUERY);
            ps.setInt(1, id);
            try {
                int updated = ps.executeUpdate();
            } catch (Exception e) {
                
            } finally {
                ps.close();
            }
        } catch (Exception ex) {
            
        }
    }

    public void checkAndReward(Player player) {
        if (player == null) {
            return;
        }
 

        // Lấy tất cả top rewards chưa nhận của player
        List<TopReward> unclaimedRewards = list.stream()
                .filter(top -> top.namePlayer.equals(player.name) && !top.isReward)
                .collect(Collectors.toList());

        if (unclaimedRewards.isEmpty()) {
            return;
        }

        try {
            // Gộp tất cả items từ các top rewards
            List<Item> allItems = new ArrayList<>();
            List<String> topInfos = new ArrayList<>();
            List<Integer> rewardIds = new ArrayList<>();

            for (TopReward topReward : unclaimedRewards) {
                if (topReward.item != null && !topReward.item.isEmpty()) {
                    allItems.addAll(topReward.item);
                    topInfos.add(topReward.infoTop);
                    rewardIds.add(topReward.id);
                }
            }

            if (allItems.isEmpty()) {
                return;
            }

            // Kiểm tra số slot cần thiết
            int slotsNeeded = allItems.size();
            if (player.getCountEmptyBag() < slotsNeeded) {
                player.service.dialogMessage("Hành trang không đủ " + slotsNeeded + " ô trống! để nhận thưởng Top ("
                        + String.join(", ", topInfos) + "), hãy kiểm tra và thoát game vào lại");
                return;
            }

            // Tạo message tổng hợp theo format yêu cầu
            StringBuilder message = new StringBuilder("Bạn đã nhận được phần thưởng từ " + unclaimedRewards.size() + " top:\n");

            // Hiển thị từng top và items của nó
            for (int i = 0; i < unclaimedRewards.size(); i++) {
                TopReward reward = unclaimedRewards.get(i);
                message.append(reward.infoTop).append(" : ");

                // Tạo danh sách items cho top này
                List<String> itemStrings = new ArrayList<>();
                for (Item item : reward.item) {
                    itemStrings.add("x" + item.quantity + " " + item.template.name);
                }
                message.append(String.join(", ", itemStrings));

                // Thêm xuống dòng nếu không phải top cuối cùng
                if (i < unclaimedRewards.size() - 1) {
                    message.append("\n");
                }
            }

            // Xử lý từng item
            for (Item item : allItems) {
                if (item.template.id == 2305) {
                    // Xử lý đặc biệt cho item 2305
                    for (int i = 2275; i <= 2277; i++) {
                        Item itemNew = new Item(i);
                        itemNew.quantity = item.quantity / 3;
                        player.addItem(itemNew);
                    }
                } else {
                    player.addItem(item);
                }
            }

            // Đánh dấu tất cả rewards đã nhận
            for (TopReward topReward : unclaimedRewards) {
                setReward(topReward.id);
                topReward.isReward = true;
            }

            player.service.dialogMessage(message.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
