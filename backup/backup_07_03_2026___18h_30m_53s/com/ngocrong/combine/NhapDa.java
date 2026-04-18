package com.ngocrong.combine;

import com.ngocrong.util.Utils;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.item.Item;
import com.ngocrong.lib.KeyValue;

public class NhapDa extends Combine {

    public NhapDa() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang").append("\n");
        sb.append("Chọn 10 mảnh đá vụn").append("\n");
        sb.append("Chọn 1 bình nước phép").append("\n");
        sb.append("Sau đó chọn 'Làm phép'");
        setInfo(sb.toString());

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Ta sẽ phù phép").append("\n");
        sb2.append("cho 10 mảnh đá vụn").append("\n");
        sb2.append("trở thành 1 đá nâng cấp");
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
        Item[] items = new Item[2];
        for (byte index : itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.id == 225) {
                    items[0] = item;
                }
                if (item.id == 226) {
                    items[1] = item;
                }
            }
        }
        if (items[0] == null || items[1] == null) {
            player.service.dialogMessage("Cần 10 mảnh đá vụn và 1 bình nước phép");
        } else {
            String require1 = "Cần 10 Mảnh đá vụn";
            String require2 = "Cần 1 Bình nước phép";
            String require3 = "Cần 2 k vàng";
            String receive = "|2|Con có muốn biến 10 mảnh đá vụn\nthành 1 viên đá nâng cấp ngẫu nhiên";
            player.menus.clear();
            boolean error = false;
            if (items[0].quantity < 10) {
                require1 = "|7|" + require1;
                error = true;
            } else {
                require1 = "|1|" + require1;
            }
            if (items[0].quantity == 0) {
                require2 = "|7|" + require2;
                error = true;
            } else {
                require2 = "|1|" + require2;
            }
            if (player.gold < 2000) {
                require3 = "|7|" + require3;
                error = true;
            } else {
                require3 = "|2|" + require3;
            }
            if (!error) {
                player.menus.add(new KeyValue(CMDMenu.COMBINE, "Làm phép\n2 k vàng"));
            }
            String info = receive + "\n" + require1 + "\n" + require2 + "\n" + require3;
            player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
            player.service.openUIConfirm(npc.templateId, info, npc.avatar, player.menus);

        }
    }

    @Override
    public void combine() {
        if (itemCombine == null) {
            return;
        }
        if (player.gold < 2000) {
            player.service.sendThongBao("Không đủ vàng.");
            return;
        }
        Item[] items = new Item[2];
        for (byte index : itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.id == 225 && item.quantity >= 10) {
                    items[0] = item;
                }
                if (item.id == 226 && item.quantity >= 1) {
                    items[1] = item;
                }
            }
        }
        if (items[0] == null || items[1] == null) {
            player.service.dialogMessage("Cần 10 mảnh đá vụn và 1 bình nước phép");
            return;
        }
        int[] list = new int[]{220, 221, 222, 223, 224};
        player.removeItem(items[0].indexUI, 10);
        player.removeItem(items[1].indexUI, 1);
        int rand = Utils.nextInt(list.length);
        int id = list[rand];
        Item item = new Item(id);
        item.setDefaultOptions();
        item.quantity = 1;
        result((byte) 4, item.template.iconID);
        player.addGold(-2000);
        player.addItem(item);
        update();
    }
}
