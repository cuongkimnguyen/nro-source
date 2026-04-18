package com.ngocrong.combine;

import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.util.Utils;

public class NangNhanAnhSang extends Combine {

    private static final int[] PERCENT = {100, 30, 15, 10, 5, 3, 2, 1};

    public static final int[][] OPTION = {{1000, 3}, {1001, 5}, {1002, 5}, {1003, 1}, {1004, 2}, {1005, 5}, {1006, 5}, {1007, 2}, {1008, 500}, {1009, 2}, {1010, 3}};

    public NangNhanAnhSang() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang").append("\n");
        sb.append("Chọn Nhẫn ánh sáng").append("\n");
        sb.append("Chọn Đá thời gian").append("\n");
        sb.append("Sau đó chọn 'Nâng cấp'");
        setInfo(sb.toString());

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Ta sẽ phù phép").append("\n");
        sb2.append("cho trang bị của ngươi").append("\n");
        sb2.append("trở thành trang cao cấp hơn");
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
        if (itemCombine.size() != 2) {
            player.service.dialogMessage("Số lượng trang bị không hợp lệ");
            return;
        }
        Item[] items = new Item[2];
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.template.id >= 2006 && item.template.id <= 2014) {
                    items[0] = item;
                } else if (item.template.id == ItemName.DA_THOI_GIAN) {
                    items[1] = item;
                }
            }
        }
        if (items[0] == null || items[1] == null) {
            player.service.sendThongBao("Vật phẩm sử dụng không hợp lệ");
            return;
        }
        if (items[0].template.id == ItemName.NHAN_ANH_SANG_CAP_9) {
            player.service.sendThongBao("Vật phẩm đã đạt cấp tối đa");
            return;
        }
        player.menus.clear();
        StringBuilder info = new StringBuilder();
        info.append(String.format("Cường hóa trang bị %s", items[0].template.name)).append("\n");
        info.append(String.format("Tỉ lệ thành công %d", PERCENT[items[0].template.level - 1]));
        player.menus.add(new KeyValue<>(CMDMenu.COMBINE, "Cường hóa\n500tr vàng"));
        player.menus.add(new KeyValue<>(CMDMenu.CANCEL, "Từ chối"));
        player.service.openUIConfirm(npc.templateId, info.toString(), npc.avatar, player.menus);
    }

    @Override
    public void combine() {
        if (itemCombine == null) {
            return;
        }
        if (itemCombine.size() != 2) {
            player.service.dialogMessage("Số lượng trang bị không hợp lệ");
            return;
        }
        Item[] items = new Item[2];
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.template.id >= 2006 && item.template.id <= 2014) {
                    items[0] = item;
                } else if (item.template.id == ItemName.DA_THOI_GIAN) {
                    items[1] = item;
                }
            }
        }
        if (items[0] == null || items[1] == null) {
            player.service.sendThongBao("Vật phẩm sử dụng không hợp lệ");
            return;
        }
        if (items[0].template.id == ItemName.NHAN_ANH_SANG_CAP_9) {
            player.service.sendThongBao("Vật phẩm đã đạt cấp tối đa");
            return;
        }
        if (items[1].quantity < 3) {
            player.service.sendThongBao(String.format("Cần tối thiểu x3 %s", items[1].template.name));
            return;
        }
        if (player.gold < 500000000) {
            player.service.sendThongBao("Bạn không đủ vàng");
            return;
        }
        player.addGold(-500000000);
        if (Utils.nextInt(100) < PERCENT[items[0].template.level - 1]) {
            Item item = new Item(items[0].template.id + 1);
            item.quantity = 1;
            for (ItemOption option : items[0].options) {
                if (option.optionTemplate.type == 10) {
                    item.addItemOption(new ItemOption(option.optionTemplate.id, option.param * item.template.level / items[0].template.level));
                }
            }
            item.indexUI = items[0].indexUI;
            player.itemBag[item.indexUI] = item;
            result((byte) 2);
        } else {
            result((byte) 3);
        }
        items[1].quantity -= 3;
        if (items[1].quantity < 1) {
            player.itemBag[items[1].indexUI] = null;
        }
        player.service.setItemBag();
        update();
    }
}
