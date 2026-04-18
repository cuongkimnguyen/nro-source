package com.ngocrong.combine;

import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.util.Utils;

public class CapsuleVipProOption extends Combine {
    
    private static final long FEE_GOLD = 100_000_000L;
    private static final int REQUIRE_STONE = 1;
    
    public CapsuleVipProOption() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang\n");
        sb.append("Chọn Capsule VIP PRO và Đá thạch ma\n");
        sb.append("Sau đó chọn 'Mở chỉ số'");
        setInfo(sb.toString());
        setInfo2("Thay đổi chỉ số Capsule VIP PRO");
    }
    
    @Override
    public void confirm() {
        if (itemCombine == null || itemCombine.size() != 2) {
            player.service.dialogMessage("Cần Capsule VIP PRO và 1 Đá thạch ma");
            return;
        }
        Item capsule = null;
        Item stone = null;
        for (byte idx : itemCombine) {
            Item it = player.itemBag[idx];
            if (it != null) {
                if (it.template.id == ItemName.CAPSULE_VIPPRO) {
                    capsule = it;
                }
                if (it.template.id == ItemName.DA_THACH_MA) {
                    stone = it;
                }
            }
        }
        if (capsule == null || stone == null) {
            player.service.dialogMessage("Cần Capsule VIP PRO và 1 Đá thạch ma");
            return;
        }
        if (player.gold < FEE_GOLD) {
            player.service.dialogMessage("Không đủ 100tr vàng");
            return;
        }
        player.menus.clear();
        player.menus.add(new KeyValue(CMDMenu.COMBINE, "Đồng ý"));
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
        player.service.openUIConfirm(npc.templateId,
                "Thay đổi chỉ số Capsule VIP PRO", npc.avatar, player.menus);
    }
    
    @Override
    public void combine() {
        if (itemCombine == null || itemCombine.size() != 2) {
            return;
        }
        Item capsule = null;
        Item stone = null;
        for (byte idx : itemCombine) {
            Item it = player.itemBag[idx];
            if (it != null) {
                if (it.template.id == ItemName.CAPSULE_VIPPRO) {
                    capsule = it;
                }
                if (it.template.id == ItemName.DA_THACH_MA) {
                    stone = it;
                }
            }
        }
        if (capsule == null || stone == null || stone.quantity < REQUIRE_STONE) {
            player.service.dialogMessage("Cần Capsule VIP PRO và 1 Đá thạch ma");
            return;
        }
        if (player.gold < FEE_GOLD) {
            player.service.sendThongBao("Không đủ 100tr vàng");
            return;
        }
        player.subGold(FEE_GOLD);
        player.removeItem(stone.indexUI, REQUIRE_STONE);
        int[] opts = {77, 103, 50, 94, 5};
        int optId = opts[Utils.nextInt(opts.length)];
        int param = Utils.nextInt(1, 5);
        boolean found = !capsule.options.isEmpty();
        var options = new ItemOption(optId, param);
        if (!found) {
            capsule.addItemOption(options);
        } else {
            capsule.options.set(0, options);
        }
        player.service.setItemBag();
        result((byte) 2);
        update();
    }
}
