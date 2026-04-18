package com.ngocrong.combine;

import _HunrProvision.services.BoMongService;

import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.util.Utils;
import com.ngocrong.consts.CMDMenu;

public class NangCap extends Combine {

    public static final double[] PERCENT = {80, 50, 20, 10, 7, 5, 3, 1};

    public static final double[] PERCENT_REAL = {80, 50, 5, 5,2, 2.2, 1, 1};

    public static final byte MAX_UPGRADE = 8;

    public NangCap() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang").append("\n");
        sb.append("Chọn trang bị").append("\n");
        sb.append("(Áo,quần,găng,giày hoặc rada)").append("\n");
        sb.append("Chọn loại đá để nâng cấp").append("\n");
        sb.append("Có thể sử dụng thêm đã bảo vệ để tránh hạ cấp khi nâng cấp thất bại ").append("\n");
        sb.append("Sau đó chọn 'Nâng cấp'");
        setInfo(sb.toString());

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Ta sẽ phù phép").append("\n");
        sb2.append("cho trang bị của ngươi").append("\n");
        sb2.append("trở nên mạnh mẽ");
        setInfo2(sb2.toString());
    }

    @Override
    public void confirm() {
        if (itemCombine == null) {
            return;
        }
        if (itemCombine.size() > 3) {
            player.service.dialogMessage("Cần 1 trang bị, đúng loại đá nâng cấp đá bảo vệ (nếu có)");
            return;
        }
        Item[] items = new Item[3];
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.template.type < 5) {
                    items[0] = item;
                } else if (item.template.type == 14) {
                    items[1] = item;
                } else {
                    items[2] = item;
                }
            }
        }
        if (items[2] != null && items[2].template.id != 987) {
            player.service.dialogMessage("Chỉ có thể thêm đá bảo vệ khi nâng cấp");
            return;
        }

