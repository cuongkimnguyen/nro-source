/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.combine;

import com.ngocrong.consts.CMDMenu;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.util.Utils;

/**
 *
 * @author Administrator
 */
public class NangCap_2 extends Combine {

    public static final double[] PERCENT = {1, 0.5, 0.3, 0.1};
    public static final int[] PERCENT_REAL = {2000, 10000, 20000, 40000};
    public static final byte MAX_UPGRADE = 10;

    static int getBonus(Item item) {
        if (item == null || item.template == null || item.template.type != 20) {
            return 0;
        }
        switch (item.template.id) {
            case 2199:
                return 5;
            case 2200:
                return 10;
            case 2201:
                return 20;
            case 2202:
                return 40;
            case 2203:
                return 80;
            case 2204:
                return 160;
            case 2205:
                return 350;
        }
        return 0;
    }

    public NangCap_2() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang").append("\n");
        sb.append("Chọn trang bị").append("\n");
        sb.append("(Áo,quần,găng,giày hoặc rada)").append("\n");
        sb.append("Chọn đá nâng cấp").append("\n");
        sb.append("Chọn đá bảo vệ").append("\n");
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
            player.service.dialogMessage("Cần 1 trang bị, đá nâng cấp và đá bảo vệ");
            return;
        }
        Item[] items = new Item[3];
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.template.type < 5) {
                    items[0] = item;
                } else if (item.template.type == 20) {
                    items[1] = item;
                } else if (item.template.id == 987) {
                    items[2] = item;
                }
            }
        }
        if (items[0] == null || items[1] == null || items[2] == null) {
            player.service.dialogMessage("Cần 1 trang bị, đá nâng cấp và đá bảo vệ");
            return;
        }
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
        if (upgrade < 6) {
            player.service.dialogMessage("Chỉ có thể nâng cấp trang bị từ cấp 6 trở lên");
            return;
        }
        if (upgrade >= MAX_UPGRADE) {
            player.service.dialogMessage("Trang bị đã đạt cấp tối đa");
            return;
        }
        int bonus = getBonus(items[1]);
        if (bonus != 0) {
            int param = itemOption.param;
            int add = param * 10 / 100;
            if (add == 0) {
                add = 1;
            }
            ItemOption itemOption2 = new ItemOption(itemOption.optionTemplate.id, param + add);
            int gold = 100000000;
            int da = 1;
            String name = "|2|" + items[0].template.name + " (+" + upgrade + ")";
            String before = "|0|" + itemOption.getOptionString();
            String upgrade2 = "|2|Sau khi nâng cấp (+" + (upgrade + 1) + ")";
            String after = "|1|" + itemOption2.getOptionString();
            String percent = "|2|Tỉ lệ thành công: " + PERCENT[upgrade - 6] + "%";
            String require1 = "Cần " + 1 + " " + items[1].template.name;
            String require2 = "Cần " + Utils.currencyFormat(gold) + " vàng";
            String bonusStr = String.format("Dùng %s sẽ tăng %d điểm may mắn , giúp tăng khả năng nâng cấp thành công", items[1].template.name, bonus);
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
            String info = name + "\n" + before + "\n" + upgrade2 + "\n" + after + "\n" + percent + "\n" + require1 + "\n" + require2 + "\n" + bonusStr;
            player.menus.clear();
            if (!error) {
                player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng cấp"));
            }
            player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
            player.service.openUIConfirm(npc.templateId, info, npc.avatar, player.menus);
        }
    }

    @Override
    public void combine() {
        if (itemCombine == null) {
            return;
        }
        if (itemCombine.size() > 3) {
            player.service.dialogMessage("Cần 1 trang bị, đá nâng cấp và đá bảo vệ");
            return;
        }
        Item[] items = new Item[3];
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.template.type < 5) {
                    items[0] = item;
                } else if (item.template.type == 20) {
                    items[1] = item;
                } else if (item.template.id == 987) {
                    items[2] = item;
                }
            }
        }
        if (items[0] == null || items[1] == null || items[2] == null) {
            player.service.dialogMessage("Cần 1 trang bị, đá nâng cấp và đá bảo vệ");
            return;
        }
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
        if (upgrade < 6) {
            player.service.dialogMessage("Chỉ có thể nâng cấp trang bị từ cấp 6 trở lên");
            return;
        }
        if (upgrade >= MAX_UPGRADE) {
            player.service.dialogMessage("Trang bị đã đạt cấp tối đa");
            return;
        }
        int bonus = getBonus(items[1]);
        if (bonus != 0) {
            int gold = 100000000;
            int da = 1;
            if (player.gold < gold) {
                player.service.serverMessage2("Không đủ vàng");
                return;
            }
            if (items[1].quantity < da) {
                player.service.serverMessage2("Không đủ " + items[1].template.name);
                return;
            }
            if (items[2].quantity < 1) {
                player.service.serverMessage2("Không đủ " + items[1].template.name);
                return;
            }
            player.addGold(-gold);
            player.removeItem(items[1].indexUI, da);
            player.removeItem(items[2].indexUI, 1);
            if (Utils.isTrue(bonus, PERCENT_REAL[upgrade - 6])) {
                int param = itemOption.param;
                int add = param / 10;
                if (add == 0) {
                    add = 1;
                }
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
            } else {
                result((byte) 3);
            }
            player.service.refreshItem((byte) 1, items[0]);
            update();
        }
    }

}
