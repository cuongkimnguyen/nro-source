package com.ngocrong.NQMP.Event;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.ItemTimeName;
import com.ngocrong.consts.NpcName;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.mob.Mob;
import com.ngocrong.model.RandomItem;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;

/**
 * Sự kiện Quốc Khánh 2-9
 */
public class QuocKhanh {

    private static final int[] MOB_LETTERS = {
        ItemName.CHU_Q, ItemName.CHU_U, ItemName.CHU_O, ItemName.CHU_C
    };

    private static final int[] BOSS_LETTERS = {
        ItemName.CHU_K, ItemName.CHU_H, ItemName.CHU_A, ItemName.CHU_N, ItemName.CHU_H_2
    };

    private static final int[] ALL_LETTERS = {
        ItemName.CHU_Q, ItemName.CHU_U, ItemName.CHU_O, ItemName.CHU_C,
        ItemName.CHU_K, ItemName.CHU_H, ItemName.CHU_A, ItemName.CHU_N, ItemName.CHU_H_2
    };

    private static final int[] NUMBERS = {ItemName.SO_2, ItemName.SO_9};

    public static void mobReward(Player player, Mob mob) {
        if (player == null || player.zone == null || true) {
            return;
        }
        if (!Utils.isTrue(1, 100)) {
            return;
        }
        int id = MOB_LETTERS[Utils.nextInt(MOB_LETTERS.length)];
        Item item = new Item(id);
        item.setDefaultOptions();
        item.quantity = 1;
        ItemMap map = new ItemMap(player.zone.autoIncrease++);
        map.item = item;
        map.playerID = Math.abs(player.id);
        map.x = player.getX();
        map.y = player.zone.map.collisionLand(player.getX(), player.getY());
        player.zone.addItemMap(map);
        if (mob != null) {
            mob.items.add(map);
        }
    }

    public static void bossReward(Player player, Boss boss) {
        if (player == null || boss == null || boss.zone == null || true) {
            return;
        }
        int id = BOSS_LETTERS[Utils.nextInt(BOSS_LETTERS.length)];
        Item item = new Item(id);
        item.setDefaultOptions();
        item.quantity = 1;
        ItemMap map = new ItemMap(boss.zone.autoIncrease++);
        map.item = item;
        map.playerID = Math.abs(player.id);
        map.x = boss.getX();
        map.y = boss.zone.map.collisionLand(boss.getX(), boss.getY());
        boss.zone.addItemMap(map);
        boss.zone.service.addItemMap(map);
    }