//        int index = this.itemCombine.get(i == 0 ? 1 : 0);
//        items[1] = player.itemBag[index];
        if (items[0] == null || items[1] == null || (items[0].template.type == 0 && items[1].id != 223)
                || (items[0].template.type == 1 && items[1].id != 222 || (items[0].template.type == 2 && items[1].id != 224)
                || (items[0].template.type == 3 && items[1].id != 221) || (items[0].template.type == 4 && items[1].id != 220))) {
            player.service.dialogMessage("Cần 1 trang bị và đúng loại đá nâng cấp");
        } else {
            int upgrade = 0;
            ItemOption itemOption = null;
            for (ItemOption option : items[0].options) {
                if (itemOption == null) {
                    if (items[0].template.type == 0 && option.optionTemplate.id == 47) {
                        itemOption = option;
                    }
                    if (items[0].template.type == 1 && option.optionTemplate.id == 6) {
                        itemOption = option;
                    }
                    if (items[0].template.type == 2 && option.optionTemplate.id == 0) {
                        itemOption = option;
                    }
                    if (items[0].template.type == 3 && option.optionTemplate.id == 7) {
                        itemOption = option;
                    }
                    if (items[0].template.type == 4 && option.optionTemplate.id == 14) {
                        itemOption = option;
                    }
                }
                if (option.optionTemplate.id == 72) {
                    upgrade = option.param;
                }
            }
//            if (upgrade < 6) {

            if (upgrade >= MAX_UPGRADE) {
                player.service.dialogMessage("Trang bị đã đạt cấp tối đa");
                return;
            }
            int param = itemOption.param;
            int add = param * 10 / 100;
            if (add == 0) {
                add = 1;
            }
            ItemOption itemOption2 = new ItemOption(itemOption.optionTemplate.id, param + add);
            int gold = 100000000;
            int da = (items[0].template.level + upgrade + 1);
            String name = "|2|" + items[0].template.name + " (+" + upgrade + ")";
            String before = "|0|" + itemOption.getOptionString();
            String upgrade2 = "|2|Sau khi nâng cấp (+" + (upgrade + 1) + ")";
            String after = "|1|" + itemOption2.getOptionString();
            String percent = "|2|Tỉ lệ thành công: " + PERCENT[upgrade] + "%";
            String require1 = "Cần " + da + " " + items[1].template.name;
            String require2 = "Cần " + Utils.currencyFormat(gold) + " vàng";
            boolean error = false;
            if (items[1].quantity < da) {
                require1 = "|7|" + require1;
                error = true;
            } else {
                require1 = "|2|" + require1;
            }
            if (player.gold < gold) {
                require2 = "|7|" + require2;
                error = true;
            } else {
                require2 = "|2|" + require2;
            }
            String info = name + "\n" + before + "\n" + upgrade2 + "\n" + after + "\n" + percent + "\n" + require1 + "\n" + require2;
            if (upgrade == 2 || upgrade == 4 || upgrade == 6 || upgrade == 7) {
                info += "\n|2|Nếu thất bại sẽ rớt xuống (+" + (upgrade - 1) + ")";
                info += "\n|2|Nếu có đá bảo vệ thì thất bại sẽ không bị hạ cấp";
            }
            player.menus.clear();
            if (!error) {
                player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng cấp"));
            }
            player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
            player.service.openUIConfirm(npc.templateId, info, npc.avatar, player.menus);
//            } else {
//                player.service.dialogMessage("Muốn nâng cấp 7 trở lên hãy dùng chức năng Nâng Cấp 2");
//            }
        }
    }

    @Override
    public void combine() {
        if (itemCombine == null) {
            return;
        }
        if (itemCombine.size() > 3) {
            player.service.dialogMessage("Cần 1 trang bị, đúng loại đá nâng cấp và đá bảo vệ (nếu có)");
            return;
        }
        Item[] items = new Item[3];
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.template.type < 5) {
                    items[0] = item;
                } else if (item.template.type == 14) {
                    items[1] = item;
                } else {
                    items[2] = item;
                }
            }
        }

        if (items[2] != null && items[2].template.id != 987) {
            player.service.dialogMessage("Chỉ có thể thêm đá bảo vệ khi nâng cấp");
            return;
        }

        if (items[0] == null || items[1] == null || (items[0].template.type == 0 && items[1].id != 223)
                || (items[0].template.type == 1 && items[1].id != 222 || (items[0].template.type == 2 && items[1].id != 224)
                || (items[0].template.type == 3 && items[1].id != 221) || (items[0].template.type == 4 && items[1].id != 220))) {
            player.service.dialogMessage("Cần 1 trang bị và đúng loại đá nâng cấp");
        } else {
            int upgrade = 0;
            ItemOption itemOption = null;
            ItemOption itemOption2 = null;
            for (ItemOption option : items[0].options) {
                if (itemOption == null) {
                    if (items[0].template.type == 0 && option.optionTemplate.id == 47) {
                        itemOption = option;
                    }
                    if (items[0].template.type == 1 && option.optionTemplate.id == 6) {
                        itemOption = option;
                    }
                    if (items[0].template.type == 2 && option.optionTemplate.id == 0) {
                        itemOption = option;
                    }
                    if (items[0].template.type == 3 && option.optionTemplate.id == 7) {
                        itemOption = option;
                    }
                    if (items[0].template.type == 4 && option.optionTemplate.id == 14) {
                        itemOption = option;
                    }
                }
                if (option.optionTemplate.id == 72) {
                    upgrade = option.param;
                    itemOption2 = option;
                }
            }
//            if (upgrade < 6) {

            if (upgrade >= MAX_UPGRADE) {
                player.service.dialogMessage("Trang bị đã đạt cấp tối đa");
                return;
            }
            int param = itemOption.param;
            int add = param / 10;
            var percent = PERCENT_REAL[upgrade];
            //percent = getFloat(items[0], upgrade, items[2] != null);
            if (add == 0) {
                add = 1;
            }
//            int gold = items[0].template.level * 10000 * (upgrade + 1);
            int gold = 100000000;
            int da = (items[0].template.level + upgrade + 1);
            if (player.gold < gold) {
                player.service.serverMessage2("Không đủ vàng");
                return;
            }
            if (items[1].quantity < da) {
                player.service.serverMessage2("Không đủ " + items[1].template.name);
                return;
            }
            player.addGold(-gold);
            player.removeItem(items[1].indexUI, da);
            if (items[2] != null) {
                player.removeItem(items[2].indexUI, 1);
            }
     //       System.err.println("percent :" + percent);
            if (Utils.isTrue(percent, 100.0)) {
                if (itemOption2 == null) {
                    itemOption2 = new ItemOption(72, 0);
                    items[0].addItemOption(itemOption2);
                }
                itemOption2.param++;
                itemOption.param += add;
                result((byte) 2);
                if (itemOption2.param > 7) {
                    player.saveData();
                }
                if (player.currentNhiemVuBoMong != null) {
                    BoMongService nv = player.currentNhiemVuBoMong;
                    if (nv.loaiNv == BoMongService.LOAI_NANG_TRANG_BI) {
                        nv.tienDo++;
                        player.checkBoMongProgress(nv);
                        player.saveBoMongNhiemVu();
                        if (nv.tienDo >= nv.yeuCau) {
                            player.completeNhiemVuBoMong();
                        }
                    }
                }
            } else {
                if (upgrade == 2 || upgrade == 4 || upgrade == 6 || upgrade == 7) {
                    if (items[2] != null) {
                        player.service.serverMessage2("Bạn đã sử dụng đá nâng cấp nên cấp độ không bị giảm");
                    } else {
                        itemOption.param -= add;
                        itemOption2.param--;
                    }
                }
                result((byte) 3);
            }
            player.service.refreshItem((byte) 1, items[0]);
            update();
//            } else {
//                player.service.dialogMessage("Muốn nâng cấp 7 trở lên hãy dùng chức năng Nâng Cấp 2");
//            }
        }
    }

