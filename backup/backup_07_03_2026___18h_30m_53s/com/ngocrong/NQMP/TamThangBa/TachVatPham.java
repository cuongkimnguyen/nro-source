/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.NQMP.TamThangBa;

import com.ngocrong.combine.Combine;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.item.Item;
import com.ngocrong.lib.KeyValue;

/**
 *
 * @author Administrator
 */
public class TachVatPham extends Combine {

    public TachVatPham() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang").append("\n");
        sb.append("Chọn 1 Đeo lưng Ngọc rồng hoặc Hào Quang Ám Lang vĩnh viễn").append("\n");
        sb.append("Sau đó chọn 'Tách Đồ'");
        setInfo(sb.toString());

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Ta sẽ phù phép").append("\n");
        sb2.append("Biến cải trang thành mảnh đeo lưng").append("\n");
        setInfo2(sb2.toString());
    }

    @Override
    public void showTab() {
        super.showTab();
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
        if (item == null || (item.template.id != 2228 && item.template.id != 2230)) {
            player.service.dialogMessage("Vật phẩm không hợp lệ");
            return;
        }
        if (item.findOptions(93) != -1) {
            player.service.dialogMessage("Chỉ có thể tách cải trang vĩnh viễn");
            return;
        }
        String info = "Sau khi đồng ý, sẽ nhận được 1 Mảnh đeo lưng ";
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
        if (item == null || (item.template.id != 2228 && item.template.id != 2230)) {
            player.service.dialogMessage("Vật phẩm không hợp lệ");
            return;
        }
        if (item.findOptions(93) != -1) {
            player.service.dialogMessage("Chỉ có thể tách cải trang vĩnh viễn");
            return;
        }
        player.removeItem(item.indexUI, 1);
        Item manhdeolung = new Item(2227);
        manhdeolung.quantity = 1;
        manhdeolung.setDefaultOptions();
        player.addItem(manhdeolung);
        result((byte) 5, manhdeolung.template.iconID);
        update();
    }

}
