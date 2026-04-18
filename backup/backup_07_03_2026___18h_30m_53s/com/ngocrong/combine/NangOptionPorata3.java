package com.ngocrong.combine;

import com.ngocrong.item.ItemOption;
import com.ngocrong.util.Utils;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.lib.KeyValue;

public class NangOptionPorata3 extends Combine {

    public static final int PERCENT = 100;
    public static final int[] REQUIRE = {1, 199, 1};
    public static final int[] OPTIONS = {50, 77, 103, 94, 108, 14, 5, 80, 81, 97};

    public NangOptionPorata3() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang\n");
        sb.append("Chọn bông tai Porata cấp 3\n");
        sb.append("Chọn mảnh hồn bông tai số lượng 199 cái và đá xanh lam để nâng cấp.\n");
        sb.append("Sau đó chọn 'Nâng cấp chỉ số'\n");
        setInfo(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Ta sẽ phù phép\n");
        sb2.append("Cho bông tai Porata cấp 3 của ngươi\n");
        sb2.append("có 1 chỉ số ngẫu nhiên");
        setInfo2(sb2.toString());
    }

    @Override
    public void showTab() {
        player.service.combine((byte)0, this, (short)-1, (short)-1);
    }

    @Override
    public void confirm() {
        if (itemCombine == null) {
            return;
        }
        String text = String.format("Cần %d bông tai Porata cấp 3, %d mảnh hồn bông tai, %d đá xanh lam và 100tr vàng", REQUIRE[0], REQUIRE[1], REQUIRE[2]);
        if (itemCombine.size() != 3) {
            player.service.dialogMessage(text);
            return;
        }
        Item[] items = new Item[3];
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.template.id == ItemName.BONG_TAI_PORATA_CAP_3) {
                    items[0] = item;
                }
                if (item.template.id == ItemName.MANH_HON_BONG_TAI) {
                    items[1] = item;
                }
                if (item.template.id == ItemName.DA_XANH_LAM) {
                    items[2] = item;
                }
            }
        }
        if (items[0] == null || items[1] == null || items[2] == null) {
            player.service.dialogMessage(text);
            return;
        }
        if (items[1].quantity < REQUIRE[1]) {
            player.service.dialogMessage(text);
            return;
        }
        if (player.gold < 100000000) {
            player.service.dialogMessage(text);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("|2|").append(String.format("%s [+%d]", items[0].template.name, 3)).append("\n");
        sb.append(String.format("Tỉ lệ thành công: %d%%", PERCENT)).append("\n");
        sb.append(String.format("Cần %d Mảnh hồn bông tai", REQUIRE[1])).append("\n");
        sb.append(String.format("Cần %s đá xanh lam", REQUIRE[2])).append("\n");
        sb.append("|1|").append(String.format("+%d Chỉ số ngâu nhiên", 1));
        player.menus.clear();
        player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng cấp\n 100tr vàng"));
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
        player.service.openUIConfirm(npc.templateId, sb.toString(), npc.avatar, player.menus);
    }

    @Override
    public void combine() {
        if (itemCombine == null) {
            return;
        }
        String text = String.format("Cần %d bông tai Porata cấp 3, %d mảnh hồn bông tai, %d đá xanh lam và 100tr vàng", REQUIRE[0], REQUIRE[1], REQUIRE[2]);
        if (itemCombine.size() != 3) {
            player.service.dialogMessage(text);
            return;
        }
        Item[] items = new Item[3];
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.template.id == ItemName.BONG_TAI_PORATA_CAP_3) {
                    items[0] = item;
                }
                if (item.template.id == ItemName.MANH_HON_BONG_TAI) {
                    items[1] = item;
                }
                if (item.template.id == ItemName.DA_XANH_LAM) {
                    items[2] = item;
                }
            }
        }
        if (items[0] == null || items[1] == null || items[2] == null) {
            player.service.dialogMessage(text);
            return;
        }
        if (items[1].quantity < REQUIRE[1]) {
            player.service.dialogMessage(text);
            return;
        }
        if (player.gold < 100000000) {
            player.service.dialogMessage(text);
            return;
        }
        player.removeItem(items[1].indexUI, REQUIRE[1]);
        player.removeItem(items[2].indexUI, REQUIRE[2]);
        player.subGold(100000000);
        if (Utils.nextInt(100) < PERCENT) {
            int index = -1;
            for (int i = 0; i < items[0].options.size(); i++) {
                ItemOption o = items[0].options.get(i);
                if (o.optionTemplate.id == 72) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                int rd1 = Utils.nextInt(OPTIONS.length);
                int idOpt = OPTIONS[rd1];
                int param = Utils.nextInt(5, 15);
                ItemOption add = new ItemOption(idOpt, param);
                index+=2;
                if (index >= items[0].options.size()) {
                    items[0].addItemOption(add);
                } else {
                    items[0].putItemOption(index, add);
                }
            }
            result((byte)2);
        } else {
            result((byte)3);
        }
        player.service.setItemBag();
        update();
    }
}
