/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.combine;

import com.ngocrong.consts.CMDMenu;
import com.ngocrong.item.Item;
import com.ngocrong.lib.KeyValue;

/**
 *
 * @author Administrator
 */
public class TachDoKichHoat extends Combine {

    public TachDoKichHoat() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang").append("\n");
        sb.append("Chọn 1 món đồ Kich hoạt").append("\n");
        sb.append("Sau đó chọn 'Tách Đồ'");
        setInfo(sb.toString());

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Ta sẽ phù phép").append("\n");
        sb2.append("Biến đồ Kích hoạt thành Đá nâng cấp").append("\n");
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
        if (item == null || item.template.type >= 5) {
            player.service.dialogMessage("Vật phẩm không hợp lệ");
            return;
        }
        if (!item.isDoKH()) {
            player.service.dialogMessage("Chỉ có thể tách đồ kích hoạt");
            return;
        }
        String info = "Sau khi đồng ý, sẽ nhận được 1 viên Đá nâng cấp Level 5 ";
        player.menus.clear();
        player.menus.add(new KeyValue(CMDMenu.COMBINE, "Đồng ý"));
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
        player.service.openUIConfirm(npc.templateId, info, npc.avatar, player.menus);
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
        if (item == null || item.template.type >= 5) {
            player.service.dialogMessage("Vật phẩm không hợp lệ");
            return;
        }
        if (!item.isDoKH()) {
            player.service.dialogMessage("Chỉ có thể tách đồ kích hoạt");
            return;
        }
        player.removeItem(item.indexUI, 1);
        Item dnc = new Item(2203);
        dnc.quantity = 1;
        dnc.setDefaultOptions();
        player.addItem(dnc);
        result((byte) 5, dnc.template.iconID);
        update();
    }
}
