package com.ngocrong.combine;

import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.util.Utils;

public class GhepKichHoat extends Combine {

    public static final int[][][] OPTIONS = {{{127, 139}, {128, 140}, {129, 141}}, {{130, 142}, {131, 143}, {132, 144}}, {{133, 136}, {134, 137}, {135, 138}}};

    public static final int[] ITEMS = {ItemName.AO_VAI_3_LO, ItemName.AO_SOI_LEN, ItemName.AO_VAI_THO, ItemName.QUAN_VAI_DEN, ItemName.QUAN_SOI_LEN, ItemName.QUAN_VAI_THO,
        ItemName.GANG_VAI_DEN, ItemName.GANG_SOI_LEN, ItemName.GANG_VAI_THO, ItemName.GIAY_NHUA, ItemName.GIAY_SOI_LEN, ItemName.GIAY_VAI_THO, ItemName.RADA_CAP_1};

    public GhepKichHoat() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang").append("\n");
        sb.append("Chọn 1 trang bị hủy diệt").append("\n");
        sb.append("Sau đó chọn 'Nâng cấp'");
        setInfo(sb.toString());

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Ta sẽ phù phép").append("\n");
        sb2.append("cho trang bị của ngươi").append("\n");
        sb2.append("trở thành trang bị kích hoạt");
        setInfo2(sb2.toString());
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
        if (itemCombine.size() != 1) {
            player.service.dialogMessage("Số lượng trang bị không hợp lệ");
            return;
        }
        Item item = player.itemBag[itemCombine.get(0)];
        if (item == null || item.template.type >= 5 || item.template.level != 14) {
            player.service.dialogMessage("Vật phẩm không hợp lệ");
            return;
        }
        String info = "Sau khi cường hóa, sẽ nhận được 1 trang bị kích hoạt ngẫu nhiên\nTỉ lệ thành công 50%\n"
                + "Cường hóa thất bại không mất trang bị sử dụng";
        player.menus.clear();
        player.menus.add(new KeyValue(CMDMenu.COMBINE, "Cường hóa\n500tr vàng"));
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
        if (item == null || item.template.type >= 5 || item.template.level != 14) {
            player.service.dialogMessage("Vật phẩm không hợp lệ");
            return;
        }
        if (player.gold < 500000000) {
            player.service.sendThongBao("Bạn không đủ vàng");
            return;
        }
        player.addGold(-500000000);
        if (Utils.nextInt(100) < 50) {
            item = new Item(ITEMS[Utils.nextInt(ITEMS.length)]);
            item.quantity = 1;
            item.setDefaultOptions();
            int index = Utils.nextInt(3);
            item.addItemOption(new ItemOption(OPTIONS[player.gender][index][0], 0));
            item.addItemOption(new ItemOption(OPTIONS[player.gender][index][1], 0));
            item.addItemOption(new ItemOption(30, 0));
            item.indexUI = itemCombine.get(0);
            player.itemBag[item.indexUI] = item;
            player.service.refreshItem((byte) 1, item);
            update();
        } else {
            result((byte) 3);
        }
    }
}
