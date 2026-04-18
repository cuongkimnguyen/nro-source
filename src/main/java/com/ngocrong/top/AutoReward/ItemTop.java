/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.top.AutoReward;

import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.model.RandomItem;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.NpcName;
import com.ngocrong.server.Language;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;

/**
 *
 * @author Administrator
 */
public class ItemTop {

    public static ItemTop instance = new ItemTop();
    public void useItem(Item item, Player player) {
        if (item == null || player == null) {
            return;
        }
        switch (item.template.id) {
            case ItemName.RUONG_ITEM_CAP_1:
                openRuongItemCap1(item, player);
                break;
            case ItemName.RUONG_DA_NANG_CAP:
                openRuongDaNangCap(item, player);
                break;
            case ItemName.RUONG_ITEM_CAP_2:
                openRuongItemCap2(item, player);
                break;
            case ItemName.RUONG_NGOC_RONG:
                openRuongNgocRong(item, player);
                break;
            case ItemName.RUONG_1_MON_HUY_DIET:
                openRuong1HD(item, player);
                break;
            case ItemName.RUONG_SET_HUY_DIET:
                openRuongSetHD(item, player);
                break;
            case ItemName.RUONG_SET_KICH_HOAT:
                openRuongSetKH(item, player);
                break;
            case ItemName.RUONG_SKH_1_MON_NGAU_NHIEN:
                openRuongSKH1Mon(item, player);
                break;
            case ItemName.RUONG_1_MON_SKH:
                openRuong1MonSKH(item, player);
                break;
        }
    }

    public void openRuongItemCap1(Item chest, Player player) {
        if (player.getSlotNullInBag() < 1) {
            player.service.sendThongBao(Language.ME_BAG_FULL);
            return;
        }
        player.removeItem(chest.indexUI, 1);
        int id = RandomItem.ITEM_CAP_1.next();
        Item it = new Item(id);
        it.quantity = 1;
        it.setDefaultOptions();
        player.addItem(it);
        player.service.sendThongBao("Bạn nhận được vật phẩm từ rương");
    }

    public void openRuongDaNangCap(Item chest, Player player) {
        if (player.getSlotNullInBag() < 1) {
            player.service.sendThongBao(Language.ME_BAG_FULL);
            return;
        }
        player.removeItem(chest.indexUI, 1);
        int id = RandomItem.DA_NANG_CAP.next();
        Item it = new Item(id);
        it.quantity = 1;
        it.setDefaultOptions();
        player.addItem(it);
        player.service.sendThongBao("Bạn nhận được vật phẩm từ rương");
    }

    public void openRuongItemCap2(Item chest, Player player) {
        if (player.getSlotNullInBag() < 1) {
            player.service.sendThongBao(Language.ME_BAG_FULL);
            return;
        }
        player.removeItem(chest.indexUI, 1);
        int id = RandomItem.ITEM_CAP_2.next();
        Item it = new Item(id);
        it.quantity = 1;
        it.setDefaultOptions();
        player.addItem(it);
        player.service.sendThongBao("Bạn nhận được vật phẩm từ rương");
    }

    public void openRuongNgocRong(Item chest, Player player) {
        if (player.getSlotNullInBag() < 1) {
            player.service.sendThongBao(Language.ME_BAG_FULL);
            return;
        }
        player.removeItem(chest.indexUI, 1);
        int id = Utils.nextInt(16, 20);
        Item it = new Item(id);
        it.quantity = 1;
        it.setDefaultOptions();
        player.addItem(it);
        player.service.sendThongBao("Bạn nhận được vật phẩm từ rương");
    }

    private static final int[][] SET_HD = {
        {ItemName.AO_HUY_DIET_TD, ItemName.QUAN_HUY_DIET_TD, ItemName.GANG_HUY_DIET_TD,
            ItemName.GIAY_HUY_DIET_TD, ItemName.NHAN_HUY_DIET},
        {ItemName.AO_HUY_DIET_NM, ItemName.QUAN_HUY_DIET_NM, ItemName.GANG_HUY_DIET_NM,
            ItemName.GIAY_HUY_DIET_NM, ItemName.NHAN_HUY_DIET},
        {ItemName.AO_HUY_DIET_XD, ItemName.QUAN_HUY_DIET_XD, ItemName.GANG_HUY_DIET_XD,
            ItemName.GIAY_HUY_DIET_XD, ItemName.NHAN_HUY_DIET}
    };

    private static final int[][] SET_KH = {
        {ItemName.AO_VAI_3_LO, ItemName.QUAN_VAI_DEN, ItemName.GANG_VAI_DEN,
            ItemName.GIAY_NHUA, ItemName.RADA_CAP_1},
        {ItemName.AO_SOI_LEN, ItemName.QUAN_SOI_LEN, ItemName.GANG_SOI_LEN,
            ItemName.GIAY_SOI_LEN, ItemName.RADA_CAP_1},
        {ItemName.AO_VAI_THO, ItemName.QUAN_VAI_THO, ItemName.GANG_VAI_THO,
            ItemName.GIAY_VAI_THO, ItemName.RADA_CAP_1}
    };

    public void openRuong1HD(Item chest, Player player) {
        if (player.getSlotNullInBag() < 1) {
            player.service.sendThongBao(Language.ME_BAG_FULL);
            return;
        }
        player.menus.clear();
        player.menus.add(new KeyValue(CMDMenu.OPEN_RUONG_HUY_DIET_PLANET, "Trái Đất", chest.indexUI, chest.template.id, 0));
        player.menus.add(new KeyValue(CMDMenu.OPEN_RUONG_HUY_DIET_PLANET, "Namec", chest.indexUI, chest.template.id, 1));
        player.menus.add(new KeyValue(CMDMenu.OPEN_RUONG_HUY_DIET_PLANET, "Xayda", chest.indexUI, chest.template.id, 2));
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Đóng"));
        player.service.openUIConfirm(NpcName.CON_MEO, "Hãy chọn hành tinh bạn mong muốn ?", player.getPetAvatar(), player.menus);
    }

