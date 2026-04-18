package com.ngocrong.NQMP.Event;

import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemOption;
import com.ngocrong.mob.Mob;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;

public class NuocMiaEvent {

    public static void mobReward(Player player,Mob mob) {

        if (player == null || player.zone == null) {
            return;
        }
        int id = -1;
        if (player.zone.map.isCold() || player.zone.map.isFuture()) {
            id = ItemName.DA_LANH;
        } else if (player.zone.map.isNappa()) {
            id = ItemName.TRAI_TAC;
        } else if (player.zone.map.isNormalMap() || player.zone.map.isBroly()) {
            id = ItemName.KHUC_MIA;
        }
        if (id == -1) {
            return;
        }
        if (Utils.isTrue(1, 150)) {
            Item it = new Item(id);
            it.setDefaultOptions();
            it.quantity = 1;
            ItemMap map = new ItemMap(player.zone.autoIncrease++);
            map.item = it;
            map.playerID = Math.abs(player.id);
            map.x = player.getX();
            map.y = player.zone.map.collisionLand(player.getX(), player.getY());
            player.zone.addItemMap(map);
            mob.items.add(map);
        }
    }

    public static void combine(Player player, byte type) {
        if (player == null) {
            return;
        }
        if (player.getCountEmptyBag() == 0) {
            player.service.dialogMessage("Hành trang đã đầy");
            return;
        }
        Item da = player.getItemInBag(ItemName.DA_LANH);
        Item mia = player.getItemInBag(ItemName.KHUC_MIA);
        Item tac = player.getItemInBag(ItemName.TRAI_TAC);
        Item thoivang = player.getItemInBag(ItemName.THOI_VANG);
        if (type == 1) {
            if (da == null || mia == null || tac == null || da.quantity < 1 || mia.quantity < 5 || tac.quantity < 1 || player.gold < 200_000_000L) {
                player.service.sendThongBao("Bạn không đủ nguyên liệu");
                return;
            }
            player.removeItem(da.indexUI, 1);
            player.removeItem(mia.indexUI, 5);
            player.removeItem(tac.indexUI, 1);
            player.subGold(200_000_000L);
            Item item = new Item(ItemName.NUOC_MIA_SIZE_M);
            item.setDefaultOptions();
            player.addItem(item);
            player.service.sendThongBao("Bạn nhận được " + item.template.name);
        } else if (type == 2) {
            if (da == null || mia == null || tac == null || thoivang == null || da.quantity < 5 || mia.quantity < 10 || tac.quantity < 3 || thoivang.quantity < 5) {
                player.service.sendThongBao("Bạn không đủ nguyên liệu");
                return;
            }
            player.removeItem(da.indexUI, 5);
            player.removeItem(mia.indexUI, 10);
            player.removeItem(tac.indexUI, 3);
            player.removeItem(thoivang.indexUI, 5);
            Item item = new Item(ItemName.NUOC_MIA_SIZE_XXL);
            item.setDefaultOptions();
            player.addItem(item);
            player.service.sendThongBao("Bạn nhận được " + item.template.name);
        }
    }

    public static boolean useItem(Player player, Item item) {
        if (player == null || item == null) {
            return false;
        }
        if (player.getCountEmptyBag() == 0) {
            return false;
        }
        if (item.template.id == ItemName.NUOC_MIA_SIZE_M) {
            player.removeItem(item.indexUI, 1);
            openM(player);
            return true;
        } else if (item.template.id == ItemName.NUOC_MIA_SIZE_XXL) {
            player.removeItem(item.indexUI, 1);
            openXXL(player);
            return true;
        }
        return false;
    }

