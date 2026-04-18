package com.ngocrong.NQMP.Event;

import com.ngocrong.consts.*;
import com.ngocrong.item.Item;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.model.Npc;
import com.ngocrong.server.DropRateService;
import com.ngocrong.server.SessionManager;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class CauCa {

    private static final int[] MAPS = {5, 29};

    public static boolean useItem(Player player, Item item) {
        if (item.id != ItemName.MOI_CAU_THUONG && item.id != ItemName.MOI_CAU_VIP) {
            return false;
        }
        boolean mapOk = false;
        for (int id : MAPS) {
            if (player.zone.map.mapID == id) {
                mapOk = true;
                break;
            }
        }
        if (!mapOk) {
            player.service.sendThongBao("Không thể câu cá ở đây");
            return true;
        }
        if (player.getIndexBagById(ItemName.CAN_CAU) == -1) {
            player.service.sendThongBao("Cần có Cần câu trong hành trang");
            return true;
        }
        if (player.exitsItemTime(ItemTimeName.CAU_CA)) {
            player.service.sendThongBao("Bạn đang câu cá");
            return true;
        }
        player.removeItem(item.indexUI, 1);
        final int oldBag = player.bag;
        player.bag = 115;
        player.zone.service.updateBag(player);
        Item cane = player.getItemInBag(ItemName.CAN_CAU);
        int icon = cane != null ? cane.template.iconID : 0;
        player.setItemTime(ItemTimeName.CAU_CA, icon, false, 15);
        boolean vip = item.id == ItemName.MOI_CAU_VIP;
        Utils.setTimeout(() -> endFishing(player, vip, (byte) oldBag), 15000);
        return true;
    }

    private static void endFishing(Player player, boolean vip, byte oldBag) {
        if (player == null || player.zone == null) {
            return;
        }
        player.setBag(oldBag);
        player.zone.service.updateBag(player);
        int rate = DropRateService.getFishBiteRate(vip);
        if (!Utils.isTrue(rate, 100)) {
            player.service.sendThongBao("Không có cá cắn câu");
            return;
        }
        int fishIdx = randomByRates(DropRateService.getFishTypeRates());
        int fishId;
        switch (fishIdx) {
            case 0:
                fishId = ItemName.CA_DIEU_HONG;
                break;
            case 1:
                fishId = ItemName.CA_NOC;
                break;
            case 2:
                fishId = ItemName.CA_MAP;
                break;
            case 3:
                fishId = ItemName.BACH_TUOC_TIM;
                break;
            default:
                return;
        }
        Item fish = new Item(fishId);
        fish.setDefaultOptions();
        player.addItem(fish);
        player.service.sendThongBao("Bạn câu được : " + fish.template.name);
        if (fishId == ItemName.CA_MAP) {
            SessionManager.chatVip(String.format("%s: đã câu được Cá Mập , nhận ngay phần thường lớn.", player.name));
        }
    }

    private static int randomByRates(int[] rates) {
        int total = Arrays.stream(rates).sum();
        int rnd = Utils.nextInt(total);
        int sum = 0;
        for (int i = 0; i < rates.length; i++) {
            sum += rates[i];
            if (rnd < sum) {
                return i;
            }
        }
        return -1;
    }

    public static void openMenu(Player player) {
        Npc npc = player.zone.findNpcByID(NpcName.QUY_LAO_KAME);
        ArrayList<KeyValue> menus = new ArrayList<>();
        menus.add(new KeyValue(1196, "1 cá diêu hồng"));
        menus.add(new KeyValue(1197, "3 cá diêu hồng"));
        menus.add(new KeyValue(1198, "1 cá nóc"));
        menus.add(new KeyValue(1199, "1 bạch tuộc tím"));
        menus.add(new KeyValue(1200, "1 cá mập"));
        menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
        int avatar = npc != null ? npc.avatar : 0;
        player.service.openUIConfirm(NpcName.QUY_LAO_KAME, "Con muốn đổi cá lấy vật phẩm nào?", (short) avatar, menus);
    }

    public static void exchange(Player player, int action) {
        switch (action) {
            case 1196: {
                Item fish = player.getItemInBag(ItemName.CA_DIEU_HONG);
                if (fish == null) {
                    player.service.sendThongBao("Bạn không có cá diêu hồng");
                    return;
                }
                player.removeItem(fish.indexUI, 1);
                int r = Utils.nextInt(100);
                int quantity;
                if (r < 60) {
                    quantity = Utils.nextInt(1, 3);
                } else if (r < 95) {
                    quantity = Utils.nextInt(3, 5);
                } else {
                    quantity = Utils.nextInt(5, 10);
                }
                Item vang = new Item(ItemName.THOI_VANG);
                vang.quantity = quantity;
                player.addItem(vang);
                player.service.sendThongBao("Bạn nhận được x" + vang.quantity + " " + vang.template.name);
                break;
            }
            case 1197: {
                Item fish = player.getItemInBag(ItemName.CA_DIEU_HONG);
                if (fish == null || fish.quantity < 3) {
                    player.service.sendThongBao("Không đủ cá diêu hồng");
                    return;
                }
                player.removeItem(fish.indexUI, 3);
                int r = Utils.nextInt(100);
                int id;
                if (r < 70) {
                    id = ItemName.NGOC_RONG_4_SAO + Utils.nextInt(4);
                } else if (r < 95) {
                    id = ItemName.NGOC_RONG_2_SAO + Utils.nextInt(3);
                } else {
                    id = ItemName.NGOC_RONG_1_SAO + Utils.nextInt(4);
                }
                Item db = new Item(id);
                db.quantity = 1;
                player.addItem(db);
                player.service.sendThongBao("Bạn nhận được x" + db.quantity + " " + db.template.name);
                break;
            }
            case 1198: {
                Item fish = player.getItemInBag(ItemName.CA_NOC);
                if (fish == null) {
                    player.service.sendThongBao("Bạn không có cá nóc");
                    return;
                }
                player.removeItem(fish.indexUI, 1);
                for (int i = 0; i < 3; i++) {
                    int r = Utils.nextInt(100);
                    int id = r < 70 ? Utils.nextInt(381, 385) : Utils.nextInt(1021, 1023);
                    Item it = new Item(id);
                    it.quantity = 1;
                    player.addItem(it);
                    player.service.sendThongBao("Bạn nhận được x" + it.quantity + " " + it.template.name);
                }
                break;
            }
            case 1199: {
                Item fish = player.getItemInBag(ItemName.BACH_TUOC_TIM);
                if (fish == null) {
                    player.service.sendThongBao("Bạn không có bạch tuộc tím");
                    return;
                }
                player.removeItem(fish.indexUI, 1);
                int r = Utils.nextInt(100);
                int quantity;
                if (r < 60) {
                    quantity = Utils.nextInt(1, 3);
                } else if (r < 95) {
                    quantity = Utils.nextInt(3, 5);
                } else {
                    quantity = Utils.nextInt(5, 10);
                }
                Item hlt = new Item(ItemName.HON_LINH_THU);
                hlt.quantity = quantity;
                player.addItem(hlt);
                player.service.sendThongBao("Bạn nhận được x" + hlt.quantity + " " + hlt.template.name);

                break;
            }
            case 1200: {
                Item fish = player.getItemInBag(ItemName.CA_MAP);
                if (fish == null) {
                    player.service.sendThongBao("Bạn không có cá mập");
                    return;
                }
                player.removeItem(fish.indexUI, 1);
                boolean divine = randomByRates(DropRateService.getFishSharkRates()) == 0;
                int type = randomByRates(DropRateService.getFishItemRates());
                int itemId;
                if (divine) {
                    int[] set = {ItemName.AO_THAN_LINH, ItemName.QUAN_THAN_LINH, ItemName.GANG_THAN_LINH, ItemName.GIAY_THAN_LINH, ItemName.NHAN_THAN_LINH};
                    itemId = set[type];
                } else {
                    int[][] set = {
                        {ItemName.AO_HUY_DIET_TD, ItemName.QUAN_HUY_DIET_TD, ItemName.GANG_HUY_DIET_TD, ItemName.GIAY_HUY_DIET_TD, ItemName.NHAN_HUY_DIET},
                        {ItemName.AO_HUY_DIET_NM, ItemName.QUAN_HUY_DIET_NM, ItemName.GANG_HUY_DIET_NM, ItemName.GIAY_HUY_DIET_NM, ItemName.NHAN_HUY_DIET},
                        {ItemName.AO_HUY_DIET_XD, ItemName.QUAN_HUY_DIET_XD, ItemName.GANG_HUY_DIET_XD, ItemName.GIAY_HUY_DIET_XD, ItemName.NHAN_HUY_DIET}
                    };
                    itemId = set[player.gender][type];
                }
                Item it = new Item(itemId);
                it.quantity = 1;
                it.setDefaultOptions();
                player.addItem(it);
                player.service.sendThongBao("Bạn nhận được x" + it.quantity + " " + it.template.name);

                break;
            }
        }
    }
}
