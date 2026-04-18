/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.combine;

import com.ngocrong.consts.CMDMenu;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.util.Utils;

/**
 *
 * @author Administrator
 */
public class DoiDoHuyDiet extends Combine {

    static final int goldRequire = 500_000_000;

    public DoiDoHuyDiet() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang").append("\n");
        sb.append("Chọn 1 món đồ Thần Linh").append("\n");
        sb.append("Sau đó chọn 'Đổi Đồ'");
        setInfo(sb.toString());

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Ta sẽ phù phép").append("\n");
        sb2.append("Biến đồ Thần Linh thành Đồ hủy diệt").append("\n");
        setInfo2(sb2.toString());
    }

    @Override
    public void confirm() {
        if (itemCombine == null) {
            return;
        }
        if (itemCombine.size() != 1) {
            player.service.dialogMessage("Số lượng trang bị không hợp lệ");
            return;
        }
        Item item = player.itemBag[itemCombine.get(0)];
        if (item == null || item.template.type >= 5 || item.template.level != 13) {
            player.service.dialogMessage("Vật phẩm không hợp lệ");
            return;
        }
        if (item.isDoKH()) {
            player.service.dialogMessage("Không thể cường hóa đồ Thần Linh Kích Hoạt");
            return;
        }
        String info = "Sau khi cường hóa, sẽ nhận được 1 trang bị Hủy Diệt ";
        player.menus.clear();
        player.menus.add(new KeyValue(CMDMenu.COMBINE, "Đồng ý\n500tr vàng"));
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
        player.service.openUIConfirm(npc.templateId, info, npc.avatar, player.menus);
    }

    static int getTempId(Item item) {
        int tempId = -1;
        int[] ao = new int[]{650, 652, 654};
        int[] quan = new int[]{651, 653, 655};
        int[] gang = new int[]{657, 659, 661};
        int[] giay = new int[]{658, 660, 662};
        int rd = 656;
        switch (item.template.type) {
            case 0:
                return ao[item.template.gender];
            case 1:
                return quan[item.template.gender];
            case 2:
                return gang[item.template.gender];
            case 3:
                return giay[item.template.gender];
            case 4:
                return rd;
        }
        return tempId;
    }

    @Override
    public void combine() {
        if (itemCombine == null) {
            return;
        }
        if (itemCombine.size() != 1) {
            player.service.dialogMessage("Số lượng trang bị không hợp lệ");
            return;
        }
        Item item = player.itemBag[itemCombine.get(0)];
        if (item == null || item.template.type >= 5 || item.template.level != 13) {
            player.service.dialogMessage("Vật phẩm không hợp lệ");
            return;
        }
        if (item.isDoTLKH()) {
            player.service.dialogMessage("Không thể cường hóa đồ Thần Linh Kích Hoạt");
            return;
        }
        if (player.gold < 500000000) {
            player.service.sendThongBao("Bạn không đủ vàng");
            return;
        }
        player.addGold(-500000000);

        item = new Item(getTempId(item));
        item.quantity = 1;
        item.setDefaultOptions();
        int[] optionBonus = new int[]{77, 103, 50, item.template.gender == 2 ? 94 : 5};
        item.addItemOption(new ItemOption(optionBonus[Utils.nextInt(optionBonus.length)], Utils.nextInt(1, 5)));
        item.addItemOption(new ItemOption(30, 0));
        item.indexUI = itemCombine.get(0);
        player.itemBag[item.indexUI] = item;
        player.service.refreshItem((byte) 1, item);
        update();
        result((byte) 2);
    }

    @Override
    public void showTab() {
        player.service.combine((byte) 0, this, (short) -1, (short) -1);
    }

}
