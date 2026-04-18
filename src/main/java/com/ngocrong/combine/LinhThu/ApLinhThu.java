package com.ngocrong.combine.LinhThu;

import com.ngocrong.combine.Combine;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.util.Utils;

public class ApLinhThu extends Combine {

    private static final int REQUIRE_SOUL = 200;
    private static final int[] PET_IDS = {ItemName.DREAMLET, ItemName.FIRENIX, ItemName.BONG_BAY, ItemName.GHOST};
    private static final int[] OPTION_IDS = {77, 103, 50, 95, 96};

    public ApLinhThu() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang\n");
        sb.append("Chọn Quả trứng linh thú\n");
        sb.append(String.format("và %d Hồn linh thú\n", REQUIRE_SOUL));
        sb.append("Sau đó chọn 'Ấp'");
        setInfo(sb.toString());

        setInfo2("Ta sẽ ấp trứng giúp ngươi");
    }

    @Override
    public void showTab() {
        player.service.combine((byte) 0, this, (short) -1, (short) -1);
    }

    @Override
    public void confirm() {
        if (itemCombine == null) {
            return;
        }
        Item egg = null;
        Item soul = null;
        for (byte idx : itemCombine) {
            Item item = player.itemBag[idx];
            if (item != null) {
                if (item.id == ItemName.QUA_TRUNG_LINH_THU) {
                    egg = item;
                } else if (item.id == ItemName.HON_LINH_THU) {
                    soul = item;
                }
            }
        }
        String info = String.format("Cần 1 Quả trứng linh thú và %d Hồn linh thú", REQUIRE_SOUL);
        boolean ok = egg != null && egg.quantity >= 1 && soul != null && soul.quantity >= REQUIRE_SOUL;
        player.menus.clear();
        if (ok) {
            player.menus.add(new KeyValue(CMDMenu.COMBINE, "Ấp"));
        }
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
        player.service.openUIConfirm(npc.templateId, info, npc.avatar, player.menus);
    }

    @Override
    public void combine() {
        if (itemCombine == null) {
            return;
        }
        Item egg = null;
        Item soul = null;
        for (byte idx : itemCombine) {
            Item item = player.itemBag[idx];
            if (item != null) {
                if (item.id == ItemName.QUA_TRUNG_LINH_THU) {
                    egg = item;
                } else if (item.id == ItemName.HON_LINH_THU) {
                    soul = item;
                }
            }
        }
        if (egg == null || soul == null || soul.quantity < REQUIRE_SOUL) {
            player.service.sendThongBao("Vật phẩm không đủ");
            return;
        }
        player.removeItem(egg.indexUI, 1);
        player.removeItem(soul.indexUI, REQUIRE_SOUL);

        int petId = PET_IDS[Utils.nextInt(PET_IDS.length)];
        Item pet = new Item(petId);
        pet.quantity = 1;
        for (int i = 0; i < OPTION_IDS.length; i++) {
            int optId = OPTION_IDS[i];
            int param = Utils.nextInt(1, 10);
            pet.options.add(new ItemOption(optId, param));
        }

        result((byte) 5, pet.template.iconID);
        player.addItem(pet);
        update();
    }
}
