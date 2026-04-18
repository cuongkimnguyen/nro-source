package com.ngocrong.combine;

import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.util.Utils;
import com.ngocrong.consts.CMDMenu;

public class CapsuleVipPro extends Combine {

    private static final int REQUIRE_STARFISH = 1000;
    private static final int REQUIRE_FRAGMENT = 100;
    private static final int REQUIRE_GOLDBAR = 50;
    private static final int PERCENT = 100;

    public CapsuleVipPro() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang\n");
        sb.append("Chọn 1000 Sao biển, 100 Mảnh Capsule VIPPRO và 50 Thỏi vàng\n");
        sb.append("Sau đó chọn 'Nâng cấp'");
        setInfo(sb.toString());

        setInfo2("Nâng cấp Capsule VIP PRO");
    }

    @Override
    public void confirm() {
        if (itemCombine == null || itemCombine.size() != 3) {
            player.service.dialogMessage("Cần đủ 1000 Sao biển, 50 Mảnh Capsule VIPPRO và 50 Thỏi vàng");
            return;
        }
        Item star = null;
        Item frag = null;
        Item bar = null;
        for (byte idx : itemCombine) {
            Item itm = player.itemBag[idx];
            if (itm != null) {
                if (itm.template.id == ItemName._SAO_BIEN) {
                    star = itm;
                }
                if (itm.template.id == ItemName.MANH_CAPSULE_VIPPRO) {
                    frag = itm;
                }
                if (itm.template.id == ItemName.THOI_VANG) {
                    bar = itm;
                }
            }
        }
        if (star == null || frag == null || bar == null
                || star.quantity < REQUIRE_STARFISH || frag.quantity < REQUIRE_FRAGMENT || bar.quantity < REQUIRE_GOLDBAR) {
            player.service.dialogMessage("Cần đủ 1000 Sao biển, 100 Mảnh Capsule VIPPRO và 50 Thỏi vàng");
            return;
        }
        String text = String.format("Tỉ lệ thành công: %d%%", PERCENT);
        player.menus.clear();
        player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng cấp"));
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
        player.service.openUIConfirm(npc.templateId, text, npc.avatar, player.menus);
    }

    @Override
    public void combine() {
        if (itemCombine == null || itemCombine.size() != 3) {
            return;
        }
        Item star = null;
        Item frag = null;
        Item bar = null;
        for (byte idx : itemCombine) {
            Item itm = player.itemBag[idx];
            if (itm != null) {
                if (itm.template.id == ItemName._SAO_BIEN) {
                    star = itm;
                }
                if (itm.template.id == ItemName.MANH_CAPSULE_VIPPRO) {
                    frag = itm;
                }
                if (itm.template.id == ItemName.THOI_VANG) {
                    bar = itm;
                }
            }
        }
        if (star == null || frag == null || bar == null
                || star.quantity < REQUIRE_STARFISH || frag.quantity < REQUIRE_FRAGMENT || bar.quantity < REQUIRE_GOLDBAR) {
            player.service.dialogMessage("Cần đủ 1000 Sao biển, 100 Mảnh Capsule VIPPRO và 50 Thỏi vàng");
            return;
        }
        player.removeItem(star.indexUI, REQUIRE_STARFISH);
        player.removeItem(frag.indexUI, REQUIRE_FRAGMENT);
        player.removeItem(bar.indexUI, REQUIRE_GOLDBAR);
        if (Utils.isTrue(PERCENT, 100)) {
            Item vip = new Item(ItemName.CAPSULE_VIPPRO);
            vip.setDefaultOptions();
            vip.quantity = 1;
            player.addItemBag(vip);
            result((byte) 2);
        } else {
            result((byte) 3);
        }
        player.service.setItemBag();
        update();
    }
}
