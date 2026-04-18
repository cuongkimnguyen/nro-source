package com.ngocrong.combine.LinhThu;

import com.ngocrong.combine.Combine;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.util.Utils;

public class NangBacLinhThu extends Combine {

    public NangBacLinhThu() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang\n");
        sb.append("Chọn Linh thú bậc 1 cấp 7\n");
        sb.append("và Thăng tinh thạch\n");
        sb.append("và Hồn linh thú\n");
        sb.append("Sau đó chọn 'Nâng cấp'");
        setInfo(sb.toString());

        setInfo2("Ta sẽ nâng bậc linh thú giúp ngươi");
    }
    private static final int REQUIRE_STONE = 50; // Thăng tinh thạch
    private static final int REQUIRE_SOUL = 100; // Hồn linh thú
    private static final double SUCCESS_RATE = 50d;

    @Override
    public void confirm() {
        if (itemCombine == null) {
            return;
        }
        Item pet = null;
        Item stone = null;
        Item soul = null;
        for (byte idx : itemCombine) {
            Item item = player.itemBag[idx];
            if (item != null) {
                if (item.template.type == Item.TYPE_PET_BAY_BAC_1) {
                    pet = item;
                } else if (item.id == ItemName.THANG_TINH_THACH) {
                    stone = item;
                } else if (item.id == ItemName.HON_LINH_THU) {
                    soul = item;
                }
            }
        }
        if (pet == null || getLevel(pet) < 7) {
            showCancel("Cần 1 Linh thú bậc 1 cấp 7");
            return;
        }
        if (stone == null) {
            showCancel("Không tìm thấy Thăng tinh thạch");
            return;
        }
        if (soul == null) {
            showCancel("Không tìm thấy hồn linh thú");
            return;
        }
        String info = String.format("Cần Linh thú bậc 1 cấp 7, %d Thăng tinh thạch và %d Hồn linh thú", REQUIRE_STONE, REQUIRE_SOUL);
        boolean ok = getLevel(pet) >= 7 && stone != null && stone.quantity >= REQUIRE_STONE && soul != null && soul.quantity >= REQUIRE_SOUL;
        player.menus.clear();
        if (ok) {
            player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng bậc"));
        }
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
        Item soul = null;
        for (byte idx : itemCombine) {
            Item item = player.itemBag[idx];
            if (item != null) {
                if (item.template.type == Item.TYPE_PET_BAY_BAC_1) {
                    pet = item;
                } else if (item.id == ItemName.THANG_TINH_THACH) {
                    stone = item;
                } else if (item.id == ItemName.HON_LINH_THU) {
                    soul = item;
                }
            }
        }
        if (pet == null || stone == null || soul == null || getLevel(pet) < 7 || stone.quantity < REQUIRE_STONE || soul.quantity < REQUIRE_SOUL) {
            player.service.sendThongBao("Vật phẩm không đủ");
            return;
        }

        player.removeItem(stone.indexUI, REQUIRE_STONE);
        player.removeItem(soul.indexUI, REQUIRE_SOUL);

        boolean success = Utils.isTrue(SUCCESS_RATE, 100d);
        if (success) {
            player.removeItem(pet.indexUI, 1);
            int newId = pet.id + 4;
            Item newPet = new Item(newId);
            newPet.quantity = 1;
            for (ItemOption op : pet.options) {
                int id = op.optionTemplate.id;
                if (id != 72 && (id < 227 || id > 233)) {
                    newPet.options.add(new ItemOption(id, op.param));
                }
            }
//            newPet.options.add(new ItemOption(72, 1));
//            newPet.options.add(new ItemOption(227, 0));
            player.addItem(newPet);
            result((byte) 5, newPet.template.iconID);
        } else {
            result((byte) 3);
        }
        update();
    }

    private int getLevel(Item item) {
        for (ItemOption op : item.options) {
            if (op.optionTemplate.id == 72) {
                return op.param;
            }
        }
        return 0;
    }
}