    public void openRuongSetHD(Item chest, Player player) {
        if (player.getSlotNullInBag() < SET_HD[0].length) {
            player.service.sendThongBao(Language.ME_BAG_FULL);
            return;
        }
        player.menus.clear();
        player.menus.add(new KeyValue(CMDMenu.OPEN_RUONG_HUY_DIET_PLANET, "Trái Đất", chest.indexUI, chest.template.id, 0));
        player.menus.add(new KeyValue(CMDMenu.OPEN_RUONG_HUY_DIET_PLANET, "Namec", chest.indexUI, chest.template.id, 1));
        player.menus.add(new KeyValue(CMDMenu.OPEN_RUONG_HUY_DIET_PLANET, "Xayda", chest.indexUI, chest.template.id, 2));
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Đóng"));
        player.service.openUIConfirm(NpcName.CON_MEO, "Hãy chọn hành tinh bạn mong muốn ?", player.getPetAvatar(), player.menus);
    }

    public void openRuongSetKH(Item chest, Player player) {
        if (player.getSlotNullInBag() < SET_KH[0].length) {
            player.service.sendThongBao(Language.ME_BAG_FULL);
            return;
        }
        player.menus.clear();
        player.menus.add(new KeyValue(CMDMenu.OPEN_SET_KH_TD, "Trái Đất"));
        player.menus.add(new KeyValue(CMDMenu.OPEN_SET_KH_NM, "Namec"));
        player.menus.add(new KeyValue(CMDMenu.OPEN_SET_KH_XD, "Xayda"));
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Đóng"));
        player.service.openUIConfirm(NpcName.CON_MEO, "Hãy chọn hành tinh bạn mong muốn ?", player.getPetAvatar(), player.menus);
    }

    public void openRuongSKH1Mon(Item chest, Player player) {
        if (player.getSlotNullInBag() < 1) {
            player.service.sendThongBao(Language.ME_BAG_FULL);
            return;
        }
        player.menus.clear();
        player.menus.add(new KeyValue(CMDMenu.OPEN_RUONG_SKH_PLANET, "Trái Đất", chest.indexUI, 0));
        player.menus.add(new KeyValue(CMDMenu.OPEN_RUONG_SKH_PLANET, "Namec", chest.indexUI, 1));
        player.menus.add(new KeyValue(CMDMenu.OPEN_RUONG_SKH_PLANET, "Xayda", chest.indexUI, 2));
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Đóng"));
        player.service.openUIConfirm(NpcName.CON_MEO, "Hãy chọn hành tinh bạn mong muốn ?", player.getPetAvatar(), player.menus);
    }

    public void openRuong1MonSKH(Item chest, Player player) {
        if (player.getSlotNullInBag() < 1) {
            player.service.sendThongBao(Language.ME_BAG_FULL);
            return;
        }
        player.menus.clear();
        player.menus.add(new KeyValue(CMDMenu.OPEN_RUONG_1_MON_SKH_PLANET, "Trái Đất", chest.indexUI, 0));
        player.menus.add(new KeyValue(CMDMenu.OPEN_RUONG_1_MON_SKH_PLANET, "Namec", chest.indexUI, 1));
        player.menus.add(new KeyValue(CMDMenu.OPEN_RUONG_1_MON_SKH_PLANET, "Xayda", chest.indexUI, 2));
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Đóng"));
        player.service.openUIConfirm(NpcName.CON_MEO, "Hãy chọn hành tinh bạn mong muốn ?", player.getPetAvatar(), player.menus);
    }

    public void reward1HD(Player player, int planet, int optionID) {
        int[] set = SET_HD[planet];
        int temp = set[Utils.nextInt(set.length)];
        Item it = createHuyDiet(temp, optionID);
        player.addItem(it);
        player.service.sendThongBao("Bạn nhận được " + it.template.name);
    }

    public void rewardSetHD(Player player, int planet, int optionID) {
        int[] set = SET_HD[planet];
        if (player.getSlotNullInBag() < set.length) {
            player.service.sendThongBao(Language.ME_BAG_FULL);
            return;
        }
        for (int temp : set) {
            Item it = createHuyDiet(temp, optionID);
            player.addItem(it);
        }
        player.service.sendThongBao("Bạn nhận được set Hủy Diệt");
    }

    public void rewardSetKH(Player player, int planet, int skhId) {
        int[] set = SET_KH[planet];
        if (player.getSlotNullInBag() < set.length) {
            player.service.sendThongBao(Language.ME_BAG_FULL);
            return;
        }
        for (int temp : set) {
            Item it = new Item(temp);
            it.isLock = false;
            it.quantity = 1;
            it.setDefaultOptions();
            it.addOptionSKH(skhId);
            it.addItemOption(new ItemOption(30, 0));
            player.addItem(it);
        }
        player.service.sendThongBao("Bạn nhận được set Kích Hoạt");
    }

    public Item createHuyDiet(int tempID, int optionID) {
        Item item = new Item(tempID);
        item.setDefaultOptions();
        item.addRandomOption(15);
        item.options.add(new ItemOption(optionID,5));
        item.options.add(new ItemOption(30,0));
        return item;
    }

}
