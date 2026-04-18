package com.ngocrong.combine;

import com.ngocrong.item.ItemOption;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.lib.KeyValue;

import java.util.ArrayList;

public class EpPhaLe extends Combine {

    public static final int[][] ABILITY = {{108, 2}, {94, 2}, {50, 3}, {81, 5}, {80, 5}, {103, 5}, {77, 5}};

    public EpPhaLe() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang").append("\n");
        sb.append("Chọn trang bị").append("\n");
        sb.append("(Áo, quần, găng, giày hoặc rada) có ô đặt sao pha lê").append("\n");
        sb.append("Chọn loại có sao pha lê").append("\n");
        sb.append("Sau đó chọn 'Nâng cấp'");
        setInfo(sb.toString());

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Ta sẽ phù phép").append("\n");
        sb2.append("cho trang bị của ngươi").append("\n");
        sb2.append("trở nên mạnh mẽ");
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
        String text = "Cần 1 trang bị có lỗ sao và 1 loại ngọc để ép vào";
        if (itemCombine.size() != 2) {
            player.service.dialogMessage(text);
            return;
        }
        Item[] items = new Item[2];
        Item item = player.itemBag[itemCombine.get(0)];
        if (item != null) {
            if (item.template.type < 5 || item.template.type == 32) {
                items[0] = item;
                items[1] = player.itemBag[itemCombine.get(1)];
            } else {
                items[0] = player.itemBag[itemCombine.get(1)];
                items[1] = player.itemBag[itemCombine.get(0)];
            }
        }
        boolean z = false;
        if (items[1] == null || (!(items[1].id >= 14 && items[1].id <= 20) && !(items[1].id >= 441 && items[1].id <= 447)) && items[1].id != ItemName.SAO_PHA_LE_964 && items[1].id != ItemName.SAO_PHA_LE_965) {
            z = true;
        }
        if (items[0] == null || items[1] == null || z) {
            player.service.dialogMessage(text);
            return;
        }
        ItemOption option1 = null;
        ItemOption option2 = null;
        ItemOption option3 = null;
//        int star = 0;
        for (ItemOption option : items[0].options) {
            if (option.optionTemplate.id == 107) {
                option1 = option;
            }
            if (option.optionTemplate.id == 102) {
                option2 = option;
//                star = option.param;
            }
        }
        if (option1 == null || (option2 != null && option2.param >= option1.param)) {
            player.service.dialogMessage(text);
            return;
        }
//        if (star == 7) {
//            if (items[1].id != 964 && items[1].id != 965) {
//                player.service.dialogMessage("Chỉ có thể nạm sao pha lê mới");
//                return;
//            }
//        }
        int id = 0;
        int param = 0;
        if (items[1].id >= 14 && items[1].id <= 20) {
            int[] info = ABILITY[items[1].id - 14];
            id = info[0];
            param = info[1];
        } else {
            ItemOption tmp = items[1].options.get(0);
            id = tmp.optionTemplate.id;
            param = tmp.param;
        }
        ItemOption tmp = items[0].getItemOption(id);
        if (tmp == null) {
            option3 = new ItemOption(id, param);
        } else {
            option3 = new ItemOption(id, tmp.param + param);
        }
        ArrayList<ItemOption> list = new ArrayList<>();
        for (ItemOption iOption : items[0].options) {
            if (iOption.optionTemplate.id != id) {
                list.add(iOption);
            }
        }
        list.add(option3);
        String name = items[0].template.name;
        String require = "Cần 10 ngọc";
        String detail = "";
        boolean error = false;
        for (ItemOption iOption : list) {
            if (iOption.optionTemplate.id == 102 || iOption.optionTemplate.id == 107) {
                continue;
            }
            String optionStr = iOption.getOptionString();
            if (optionStr != null && !optionStr.equals("")) {
                if (iOption.optionTemplate.id == id) {
                    detail += "|1|" + optionStr + "\n";
                } else {
                    detail += "|0|" + optionStr + "\n";
                }
            }
        }
        if (player.getTotalGem() < 10) {
            require = "|7|" + require;
            error = true;
        } else {
            require = "|2|" + require;
        }
        String info = name + "\n" + detail + require;
        player.menus.clear();
        if (!error) {
            player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng cấp"));
        }
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
        player.service.openUIConfirm(npc.templateId, info, npc.avatar, player.menus);
    }

    @Override
    public void combine() {
        if (itemCombine == null) {
            return;
        }
        if (itemCombine == null) {
            return;
        }
        String text = "Cần 1 trang bị có lỗ sao và 1 loại ngọc để ép vào";
        if (itemCombine.size() != 2) {
            player.service.dialogMessage(text);
            return;
        }
        Item[] items = new Item[2];
        Item item = player.itemBag[itemCombine.get(0)];
        if (item != null) {
            if (item.template.type < 5 || item.template.type == 32) {
                items[0] = item;
                items[1] = player.itemBag[itemCombine.get(1)];
            } else {
                items[0] = player.itemBag[itemCombine.get(1)];
                items[1] = player.itemBag[itemCombine.get(0)];
            }
        }
        boolean z = false;
        if (items[1] == null || (!(items[1].id >= 14 && items[1].id <= 20) && !(items[1].id >= 441 && items[1].id <= 447)) && items[1].id != ItemName.SAO_PHA_LE_964 && items[1].id != ItemName.SAO_PHA_LE_965) {
            z = true;
        }
        if (items[0] == null || items[1] == null || z) {
            player.service.dialogMessage(text);
            return;
        }
        ItemOption option1 = null;
        ItemOption option2 = null;
        ItemOption option3 = null;
//        int star = 0;
        for (ItemOption option : items[0].options) {
            if (option.optionTemplate.id == 107) {
                option1 = option;
            }
            if (option.optionTemplate.id == 102) {
                option2 = option;
//                star = option.param;
            }
        }
        if (option1 == null || (option2 != null && option2.param >= option1.param)) {
            player.service.dialogMessage(text);
            return;
        }
//        if (star == 7) {
//            if (items[1].id != 964 && items[1].id != 965) {
//                return;
//            }
//        }
        if (player.getTotalGem() < 10) {
            player.service.serverMessage2("Không đủ ngọc");
            return;
        }
        player.subDiamond(10);
        player.removeItem(items[1].indexUI, 1);
        int id = 0;
        int param = 0;
        if (items[1].id >= 14 && items[1].id <= 20) {
            int[] info = ABILITY[items[1].id - 14];
            id = info[0];
            param = info[1];
        } else {
            ItemOption tmp = items[1].options.get(0);
            id = tmp.optionTemplate.id;
            param = tmp.param;
        }
        ItemOption tmp = items[0].getItemOption(id);
        if (tmp == null) {
            option3 = new ItemOption(id, param);
        } else {
            option3 = new ItemOption(id, tmp.param + param);
        }
        ArrayList<ItemOption> list = new ArrayList<>();
        for (ItemOption iOption : items[0].options) {
            if (iOption.optionTemplate.id != id && iOption.optionTemplate.id != 107 && iOption.optionTemplate.id != 102) {
                list.add(iOption);
            }
        }
        list.add(option1);
        if (option2 == null) {
            option2 = new ItemOption(102, 0);
        }
        option2.param++;
        list.add(option2);
        list.add(option3);
        items[0].options = list;
        result((byte) 2);
        player.service.refreshItem((byte) 1, items[0]);
        update();
    }

}
