package com.ngocrong.event;

import _HunrProvision.ConfigStudio;
import com.ngocrong.data.OsinLixiData;
import com.ngocrong.data.SuKienTetData;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemOption;
import com.ngocrong.mob.Mob;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.repository.OsinLixiRepository;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.json.JSONArray;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OsinTetEvent {

    private static OsinLixiRepository repo() {
        return GameRepository.getInstance().osinLixiRepository;
    }

    public static final int[] CHU_ANKHANG_THINHVUONG = {2462, 2463, 2464, 2465};
    public static final int[] CHU_CHUCMUNG_NAMMOI = {2466, 2467, 2468, 2469};
    public static final int[] CHU_BINH_NGO_2026 = {2470, 2471, 2472};
    public static final int[] ALL_CHU_IDS = {
            2462, 2463, 2464, 2465,
            2466, 2467, 2468, 2469,
            2470, 2471, 2472
    };

    public static class ChuReward {
        public int itemId;
        public int quantity;
        public List<int[]> options;

        public ChuReward(int itemId, int quantity, List<int[]> options) {
            this.itemId = itemId;
            this.quantity = quantity;
            this.options = options;
        }
    }

    private static List<ChuReward> getRewardForBoChu(int[] chuIds) {
        List<ChuReward> rewards = new ArrayList<>();

   
        if (chuIds.length != 4 && chuIds.length != 3) {
            return rewards;
        }

        boolean hasAn = false, hasKhang = false, hasThinh = false, hasVuong = false;
        boolean hasChuc = false, hasMung = false, hasNam = false, hasMoi = false;
        boolean hasBinh = false, hasNgo = false, has2026 = false;

        for (int chuId : chuIds) {
            if (chuId == 2462) hasAn = true;
            else if (chuId == 2463) hasKhang = true;
            else if (chuId == 2464) hasThinh = true;
            else if (chuId == 2465) hasVuong = true;
            else if (chuId == 2466) hasChuc = true;
            else if (chuId == 2467) hasMung = true;
            else if (chuId == 2468) hasNam = true;
            else if (chuId == 2469) hasMoi = true;
            else if (chuId == 2470) hasBinh = true;
            else if (chuId == 2471) hasNgo = true;
            else if (chuId == 2472) has2026 = true;
        }

        boolean isAnKhangThinhVuong = hasAn && hasKhang && hasThinh && hasVuong;
        boolean isChucMungNamMoi = hasChuc && hasMung && hasNam && hasMoi;
        boolean isBinhNgo2026 = hasBinh && hasNgo && has2026;

        if (isAnKhangThinhVuong) {
            rewards.add(new ChuReward(2263, 50, List.of(new int[]{73, 0})));
            rewards.add(new ChuReward(2262, 50, List.of(new int[]{73, 0})));
            rewards.add(new ChuReward(2265, 50, List.of(new int[]{73, 0})));

        } else if (isChucMungNamMoi) {
            rewards.add(new ChuReward(2264, 1, List.of(new int[]{73, 0})));
        } else if (isBinhNgo2026) {
            rewards.add(new ChuReward(2481, 1, List.of(new int[]{50, 3}, new int[]{77, 3}, new int[]{103, 3})));
        }

        return rewards;
    }

    public static void rutLixi(Player player) {
        if (player == null) {
            return;
        }


        Optional<OsinLixiData> existingLixiOpt = repo().findTodayByPlayer(player.id);
        if (existingLixiOpt.isPresent()) {
            player.service.sendThongBao("Bạn đã rút lì xì hôm nay rồi. Hãy quay lại vào ngày mai.");
            return;
        }

        if (player.getCountEmptyBag() == 0) {
            player.service.sendThongBao("Hành trang đã đầy");
            return;
        }

        int randomChuId = ALL_CHU_IDS[Utils.nextInt(ALL_CHU_IDS.length)];
        Item chuItem = new Item(randomChuId);
        chuItem.setDefaultOptions();
        chuItem.addItemOption(new ItemOption(30, 1));
        chuItem.addItemOption(new ItemOption(174, 2026));
        chuItem.quantity = 1;

        if (player.addItem(chuItem)) {
            OsinLixiData newData = new OsinLixiData();
            newData.setPlayerId(player.id);
            newData.setLixiDate(Instant.now());
            repo().save(newData);

            player.service.sendThongBao("Bạn nhận được: " + chuItem.template.name);
        } else {
            player.service.sendThongBao("Không thể nhận lì xì. Hành trang đã đầy.");
        }
    }

    public static void doiChu(Player player, int[] chuIds) {
        if (player == null) {
            return;
        }

        for (int chuId : chuIds) {
            Item chuItem = player.getItemInBag(chuId);
            if (chuItem == null || chuItem.quantity < 1) {
                player.service.sendThongBao("Bạn không đủ chữ để đổi. Cần đủ 4 chữ cùng bộ.");
                return;
            }
        }

        List<ChuReward> rewards = getRewardForBoChu(chuIds);
        
        if (rewards.isEmpty()) {
            player.service.sendThongBao("Bộ chữ không hợp lệ.");
            return;
        }

        if (player.getCountEmptyBag() < rewards.size()) {
            player.service.sendThongBao("Hành trang không đủ chỗ. Cần ít nhất " + rewards.size() + " ô trống.");
            return;
        }

        List<Item> rewardItems = new ArrayList<>();
        StringBuilder rewardMessage = new StringBuilder();
        boolean hasError = false;

        for (ChuReward reward : rewards) {
            Item rewardItem = new Item(reward.itemId);
            rewardItem.setDefaultOptions();
            rewardItem.quantity = reward.quantity;
            
            if (reward.options != null) {
                for (int[] option : reward.options) {
                    if (option.length >= 2) {
                        rewardItem.addItemOption(new ItemOption(option[0], option[1]));
                    }
                }
            }
            
            rewardItems.add(rewardItem);
            if (rewardMessage.length() > 0) {
                rewardMessage.append(", ");
            }
            rewardMessage.append(rewardItem.template.name).append(" x").append(reward.quantity);
        }

        for (int chuId : chuIds) {
            Item chuItem = player.getItemInBag(chuId);
            player.removeItem(chuItem.indexUI, 1);
        }

        for (Item rewardItem : rewardItems) {
            if (!player.addItem(rewardItem)) {
                hasError = true;
                break;
            }
        }

        if (hasError) {
            player.service.sendThongBao("Không thể nhận quà. Hành trang đã đầy.");
            for (int chuId : chuIds) {
                Item chuItem = new Item(chuId);
                chuItem.setDefaultOptions();
                chuItem.addItemOption(new ItemOption(30, 1));
                chuItem.addItemOption(new ItemOption(174, 2026));
                chuItem.quantity = 1;
                player.addItem(chuItem);
            }
        } else {
            player.service.sendThongBao("Bạn đã đổi thành công! Nhận được: " + rewardMessage.toString());
        }
    }

    public static void mobReward(Player player, Mob mob) {
        if (player == null || mob == null || player.zone == null) {
            return;
        }

        if (!ConfigStudio.EVENT_NEWYEAR_2026) {
            return;
        }

        if (mob.isBoss) {
            return;
        }

        int dropRate = player.getSession().user.getActivated() == 1 ? 15 : 10;
        if (!Utils.isTrue(dropRate, 100)) {
            return;
        }

        int[] itemIds = {2476, 2477, 2478};
        int randomItemId = itemIds[Utils.nextInt(itemIds.length)];

        Item item = new Item(randomItemId);
        item.setDefaultOptions();
        item.quantity = 1;

        ItemMap map = new ItemMap(player.zone.autoIncrease++);
        map.item = item;
        map.playerID = Math.abs(player.id);
        map.x = player.getX();
        map.y = player.zone.map.collisionLand(player.getX(), player.getY());

        player.zone.addItemMap(map);
        mob.items.add(map);

        if (player.exitsItemTime(2485) && Utils.isTrue(10, 100)) {
            int[] vipIds = {2479, 2480};
            int vipId = vipIds[Utils.nextInt(vipIds.length)];
            Item vipItem = new Item(vipId);
            vipItem.setDefaultOptions();
            vipItem.quantity = 1;
            ItemMap mapVip = new ItemMap(player.zone.autoIncrease++);
            mapVip.item = vipItem;
            mapVip.playerID = Math.abs(player.id);
            mapVip.x = player.getX();
            mapVip.y = player.zone.map.collisionLand(player.getX(), player.getY());
            player.zone.addItemMap(mapVip);
            mob.items.add(mapVip);
        }
    }

    public static boolean hasEnoughTuPhuc(Player player) {
        if (player == null) {
            return false;
        }

        int[] tuPhucIds = {2476, 2477, 2478};
        for (int itemId : tuPhucIds) {
            Item item = player.getItemInBag(itemId);
            if (item == null || item.quantity < 1) {
                return false;
            }
        }
        return true;
    }

    public static void dangTuPhuc(Player player, boolean isVip) {
        if (player == null) {
            return;
        }

        if (!ConfigStudio.EVENT_NEWYEAR_2026) {
            player.service.sendThongBao("Sự kiện Tết chưa được kích hoạt.");
            return;
        }

        if (!hasEnoughTuPhuc(player)) {
            player.service.sendThongBao("Bạn cần có đủ 3 túi phúc để dâng.");
            return;
        }

        if (player.getCountEmptyBag() == 0) {
            player.service.sendThongBao("Hành trang đã đầy");
            return;
        }

        if (isVip) {
            int[] vipNeed = {2479, 2480};
            for (int itemId : vipNeed) {
                Item it = player.getItemInBag(itemId);
                if (it == null || it.quantity < 1) {
                    player.service.sendThongBao("Thiếu vật phẩm VIP để dâng.");
                    return;
                }
            }
            for (int itemId : vipNeed) {
                Item it = player.getItemInBag(itemId);
                if (it != null) {
                    player.removeItem(it.indexUI, 1);
                }
            }
        }

        int[] tuPhucIds = {2476, 2477, 2478};
        for (int itemId : tuPhucIds) {
            Item item = player.getItemInBag(itemId);
            if (item != null) {
                player.removeItem(item.indexUI, 1);
            }
        }

        Item hopQua = new Item(isVip ? 2484 : 2483);
        hopQua.setDefaultOptions();
        hopQua.quantity = 1;

        if (player.addItem(hopQua)) {
            player.service.sendThongBao("Bạn đã dâng túi phúc thành công! Nhận được: " + hopQua.template.name);
        } else {
            player.service.sendThongBao("Không thể nhận hộp quà. Hành trang đã đầy.");
            for (int itemId : tuPhucIds) {
                Item rollbackItem = new Item(itemId);
                rollbackItem.setDefaultOptions();
                rollbackItem.quantity = 1;
                player.addItem(rollbackItem);
            }
        }
    }

    public static void dangTuPhuc(Player player) {
        dangTuPhuc(player, false);
    }

    public static boolean useHopQuaTet(Player player, Item item) {
        if (player == null || item == null) {
            return false;
        }

        if (item.template.id != 2483 && item.template.id != 2484) {
            return false;
        }

        if (!ConfigStudio.EVENT_NEWYEAR_2026) {
            return false;
        }

        int needSlot = 1;
        if (player.getCountEmptyBag() < needSlot) {
            player.service.dialogMessage("Hành trang đã đầy");
            return false;
        }

        if (item.template.id == 2483) {
            try {
                JSONArray arr;
                Optional<SuKienTetData> opt = GameRepository.getInstance().eventTet.findFirstByName((int) player.id);
                if (opt.isPresent() && opt.get().point != null && !opt.get().point.isEmpty() && !opt.get().point.equals("null")) {
                    arr = new JSONArray(opt.get().point);
                } else {
                    arr = new JSONArray();
                }
                while (arr.length() <= 14) {
                    arr.put(0);
                }
                arr.put(14, arr.getInt(14) + 1);
                if (opt.isPresent()) {
                    GameRepository.getInstance().eventTet.setReward((int) player.id, arr.toString());
                } else {
                    SuKienTetData data = new SuKienTetData();
                    data.setPlayerId((int) player.id);
                    data.setPoint(arr.toString());
                    data.setCreateDate(Instant.now());
                    data.setModifyDate(Instant.now());
                    GameRepository.getInstance().eventTet.save(data);
                }
            } catch (Exception ignored) {
            }
        } else {
            _event.newyear_2026.EventNewYear2026.addEventPoint(player, 1);
        }

        player.removeItem(item.indexUI, 1);

        if (item.template.id == 2483) {
            Item petItem = new Item(2189);
            petItem.setDefaultOptions();
            petItem.addItemOption(new ItemOption(50, Utils.nextInt(1, 7)));
            petItem.addItemOption(new ItemOption(77, Utils.nextInt(1, 7)));
            petItem.addItemOption(new ItemOption(103, Utils.nextInt(1, 7)));
            if (Utils.isTrue(10, 100)) {
                petItem.addItemOption(new ItemOption(93, Utils.nextInt(1, 7)));
            }
            petItem.quantity = 1;
            if (player.addItem(petItem)) {
                player.service.sendThongBao("Bạn nhận được " + petItem.template.name);
                return true;
            }
            return false;
        } else {
            boolean isBack = Utils.isTrue(50, 100);
            if (isBack) {
                int[] backs = {2346, 2355, 2435};
                int backId = backs[Utils.nextInt(backs.length)];
                Item back = new Item(backId);
                back.setDefaultOptions();
                back.addItemOption(new ItemOption(50, Utils.nextInt(1, 10)));
                back.addItemOption(new ItemOption(77, Utils.nextInt(1, 10)));
                back.addItemOption(new ItemOption(103, Utils.nextInt(1, 10)));
                if (Utils.isTrue(5, 100)) {
                    back.addItemOption(new ItemOption(93, Utils.nextInt(1, 7)));
                }
                back.quantity = 1;
                if (player.addItem(back)) {
                    player.service.sendThongBao("Bạn nhận được " + back.template.name);
                    return true;
                }
                return false;
            } else {
                Item petVip = new Item(2486);
                petVip.setDefaultOptions();
                petVip.addItemOption(new ItemOption(50, Utils.nextInt(1, 7)));
                petVip.addItemOption(new ItemOption(77, Utils.nextInt(1, 7)));
                petVip.addItemOption(new ItemOption(103, Utils.nextInt(1, 7)));
                if (Utils.isTrue(10, 100)) {
                    petVip.addItemOption(new ItemOption(93, Utils.nextInt(1, 7)));
                }
                petVip.quantity = 1;
                if (player.addItem(petVip)) {
                    player.service.sendThongBao("Bạn nhận được " + petVip.template.name);
                    return true;
                }
                return false;
            }
        }
    }

    public static boolean useItem(Player player, Item item) {
        if (player == null || item == null) {
            return false;
        }
        if (item.template.id != 2485) {
            return false;
        }
        player.setItemTime(2485, item.template.iconID, true, 10 * 60);
        player.removeItem(item.indexUI, 1);
        player.service.sendThongBao("giúp ta tìm túi phúc bị bọn quái lấy trộm nhé. cảm ơn ngươi");
        return true;
    }
}