//    float getFloat(Item item, int upgrade, boolean isDBV) {
//        if (isDBV) {
//            if (upgrade == 2) {
//                return 20;
//            }
//            if (upgrade == 4) {
//                return 10;
//            }
//            if (upgrade == 6) {
//                return 6.6f;
//            }
//            if (upgrade == 7) {
//                return 5f;
//            }
//            return PERCENT_REAL[upgrade];
//        }
//        int type = item.template.type;
//        if (type == 0) {
//            switch (upgrade) {
//                case 0:
//                case 1:
//                case 2:
//                case 3:
//                    return 100;
//                case 4:
//                    return 10;
//                case 5:
//                    return 5;
//                case 6:
//                    return 2;
//                case 7:
//                    return 1;
//            }
//        }
//        if (type == 1) {
//            switch (upgrade) {
//                case 0:
//                case 1:
//                    return 100;
//                case 2:
//                    if (item.template.gender == 2) {
//                        return 10;
//                    }
//                    return 20;
//                case 3:
//                case 4:
//                case 5:
//                    return 5;
//                case 6:
//                case 7:
//                    return 2;
//            }
//        }
//        if (type == 2) {
//            switch (upgrade) {
//                case 0:
//                case 1:
//                    return 100;
//                case 2:
//                    return 10;
//                case 3:
//                case 4:
//                case 5:
//                    return 5;
//                case 6:
//                case 7:
//                    return 2;
//            }
//        }
//        if (type == 3) {
//            switch (upgrade) {
//                case 0:
//                case 1:
//                    return 100;
//                case 2:
//                case 3:
//                case 4:
//                case 5:
//                    return 10;
//                case 6:
//                case 7:
//                    return 2;
//            }
//        }
//        if (type == 4) {
//            switch (upgrade) {
//                case 0:
//                case 1:
//                    return 100;
//                case 2:
//                    return 50;
//                case 3:
//                case 4:
//                case 5:
//                    return 10;
//                case 6:
//                case 7:
//                    return 2;
//            }
//        }
//        return PERCENT_REAL[upgrade];
//    }

    @Override
    public void showTab() {
        player.service.combine((byte) 0, this, (short) -1, (short) -1);
    }
}
