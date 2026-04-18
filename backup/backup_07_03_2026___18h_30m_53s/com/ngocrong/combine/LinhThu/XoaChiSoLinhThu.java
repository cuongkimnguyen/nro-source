/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.combine.LinhThu;

import com.ngocrong.combine.Combine;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.KeyValue;

public class XoaChiSoLinhThu extends Combine {

    private static final int[] REMOVE_OPTION_IDS = {234, 235, 236, 107, 102};

    public XoaChiSoLinhThu() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang\n");
        sb.append("Chọn Linh thú bậc 2\n");
        sb.append("và Đá GALLERY\n");
        sb.append("Sau đó chọn 'Xóa chỉ số'");
        setInfo(sb.toString());

        setInfo2("Ta sẽ xóa chỉ số linh thú giúp ngươi");
    }

    @Override
    public void confirm() {
        if (itemCombine == null) {
            return;
        }
        Item pet = null;
        Item stone = null;
        for (byte idx : itemCombine) {
            Item item = player.itemBag[idx];
            if (item != null) {
                if (item.template.type == Item.TYPE_PET_BAY_BAC_2) {
                    pet = item;
                } else if (item.id == ItemName.DA_GALLERY) {
                    stone = item;
                }
            }
        }
        if (pet == null) {
            showCancel("Không tìm thấy Linh thú bậc 2");
            return;
        }
        if (stone == null) {
            showCancel("Không tìm thấy Đá gallery");
            return;
        }
        if (!hasOption(pet)) {
            showCancel("Linh thú không có chỉ số cần xóa");
            return;
        }
        String info = "Cần Linh thú bậc 2 và Đá gallery";
        player.menus.clear();
        player.menus.add(new KeyValue(CMDMenu.COMBINE, "Xóa chỉ số"));
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
        player.service.openUIConfirm(npc.templateId, info, npc.avatar, player.menus);
    }

    @Override
    public void combine() {
        if (itemCombine == null) {
            return;
        }
        Item pet = null;
        Item stone = null;
        for (byte idx : itemCombine) {
            Item item = player.itemBag[idx];
            if (item != null) {
                if (item.template.type == Item.TYPE_PET_BAY_BAC_2) {
                    pet = item;
                } else if (item.id == ItemName.DA_GALLERY) {
                    stone = item;
                }
            }
        }
        if (pet == null || stone == null || stone.quantity < 1 || !hasOption(pet)) {
            player.service.sendThongBao("Vật phẩm không đủ hoặc không có chỉ số cần xóa");
            return;
        }

        player.removeItem(stone.indexUI, 1);
        for (int id : REMOVE_OPTION_IDS) {
            ItemOption op = pet.getItemOption(id);
            if (op != null) {
                pet.options.remove(op);
            }
        }
        player.service.refreshItem((byte) 1, pet);
        result((byte) 2);
        update();
    }

    private boolean hasOption(Item pet) {
        for (int id : new int[]{234, 235, 236}) {
            if (pet.getItemOption(id) != null) {
                return true;
            }
        }
        return false;
    }
}

