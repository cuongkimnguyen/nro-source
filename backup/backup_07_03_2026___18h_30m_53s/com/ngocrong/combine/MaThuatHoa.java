package com.ngocrong.combine;

import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.util.Utils;

public class MaThuatHoa extends Combine {

    private static final int[] PERCENT = {100, 50, 10, 7, 6, 5, 4, 3};

    private static final int[][] OPTION = {{1000, 3}, {1001, 5}, {1002, 5}, {1003, 1}, {1004, 2}, {1005, 5}, {1006, 5}, {1007, 2}, {1008, 500}, {1009, 2}, {1010, 3}};

    private static final int MAX_STAR = 8;

    public MaThuatHoa() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang").append("\n");
        sb.append("Chọn cải trang, pet hoặc phụ kiện đeo lưng").append("\n");
        sb.append("Chọn Đá ma thuật tương ứng hoặc Đá thanh tẩy").append("\n");
        sb.append("Sau đó chọn 'Nâng cấp'");
        setInfo(sb.toString());

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Ta sẽ phù phép").append("\n");
        sb2.append("cho trang bị của ngươi").append("\n");
        sb2.append("trở thành trang bị sao");
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
                if (isCanMaThuatHoa(item)) {
                    items[0] = item;
                } else {
                    items[1] = item;
                }
            }
        }
        if (items[0] == null || items[1] == null
                || (items[1].template.id != ItemName.DA_THANH_TAY_MA_THUAT
                && (items[0].template.type == Item.TYPE_HAIR && items[1].template.id != ItemName.DA_MA_THUAT_THUY_THAN)
                || (items[0].template.type == Item.TYPE_BALO && items[1].template.id != ItemName.DA_MA_THUAT_LUC_BAO)
                || (items[0].template.type == 27 && items[1].template.id != ItemName.DA_MA_THUAT_HAC_AM))) {
            player.service.sendThongBao("Vật phẩm sử dụng không hợp lệ");
            return;
        }
        int star = 0;
        ItemOption itemOption = items[0].getItemOption(107);
        if (itemOption != null) {
            star = itemOption.param;
        }
        if (items[1].template.id != ItemName.DA_THANH_TAY_MA_THUAT) {
            if (star >= MAX_STAR) {
                player.service.sendThongBao("Trang bị đã đạt cấp tối đa");
                return;
            }
            if (items[1].quantity < 3) {
                player.service.sendThongBao(String.format("Cần tối thiểu x3 %s", items[1].template.name));
                return;
            }
        } else if (star == 0) {
            player.service.sendThongBao("Trang bị chưa được cường hóa");
            return;
        }
        player.menus.clear();
        StringBuilder info = new StringBuilder();
        if (items[1].template.id != ItemName.DA_THANH_TAY_MA_THUAT) {
            info.append("Bạn có muốn gỡ bỏ tất cả các chỉ số đã cường hóa không");
            player.menus.add(new KeyValue<>(CMDMenu.COMBINE, "Gỡ bỏ"));
        } else {
            info.append(String.format("Cường hóa trang bị %s", items[0].template.name)).append("\n");
            info.append(String.format("Tỉ lệ thành công %d", PERCENT[star]));
            player.menus.add(new KeyValue<>(CMDMenu.COMBINE, "Cường hóa\n500tr vàng"));
        }
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
                if (isCanMaThuatHoa(item)) {
                    items[0] = item;
                } else {
                    items[1] = item;
                }
            }
        }
        if (items[0] == null || items[1] == null
                || (items[1].template.id != ItemName.DA_THANH_TAY_MA_THUAT && (items[0].template.type == Item.TYPE_HAIR && items[1].template.id != ItemName.DA_MA_THUAT_THUY_THAN)
                || (items[0].template.type == Item.TYPE_BALO && items[1].template.id != ItemName.DA_MA_THUAT_LUC_BAO)
                || (items[0].template.type == 27 && items[1].template.id != ItemName.DA_MA_THUAT_HAC_AM))) {
            player.service.sendThongBao("Vật phẩm sử dụng không hợp lệ");
            return;
        }
        int star = 0;
        ItemOption itemOption = items[0].getItemOption(107);
        if (itemOption != null) {
            star = itemOption.param;
        }
        if (items[1].template.id != ItemName.DA_THANH_TAY_MA_THUAT) {
            if (star >= MAX_STAR) {
                player.service.sendThongBao("Trang bị đã đạt cấp tối đa");
                return;
            }
            if (items[1].quantity < 3) {
                player.service.sendThongBao(String.format("Cần tối thiểu x3 %s", items[1].template.name));
                return;
            }
        } else if (star == 0) {
            player.service.sendThongBao("Trang bị chưa được cường hóa");
            return;
        }
        if (player.gold < 500000000) {
            player.service.sendThongBao("Bạn không đủ vàng");
            return;
        }
        player.addGold(-500000000);
        int require;
        if (items[1].template.id == ItemName.DA_THANH_TAY_MA_THUAT) {
            require = 1;
            for (int i = 0; i < items[0].options.size(); i++) {
                ItemOption option = items[0].options.get(i);
                if (option.optionTemplate.id == 107 || option.optionTemplate.id == 102 || option.optionTemplate.type == 10) {
                    items[0].options.remove(i);
                    i--;
                }
            }
            result((byte) 2);
        } else {
            require = 3;
            if (Utils.nextInt(100) < PERCENT[star]) {
                int[] options = OPTION[Utils.nextInt(OPTION.length)];
                ItemOption option = items[0].getItemOption(options[0]);
                if (option != null) {
                    option.param += options[1];
                } else {
                    items[0].addItemOption(new ItemOption(options[0], options[1]));
                }
                star++;
                option = items[0].getItemOption(107);
                if (option != null) {
                    option.param = star;
                } else {
                    items[0].addItemOption(new ItemOption(107, star));
                }
                option = items[0].getItemOption(102);
                if (option != null) {
                    option.param = star;
                } else {
                    items[0].addItemOption(new ItemOption(102, star));
                }
                result((byte) 2);
            } else {
                result((byte) 3);
            }
        }
        items[1].quantity -= require;
        if (items[1].quantity < 1) {
            player.itemBag[items[1].indexUI] = null;
        }
        player.service.setItemBag();
        update();
    }

    public boolean isCanMaThuatHoa(Item item) {
        return item.template.type == Item.TYPE_HAIR || item.template.type == Item.TYPE_BALO
                || item.template.id == ItemName.LINH_BAO_VE_TAM_GIAC
                || item.template.id == ItemName.LINH_BAO_VE_TRON
                || item.template.id == ItemName.LINH_BAO_VE_VUONG
                || item.template.id == ItemName.BUP_BE
                || item.template.id == ItemName.HO_MAP_TRANG_943
                || item.template.id == ItemName.HO_MAP_VANG_942
                || item.template.id == ItemName.HO_MAP_XANH_944
                || item.template.id == ItemName.SAO_LA_967
                || item.template.id == ItemName.MA_PHONG_BA
                || item.template.id == ItemName.THAN_CHET_CUTE
                || item.template.id == ItemName.BI_NGO_NHI_NHANH
                || item.template.id == ItemName.CUA_DO_1008;
    }
}