    private static void openM(Player p) {
        if (p.getCountEmptyBag() == 0) {
            p.service.dialogMessage("Hành trang đã đầy");
            return;
        }
        int r = Utils.nextInt(100);
//        p.pointNuocMiaM++;
//        p.isChangePoint = true;
        Item reward;
        if (r < 10) {
            reward = new Item(ItemName.CAI_TRANG_1);
            reward.options.add(new ItemOption(77, Utils.nextInt(30, 35)));
            reward.options.add(new ItemOption(103, Utils.nextInt(30, 35)));
            reward.options.add(new ItemOption(50, Utils.nextInt(30, 35)));
            if (!Utils.isTrue(10, 100)) {
                reward.options.add(new ItemOption(93, 1));
            }
        } else if (r < 20) {
            int[] ct = {876, 877, 875,};
            reward = new Item(ct[p.gender]);
            reward.setDefaultOptions();
            if (!Utils.isTrue(10, 100)) {
                reward.options.add(new ItemOption(93, 1));
            }
        } else if (r < 40) {
            reward = new Item(ItemName.VAT_PHAM_KHANG_XINBATO);
        } else if (r < 50) {
            int[] arr = {381, 382, 383, 384, 385};
            reward = new Item(arr[Utils.nextInt(arr.length)]);
            reward.setDefaultOptions();
        } else if (r < 70) {
            reward = new Item(Utils.nextInt(ItemName.NGOC_RONG_4_SAO, ItemName.NGOC_RONG_7_SAO));
            reward.setDefaultOptions();
        } else if (r < 80) {
            reward = new Item(ItemName.NUOC_GIAI_KHAT);
        } else if (r < 90) {
            reward = new Item(ItemName.DEO_LUNG_SUNG_CA_MAP);
            reward.options.add(new ItemOption(14, 10));
            reward.options.add(new ItemOption(95, 10));
            reward.options.add(new ItemOption(96, 10));
            if (!Utils.isTrue(5, 100)) {
                reward.options.add(new ItemOption(93, Utils.isTrue(1, 3) ? 3 : 1));
            }
        } else {
            reward = new Item(ItemName.DEO_LUNG_SUNG_CA_MAP);
            reward.options.add(new ItemOption(101, 20));
            reward.options.add(new ItemOption(95, 10));
            reward.options.add(new ItemOption(96, 10));
            if (!Utils.isTrue(5, 100)) {
                reward.options.add(new ItemOption(93, Utils.isTrue(1, 3) ? 3 : 1));
            }
        }
        p.addItem(reward);
        p.service.sendThongBao("Bạn nhận được " + reward.template.name);
    }

    private static void openXXL(Player p) {
        if (p.getCountEmptyBag() == 0) {
            p.service.dialogMessage("Hành trang đã đầy");
            return;
        }
        int r = Utils.nextInt(100);
        Item reward;
//        p.pointNuocMiaXXL++;
//        p.isChangePoint = true;
        if (r < 10) {
            reward = new Item(ItemName.NUOC_MIA_SAU_RIENG);
        } else if (r < 20) {
            reward = new Item(ItemName.NUOC_MIA_THOM);
        } else if (r < 30) {
            reward = new Item(ItemName.NUOC_MIA_KHONG_LO);
        } else if (r < 40) {
            reward = new Item(ItemName.NUOC_MIA_XOAI);
        } else if (r < 50) {
            reward = new Item(ItemName.NUOC_MIA_TAC);
        } else if (r < 60) {
            int[] arr = {1021, 1022, 1023};
            reward = new Item(arr[Utils.nextInt(arr.length)]);
            reward.setDefaultOptions();
        } else if (r < 75) {
            reward = new Item(2114);
            reward.options.add(new ItemOption(77, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(103, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(50, Utils.nextInt(4, 8)));
            if (!Utils.isTrue(5, 100)) {
                reward.options.add(new ItemOption(93, Utils.isTrue(1, 3) ? 3 : 1));
            }
        } else if (r < 90) {
            reward = new Item(2114);
            reward.options.add(new ItemOption(77, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(103, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(50, Utils.nextInt(4, 8)));
            reward.options.add(new ItemOption(5, 5));
            if (!Utils.isTrue(5, 100)) {
                reward.options.add(new ItemOption(93, Utils.isTrue(1, 3) ? 3 : 1));
            }
        } else {
            reward = new Item(ItemName.NGOC_RONG_3_SAO);
            reward.setDefaultOptions();
        }
        p.addItem(reward);
        p.service.sendThongBao("Bạn nhận được " + reward.template.name);
    }
}
