/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _HunrProvision.minigame;

import com.ngocrong.util.CSMM;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.ItemTimeName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemTime;
import com.ngocrong.network.Message;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author Administrator
 */
public class ConSoMayMan {

    public static String showInfo() {
        if (!lastPlayer.isEmpty()) {
            String name = "Thắng giải trước :";
            for (int i = 0; i < lastPlayer.size(); i++) {
                name += lastPlayer.get(i) + ",";
            }
            name = name.substring(0, name.length() - 1);
            return String.format("Kết quả giải trước : %d \n"
                    + "%s \n"
                    + "Tham gia : %d - Tổng giải thưởng :%d\n"
                    + "%d giây\n", lastNum, name, listPlayer.size(), getGiaiThuong(), time);
        }
        return String.format("Kết quả giải trước : %d \n"
                + "Tham gia : %d - Tổng giải thưởng :%d\n"
                + "%d giây\n", lastNum, listPlayer.size(), getGiaiThuong(), time);
    }
    public static String details
            = "Thời gian diễn ra từ cả ngày, không giới hạn thời gian tham gia\n"
            + "Mỗi lượt bạn sẽ được chọn 1 số, 10 số, 100 số \n"
            + "Được chọn tối đa sẽ là 500 số cho 1 người chơi ";
    static ArrayList<CSMM> listPlayer = new ArrayList<>();
    static short lastNum = -1;
    static short result = -1;
    static ArrayList<String> lastPlayer = new ArrayList<>();
    static short time = 300;
    static long lastUpdate = 0;
    static short timeWait = 300;

    static int getGiaiThuong() {
        // Tính tổng số lượng các số mà tất cả người chơi đã chọn
        int totalNumbers = listPlayer.stream()
                .mapToInt(csmm -> csmm.listNum.size())
                .sum();

        // Trả về giá trị nhỏ hơn giữa 20 và (totalNumbers / 5)
        return 100 + (int) (totalNumbers * 0.65);
    }

    static void getNum() {
        result = (short) Utils.nextInt(1000);
    }

    static void checkReward() {
        if (lastNum < 0) {
            return;
        }

//        Danh sách các người chơi chọn số trùng với lastNum
        List<Player> winners = listPlayer.stream()
                .filter(csmm -> csmm.listNum.contains((int) lastNum))
                .map(csmm -> csmm.player)
                .collect(Collectors.toList());

        // Thêm người chơi chiến thắng vào danh sách lastPlayer
        lastPlayer.clear();
        winners.forEach(player -> lastPlayer.add(player.getName()));

        // Gửi thông báo tới người chơi chiến thắng
        winners.forEach(player -> {
            player.service.sendThongBao("Chúc mừng bạn đã thắng với số " + lastNum + "!");
            int quantity = getGiaiThuong() / winners.size();
            Item item = new Item(ItemName.THOI_VANG);
            item.setDefaultOptions();
            item.quantity = quantity;
            player.addItem(item);
            player.service.sendThongBao(String.format("Bạn nhận được %d thỏi vàng", quantity));
        });
    }