    public static boolean useItem(Player player, Item item) {
        if (player == null || item == null) {
            return false;
        }
        int itemId = item.template.id;
        // Check bag space once for items that produce rewards
        if ((itemId == ItemName.RUONG_CHU_NGAU_NHIEN || itemId == ItemName.RUONG_CHU_DUOC_CHON
                || itemId == ItemName.HOP_QUA_THUONG || itemId == ItemName.HOP_QUA_CAO_CAP || itemId == ItemName.HOP_QUA_DAC_BIET)
                && player.getCountEmptyBag() == 0) {
            player.service.dialogMessage("Hành trang đã đầy");
            return false;
        }
        if (itemId == ItemName.RUONG_CHU_NGAU_NHIEN) {
            player.removeItem(item.indexUI, 1);
            int id;
            if (Utils.isTrue(95, 100)) {
                id = ALL_LETTERS[Utils.nextInt(ALL_LETTERS.length)];
            } else {
                id = NUMBERS[Utils.nextInt(NUMBERS.length)];
            }
            Item reward = new Item(id);
            reward.setDefaultOptions();
            player.addItem(reward);
            player.service.sendThongBao("Bạn nhận được " + reward.template.name);
            return true;
        }
        if (itemId == ItemName.RUONG_CHU_DUOC_CHON) {

            player.menus.clear();
            player.menus.add(new KeyValue<>(CMDMenu.RUONG_CHU_QUOC_KHANH, "Chữ Q", ItemName.CHU_Q));
            player.menus.add(new KeyValue<>(CMDMenu.RUONG_CHU_QUOC_KHANH, "Chữ U", ItemName.CHU_U));
            player.menus.add(new KeyValue<>(CMDMenu.RUONG_CHU_QUOC_KHANH, "Chữ O", ItemName.CHU_O));
            player.menus.add(new KeyValue<>(CMDMenu.RUONG_CHU_QUOC_KHANH, "Chữ C", ItemName.CHU_C));
            player.menus.add(new KeyValue<>(CMDMenu.RUONG_CHU_QUOC_KHANH, "Chữ K", ItemName.CHU_K));
            player.menus.add(new KeyValue<>(CMDMenu.RUONG_CHU_QUOC_KHANH, "Chữ H", ItemName.CHU_H));
            player.menus.add(new KeyValue<>(CMDMenu.RUONG_CHU_QUOC_KHANH, "Chữ A", ItemName.CHU_A));
            player.menus.add(new KeyValue<>(CMDMenu.RUONG_CHU_QUOC_KHANH, "Chữ N", ItemName.CHU_N));
            player.menus.add(new KeyValue<>(CMDMenu.RUONG_CHU_QUOC_KHANH, "Chữ H2", ItemName.CHU_H_2));
            player.menus.add(new KeyValue<>(CMDMenu.CANCEL, "Từ chối"));
            player.service.openUIConfirm(NpcName.CON_MEO, "Bạn muốn nhận chữ nào?", player.getPetAvatar(), player.menus);
            return true;
        }
        if (itemId == ItemName.HOP_QUA_THUONG) {
            openBoxThuong(player, item);
            return true;
        }
        if (itemId == ItemName.HOP_QUA_CAO_CAP) {
            openBoxCaoCap(player, item);
            return true;
        }
        if (itemId == ItemName.HOP_QUA_DAC_BIET) {
            openBoxDacBiet(player, item);
            return true;
        }
        if (itemId == ItemName.SAO_VANG) {
            player.removeItem(item.indexUI, 1);
            player.setItemTime(ItemTimeName.SAO_VANG, item.template.iconID, true, 10 * 60);
            player.service.sendThongBao("Sư phụ và đệ tử +30% TNSM trong 10 phút");
            return true;
        }
        if (itemId == ItemName.MU_COI) {
            player.removeItem(item.indexUI, 1);
            player.setItemTime(ItemTimeName.MU_COI, item.template.iconID, true, 10 * 60);
            player.service.sendThongBao("Tăng 10% HP trong 10 phút");
            return true;
        }
        if (itemId == ItemName.SEN_HONG) {
            player.removeItem(item.indexUI, 1);
            player.setItemTime(ItemTimeName.SEN_HONG, item.template.iconID, true, 10 * 60);
            player.service.sendThongBao("Tăng 10% KI trong 10 phút");
            return true;
        }
        if (itemId == ItemName.NGOI_SAO_LAP_LANH) {
            player.removeItem(item.indexUI, 1);
            player.setItemTime(ItemTimeName.NGOI_SAO_HI_VONG, item.template.iconID, true, 10 * 60);
            player.service.sendThongBao("Tăng 10% Sức đánh trong 10 phút");
            return true;
        }
        if (itemId == ItemName.NU_HOA_SEN) {
            player.removeItem(item.indexUI, 1);
            player.setItemTime(ItemTimeName.NU_HOA_SEN, item.template.iconID, true, 10 * 60);
            player.service.sendThongBao("Sư phụ và đệ tử +100% TNSM trong 10 phút");
            return true;
        }
        if (itemId == ItemName.TRAI_TIM_CHUNG_TAY) {
            player.removeItem(item.indexUI, 1);
            player.setItemTime(ItemTimeName.TRAI_TIM_CHUNG_TAY, item.template.iconID, true, 10 * 60);
            player.service.sendThongBao("Đệ tử +50% TNSM trong 10 phút");
            return true;
        }
        return false;
    }