    public static void update() {
        if (System.currentTimeMillis() - lastUpdate >= 1000) {
            lastUpdate = System.currentTimeMillis();
            time--;
        }
        if (time == 0) {
            try {
                getNum();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                spin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (time <= -5) {
            lastNum = result;
            try {
                checkReward();
            } catch (Exception e) {
                e.printStackTrace();
            }

            listPlayer.clear();
            time = timeWait;
        }
    }

    public static void Add(Player player, int type, int num) {
        // Kiểm tra các điều kiện đầu vào
        if (player == null || type < 0 || type > 4 || num < 0 || num > 999 || time < 5) {
            return;
        }
        //khoidt
        if (player.getSession().user.getActivated() != 1) {
            player.service.sendThongBao("Bạn cần kích hoạt tài khoản để sử dụng tính năng này");
            return;
        }

        // Xác định số lượng thỏi vàng cần dùng dựa vào loại type
        int quantity;
        switch (type) {
            case 0:
                quantity = 1;
                break;    // Chọn 1 số
            case 1:
                quantity = 10;
                break;   // Chọn 10 số liên tiếp
            case 2:
                quantity = 100;
                break;  // Chọn 100 số liên tiếp
            case 3: // 500 số lẻ từ 0-999
            case 4: // 500 số chẵn từ 0-999
                quantity = 500;
                break;
            default:
                return;
        }

        // Tính toán số nhỏ nhất và lớn nhất cho type 0-2
        int min = type <= 2 ? num : 0;
        int max = type == 0 ? num : type == 1 ? num + 9 : type == 2 ? num + 99 : 999;

        // Xử lý khác biệt cho type 3 và 4
        List<Integer> numbersToAdd = new ArrayList<>();
        if (type <= 2) {
            // Kiểm tra các số đã chọn cho type 0-2
            boolean flag = IntStream.rangeClosed(min, max)
                    .anyMatch(i -> Contains(player, i));
            if (flag) {
                player.service.sendThongBao("Số này bạn đã chọn rồi");
                return;
            }
        } else {
            // Xử lý cho type 3 (số lẻ) và type 4 (số chẵn)
            IntStream numberStream = IntStream.rangeClosed(0, 999)
                    .filter(i -> type == 3 ? i % 2 == 1 : i % 2 == 0);

            numbersToAdd = numberStream.boxed().collect(Collectors.toList());
            if (numbersToAdd.stream().anyMatch(i -> Contains(player, i))) {
                player.service.sendThongBao("Một số trong dãy số này bạn đã chọn rồi");
                return;
            }
        }

        // Tìm hoặc tạo mới đối tượng CSMM cho người chơi
        CSMM csmm = listPlayer.stream()
                .filter(c -> c.player.equals(player))
                .findFirst()
                .orElseGet(() -> {
                    CSMM newCsmm = new CSMM();
                    newCsmm.player = player;
                    listPlayer.add(newCsmm);
                    return newCsmm;
                });

        // Kiểm tra giới hạn số lượng số có thể chọn
        int currentSize = csmm.listNum.size();
        int rangeSize = type <= 2 ? (max - min + 1) : 500;
        if (currentSize + rangeSize > 100) {
            player.service.sendThongBao("Bạn không thể chọn quá 100 số!");
            return;
        }

        // Kiểm tra số lượng thỏi vàng
        Item item = player.getItemInBag(ItemName.THOI_VANG);
        if (item == null || item.quantity < quantity) {
            player.service.sendThongBao("Bạn không đủ thỏi vàng");
            return;
        }
        player.removeItem(item.indexUI, quantity);

        // Thêm các số đã chọn vào danh sách
        if (type <= 2) {
            IntStream.rangeClosed(min, max).forEach(csmm.listNum::add);
        } else {
            csmm.listNum.addAll(numbersToAdd);
        }

        // Hiển thị số lượng số mà người chơi đã chọn
        showNumberPlayer(player);
        ItemTime itemTime = new ItemTime(-1, 2295, time, false);
        player.addItemTime(itemTime);
    }

    public static CSMM Contains(Player player) {
        for (CSMM csmm : listPlayer) {
            if (csmm.player.equals(player)) {
                return csmm;
            }
        }
        return null;
    }

    public static boolean Contains(Player player, int num) {
        return listPlayer.stream()
                .filter(csmm -> csmm.player.equals(player))
                .anyMatch(csmm -> csmm.listNum.contains(num));

    }

    public static String getStrNum(ArrayList<Integer> listNum) {
        if (listNum == null || listNum.isEmpty()) {
            return "";
        }

        // Sắp xếp danh sách trước để đảm bảo thứ tự
        Collections.sort(listNum);

        // Kiểm tra xem có phải là 500 số chẵn hoặc lẻ không
        boolean isAllEven = listNum.size() == 500 && listNum.stream().allMatch(num -> num % 2 == 0);
        boolean isAllOdd = listNum.size() == 500 && listNum.stream().allMatch(num -> num % 2 == 1);

        // Trả về chuỗi đặc biệt cho 500 số chẵn hoặc lẻ
        if (isAllEven) {
            return "Tất cả số chẵn từ 0-998";
        }
        if (isAllOdd) {
            return "Tất cả số lẻ từ 1-999";
        }

        StringBuilder result = new StringBuilder();
        int start = listNum.get(0);
        int end = start;

        for (int i = 1; i < listNum.size(); i++) {
            int current = listNum.get(i);
            if (current == end + 1) {
                // Số liền kề, cập nhật end
                end = current;
            } else {
                // Không liền kề, ghi lại khoảng hiện tại và bắt đầu khoảng mới
                if (start == end) {
                    result.append(start).append(",");
                } else {
                    result.append(start).append("-").append(end).append(",");
                }
                start = current;
                end = current;
            }
        }

        // Xử lý khoảng cuối cùng
        if (start == end) {
            result.append(start);
        } else {
            result.append(start).append("-").append(end);
        }

        return result.toString();
    }

    public static void showNumberPlayer(Player player) {
        if (player == null || Contains(player) == null) {
            return;
        }
        Message msg = null;
        try {
            msg = new Message(-126);
            msg.writer().writeByte(0); // type
            msg.writer().writeUTF(getStrNum(Contains(player).listNum)); // number player select
            player.sendMessage(msg);
        } catch (Exception e) {
            
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void spin() {
        List<Player> winners = listPlayer.stream()
                .filter(csmm -> csmm.player != null)
                .map(csmm -> csmm.player)
                .collect(Collectors.toList());

        for (Player player : winners) {
            spinNumber(player, String.valueOf(result), "Con số may mắn là " + result);
        }
    }

    public static void spinNumber(Player player, String result, String finish) {
        Message msg = null;
        try {
            msg = new Message(-126);
            msg.writer().writeByte(1); // type
            msg.writer().writeByte(1); // type
            msg.writer().writeUTF(result); // kết quả
            msg.writer().writeUTF(finish); // thông báo cho người chơi
            player.sendMessage(msg);
        } catch (Exception e) {
            
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

}