    private static void openBoxDacBiet(Player player, Item box) {
//        player.hopQuaDacBiet++;
//        player.isChangePoint = true;
        player.removeItem(box.indexUI, 1);
        int r = Utils.nextInt(100);
        Item reward;
        if (r < 10) {
            reward = new Item(ItemName.THU_CUOI_HOA_QUY);
            reward.options.add(new ItemOption(77, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(103, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(50, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(5, Utils.nextInt(1, 5)));
            if (Utils.isTrue(90, 100)) {
                reward.options.add(new ItemOption(93, Utils.nextInt(1, 5)));
            }
        } else if (r < 20) {
            reward = new Item(ItemName.THU_CUOI_HOA_QUY);
            reward.options.add(new ItemOption(77, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(103, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(50, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(197, Utils.nextInt(1, 5)));
            if (Utils.isTrue(90, 100)) {
                reward.options.add(new ItemOption(93, Utils.nextInt(1, 5)));
            }
        } else if (r < 30) {
            reward = new Item(ItemName.THU_CUOI_HOA_QUY);
            reward.options.add(new ItemOption(77, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(103, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(50, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(196, Utils.nextInt(1, 5)));
            if (Utils.isTrue(90, 100)) {
                reward.options.add(new ItemOption(93, Utils.nextInt(1, 5)));
            }
        } else if (r < 40) {
            reward = new Item(ItemName.THU_CUOI_HOA_QUY);
            reward.options.add(new ItemOption(77, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(103, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(50, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(94, Utils.nextInt(1, 5)));
            if (Utils.isTrue(90, 100)) {
                reward.options.add(new ItemOption(93, Utils.nextInt(1, 5)));
            }
        } else if (r < 50) {
            reward = new Item(ItemName.PET_PIKACHU);
            reward.options.add(new ItemOption(77, Utils.nextInt(10, 16)));
            reward.options.add(new ItemOption(103, Utils.nextInt(10, 16)));
            reward.options.add(new ItemOption(50, Utils.nextInt(10, 16)));
            reward.options.add(new ItemOption(94, Utils.nextInt(1, 5)));
            if (Utils.isTrue(90, 100)) {
                reward.options.add(new ItemOption(93, Utils.nextInt(1, 5)));
            }
        } else if (r < 60) {
            reward = new Item(ItemName.PET_PIKACHU);
            reward.options.add(new ItemOption(77, Utils.nextInt(10, 16)));
            reward.options.add(new ItemOption(103, Utils.nextInt(10, 16)));
            reward.options.add(new ItemOption(50, Utils.nextInt(10, 16)));
            reward.options.add(new ItemOption(5, Utils.nextInt(1, 5)));
            if (Utils.isTrue(90, 100)) {
                reward.options.add(new ItemOption(93, Utils.nextInt(1, 5)));
            }
        } else if (r < 70) {
            reward = new Item(ItemName.PET_PIKACHU);
            reward.options.add(new ItemOption(77, Utils.nextInt(10, 16)));
            reward.options.add(new ItemOption(103, Utils.nextInt(10, 16)));
            reward.options.add(new ItemOption(50, Utils.nextInt(10, 16)));
            reward.options.add(new ItemOption(197, Utils.nextInt(1, 5)));
            if (Utils.isTrue(90, 100)) {
                reward.options.add(new ItemOption(93, Utils.nextInt(1, 5)));
            }
        } else if (r < 80) {
            reward = new Item(ItemName.PET_PIKACHU);
            reward.options.add(new ItemOption(77, Utils.nextInt(10, 16)));
            reward.options.add(new ItemOption(103, Utils.nextInt(10, 16)));
            reward.options.add(new ItemOption(50, Utils.nextInt(10, 16)));
            reward.options.add(new ItemOption(196, Utils.nextInt(1, 5)));
            if (Utils.isTrue(90, 100)) {
                reward.options.add(new ItemOption(93, Utils.nextInt(1, 5)));
            }
        } else if (r < 85) {
            reward = new Item(ItemName.NGOC_THAN_LONG);
            reward.options.add(new ItemOption(77, Utils.nextInt(5, 10)));
            reward.options.add(new ItemOption(103, Utils.nextInt(5, 10)));
            reward.options.add(new ItemOption(50, Utils.nextInt(5, 10)));
            if (Utils.isTrue(90, 100)) {
                reward.options.add(new ItemOption(93, Utils.nextInt(1, 5)));
            }
        } else if (r < 95) {
            reward = new Item(2380);
            reward.setDefaultOptions();
            reward.quantity = Utils.nextInt(1, 3);
        } else {
            reward = new Item(ItemName.NGOC_RONG_3_SAO);
            reward.setDefaultOptions();
        }
        player.addItem(reward);
        player.service.sendThongBao("Bạn nhận được " + reward.template.name);
    }

    private static void openBoxThuong(Player player, Item box) {
//        player.hopQuathuong++;
//        player.isChangePoint = true;
        player.removeItem(box.indexUI, 1);
        int r = Utils.nextInt(120);
        Item reward;
        if (r < 15) {
            reward = new Item(ItemName.SO_2);
            reward.setDefaultOptions();
            player.addItem(reward);
        } else if (r < 30) {
            reward = new Item(ItemName.SO_9);
            reward.setDefaultOptions();
            player.addItem(reward);
        } else if (r < 45) {
            reward = new Item(ItemName.DEO_LUNG_CO_VIET_NAM);
            reward.options.add(new ItemOption(77, 10 + Utils.nextInt(6)));
            reward.options.add(new ItemOption(103, 10 + Utils.nextInt(6)));
            reward.options.add(new ItemOption(50, 10 + Utils.nextInt(6)));
            int[] option = new int[]{5, 196, 197};
            reward.options.add(new ItemOption(option[Utils.nextInt(option.length)], 1 + Utils.nextInt(5)));
            if (Utils.isTrue(90, 100)) {
                reward.options.add(new ItemOption(93, Utils.nextInt(1, 5)));
            }
            player.addItem(reward);
        } else if (r < 60) {
            int id = RandomItem.ITEM_CAP_1.next();
            reward = new Item(id);
            reward.setDefaultOptions();
            player.addItem(reward);
        } else if (r < 75) {
            int id = Utils.nextInt(ItemName.NGOC_RONG_3_SAO, ItemName.NGOC_RONG_7_SAO);
            reward = new Item(id);
            reward.setDefaultOptions();
            player.addItem(reward);
        } else if (r < 90) {
            int[] arr = {ItemName.MANH_TRUNG_UY_XANH_LO, ItemName.MANH_DOC_NHAN, ItemName.MANH_DOI_TRUONG_VANG_956};
            reward = new Item(arr[Utils.nextInt(arr.length)]);
            reward.setDefaultOptions();
            player.addItem(reward);
        } else if (r < 105) {
            reward = new Item(ItemName.DANH_HIEU_QUOC_KHANH);
            reward.options.add(new ItemOption(77, 3 + Utils.nextInt(6)));
            reward.options.add(new ItemOption(103, 3 + Utils.nextInt(6)));
            reward.options.add(new ItemOption(50, 3 + Utils.nextInt(6)));
            if (Utils.isTrue(90, 100)) {
                reward.options.add(new ItemOption(93, Utils.nextInt(1, 5)));
            }
            player.addItem(reward);
        } else {
            reward = new Item(ItemName.SAO_VANG);
            reward.setDefaultOptions();
            player.addItem(reward);
        }
        player.service.sendThongBao("Bạn nhận được " + reward.template.name);
    }

    private static void openBoxCaoCap(Player player, Item box) {
        int r = Utils.nextInt(100);
        int need = r < 10 ? 3 : 1;
        if (player.getCountEmptyBag() < need) {
            player.service.dialogMessage("Hành trang đã đầy");
            return;
        }
        player.removeItem(box.indexUI, 1);
//        player.hopQuaVip++;
//        player.isChangePoint = true;
        if (r < 10) {
            for (int i = 0; i < 3; i++) {
                int id = RandomItem.ITEM_CAP_2.next();
                Item it = new Item(id);
                it.setDefaultOptions();
                player.addItem(it);
            }
            player.service.sendThongBao("Bạn nhận được Item cấp 2 x3");
            return;
        }
        Item reward;
        if (r < 20) {
            reward = new Item(ItemName.CAI_TRANG_GOKU_AO_CO_VIET_NAM);
            reward.options.add(new ItemOption(77, 30 + Utils.nextInt(6)));
            reward.options.add(new ItemOption(103, 30 + Utils.nextInt(6)));
            reward.options.add(new ItemOption(50, 30 + Utils.nextInt(6)));
            int[] option = new int[]{108, 196, 197};
            reward.options.add(new ItemOption(option[Utils.nextInt(option.length)], 1 + Utils.nextInt(5)));
            if (Utils.isTrue(90, 100)) {
                reward.options.add(new ItemOption(93, Utils.nextInt(1, 5)));
            }
        } else if (r < 30) {
            reward = new Item(ItemName.HAO_QUANG_SAO_ROI);
            reward.options.add(new ItemOption(77, 3 + Utils.nextInt(5)));
            reward.options.add(new ItemOption(103, 3 + Utils.nextInt(5)));
            reward.options.add(new ItemOption(50, 3 + Utils.nextInt(5)));
            if (Utils.isTrue(90, 100)) {
                reward.options.add(new ItemOption(93, Utils.nextInt(1, 5)));
            }
        } else if (r < 40) {
            reward = new Item(ItemName.MU_COI);
            reward.setDefaultOptions();
        } else if (r < 50) {
            reward = new Item(ItemName.SEN_HONG);
            reward.setDefaultOptions();
        } else if (r < 60) {
            reward = new Item(ItemName.NGOI_SAO_LAP_LANH);
            reward.setDefaultOptions();
        } else if (r < 75) {
            reward = new Item(ItemName.NU_HOA_SEN);
            reward.setDefaultOptions();
        } else if (r < 90) {
            reward = new Item(ItemName.TRAI_TIM_CHUNG_TAY);
            reward.setDefaultOptions();
        } else {
            reward = new Item(1994);
            reward.setDefaultOptions();
        }
        player.addItem(reward);
        player.service.sendThongBao("Bạn nhận được " + reward.template.name);
    }

    public static void selectLetter(Player player, int id) {
        
        boolean valid = false;
        for (int letter : ALL_LETTERS) {
            if (letter == id) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            return;
        }
        if (player.getCountEmptyBag() == 0) {
            player.service.dialogMessage("Hành trang đã đầy");
            return;
        }
        var item = player.getItemInBag(ItemName.RUONG_CHU_DUOC_CHON);
        player.removeItem(item.indexUI, 1);
        Item reward = new Item(id);
        reward.setDefaultOptions();
        player.addItem(reward);
        player.service.sendThongBao("Bạn nhận được " + reward.template.name);
    }

    public static void exchange(Player player, int type) {
        if (player == null) {
            return;
        }
        int[] required = ALL_LETTERS;
        int[] numbers = NUMBERS;
        Item[] items = new Item[required.length + (type == 2 ? numbers.length : 0)];
        int i = 0;
        for (int id : required) {
            Item it = player.getItemInBag(id);
            if (it == null ) {
                player.service.sendThongBao("Bạn chưa có đủ chữ");
                return;
            }
            items[i++] = it;
        }
        if (type == 2) {
            for (int id : numbers) {
                Item it = player.getItemInBag(id);
                if (it == null) {
                    player.service.sendThongBao("Bạn chưa có đủ chữ");
                    return;
                }
                items[i++] = it;
            }
        }
        long needGold = type == 1 ? 500_000_000L : 3_000_000_000L;
        if (player.gold < needGold) {
            player.service.sendThongBao("Bạn không đủ vàng");
            return;
        }
        if (player.getCountEmptyBag() == 0) {
            player.service.dialogMessage("Hành trang đã đầy");
            return;
        }
        for (Item it : items) {
            player.removeItem(it.indexUI, 1);
        }
        player.addGold(-needGold);
        int rewardId = type == 1 ? ItemName.HOP_QUA_THUONG : ItemName.HOP_QUA_CAO_CAP;
        Item reward = new Item(rewardId);
        reward.setDefaultOptions();
        player.addItem(reward);
        player.service.sendThongBao("Bạn nhận được " + reward.template.name);
    }
}
