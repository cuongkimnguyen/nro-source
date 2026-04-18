/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.combine;

import static com.ngocrong.combine.NangCap.MAX_UPGRADE;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.util.Utils;

/**
 *
 * @author Administrator
 */
public class NangItemDeTu extends Combine {

    public int count;
    public static final float[] PERCENT_REAL = new float[]{50, 20, 10, 5, 0.5f, 0.25f, 0.125f, 0.0625f};
    public static final float[] PERCENT = new float[]{80, 60, 20, 10, 3, 2, 1, 0.5f};
    public static final int[] DA = {15, 16, 17, 18, 19, 20, 21, 22};
    public static final int[] GOLD = {10, 20, 40, 80, 140, 220, 320, 500};

    public NangItemDeTu() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang").append("\n");
        sb.append("Chọn trang bị").append("\n");
        sb.append("(đặc biệt của đệ tử)").append("\n");
        sb.append("Chọn loại đá để nâng cấp ").append("\n");
        sb.append("Sau đó chọn 'Nâng cấp'");
        setInfo(sb.toString());

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Ta sẽ phù phép").append("\n");
        sb2.append("cho trang bị của ngươi").append("\n");
        sb2.append("trở nên mạnh mẽ");
        setInfo2(sb2.toString());
    }

    public int tempIDrequire(Item item) {
        if (item != null) {
            if (item.template.id == 2239) {
                return 2275;
            }
            if (item.template.id == 2272) {
                return 2276;
            }
            if (item.template.id == 2273) {
                return 2277;
            }
        }
        return -1;
    }

    @Override
    public void confirm() {
        if (itemCombine == null) {
            return;
        }
        if (itemCombine.size() != 2) {
            player.service.dialogMessage("Cần 1 trang bị và đúng loại đá nâng cấp");
            return;
        }
        Item[] items = new Item[2];
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.template.isDeTu()) {
                    items[0] = item;
                }
            }
        }
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.template.id == tempIDrequire(items[0])) {
                    items[1] = item;
                }
            }
        }
        if (items[0] == null) {
            player.service.dialogMessage("Trang bị không phù hợp - thiếu Chân mệnh thiên tử");
            return;
        }
        if (items[1] == null) {
            player.service.dialogMessage("Trang bị không phù hợp - thiếu đá nâng cấp hoặc loại đá nâng cấp không đúng để nâng Chân mệnh thiên tử");
            return;
        }
        int upgrade = 0;
        for (ItemOption option : items[0].options) {
            if (option.optionTemplate.id == 72) {
                upgrade = option.param;
            }
        }
        if (upgrade >= MAX_UPGRADE) {
            player.service.dialogMessage("Trang bị đã đạt cấp tối đa");
            return;
        }
        String options = "";
        int options2 = 0;
        int[] hpmp = new int[]{1, 1, 2, 3, 5, 7, 9, 12};
        int[] sd = new int[]{1, 1, 2, 2, 3, 3, 5, 7};
        if (items[1].template.id == 2275) {
            options = "% HP";
            options2 = hpmp[upgrade];
        }
        if (items[1].template.id == 2276) {
            options = "% KI";
            options2 = hpmp[upgrade];
        }
        if (items[1].template.id == 2277) {
            options = "% SD";
            options2 = sd[upgrade];
        }
        int gold = GOLD[upgrade] * 1_000_000;
        int da = 20;
        String name = "|2|" + items[0].template.name + " (+" + upgrade + ")";
        String upgrade2 = "|2|Sau khi nâng cấp (+" + (upgrade + 1) + ")";
        String percent = "|2|Tỉ lệ thành công: " + PERCENT[upgrade] + "%";
        String require1 = "Cần " + da + " " + items[1].template.name;
        String require2 = "Cần " + Utils.currencyFormat(gold) + " vàng";
        String output = "Sau khi tăng cấp thành công: +" + String.valueOf(options2) + options;
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
        String info = name + "\n" + upgrade2 + "\n" + percent + "\n" + require1 + "\n" + require2 + "\n" + output;
        player.menus.clear();
        if (!error) {
            player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng cấp", 1));
            player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng cấp 10 lần", 10));
            player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng cấp 100 lần", 100));
            player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng cấp tới cấp tiếp theo", Integer.MAX_VALUE));
        }
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
        player.service.openUIConfirm(npc.templateId, info, npc.avatar, player.menus);
    }

    @Override
    public void combine() {
        if (itemCombine == null) {
            return;
        }
        if (itemCombine.size() != 2) {
            player.service.dialogMessage("Cần 1 trang bị và đúng loại đá nâng cấp");
            return;
        }
        Item[] items = new Item[2];
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.template.isDeTu()) {
                    items[0] = item;
                }
            }
        }
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.template.id == tempIDrequire(items[0])) {
                    items[1] = item;
                }
            }
        }
        if (items[0] == null) {
            player.service.dialogMessage("Trang bị không phù hợp - thiếu Chân mệnh thiên tử");
            return;
        }
        if (items[1] == null) {
            player.service.dialogMessage("Trang bị không phù hợp - thiếu đá nâng cấp hoặc loại đá nâng cấp không đúng để nâng Chân mệnh thiên tử");
            return;
        }
        int upgrade = 0;
        ItemOption[] op = new ItemOption[3];
        for (ItemOption option : items[0].options) {
            if (option.optionTemplate.id == 72) {
                upgrade = option.param;
                op[0] = option;
            }
            if (option.optionTemplate.id == 102) {
                op[1] = option;
            }
            if (option.optionTemplate.id == 107) {
                op[2] = option;
            }
        }
        if (upgrade >= MAX_UPGRADE) {
            player.service.dialogMessage("Trang bị đã đạt cấp tối đa");
            return;
        }
        int gold = GOLD[upgrade] * 1_000_000;
        int da = 20;
        int totalDa = 0;
        long totalGold = 0;
        boolean flagCheck = false;
        float percent = PERCENT_REAL[upgrade];
        System.err.println("percent:" + percent);
        for (int i = 0; i < count && !flagCheck; i++) {
            if (items[1].quantity < totalDa + da) {
                break;
            }
            if (player.gold < totalGold + gold) {
                break;
            }
            totalGold += gold;
            totalDa += da;
            if (Utils.isTrue(percent, 100)) {
                flagCheck = true;
                int[] hpmp = new int[]{1, 1, 2, 3, 5, 7, 9, 12};
                int[] sd = new int[]{1, 1, 2, 2, 3, 3, 5, 7};
                int[] options = new int[2];
                if (items[1].template.id == 2275) {
                    options[0] = 77;
                    options[1] = hpmp[upgrade];
                }
                if (items[1].template.id == 2276) {
                    options[0] = 103;
                    options[1] = hpmp[upgrade];
                }
                if (items[1].template.id == 2277) {
                    options[0] = 50;
                    options[1] = sd[upgrade];
                }
                boolean flag = false;
                for (ItemOption option : items[0].options) {
                    if (option != null && option.optionTemplate.id == options[0]) {
                        option.param += options[1];
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    items[0].options.add(new ItemOption(options[0], options[1]));
                }
                if (op[0] == null) {
                    items[0].options.add(new ItemOption(72, 1));
                } else {
                    op[0].param++;
                }
                if (op[1] == null) {
                    items[0].options.add(new ItemOption(102, 1));
                } else {
                    op[1].param++;
                }
                if (op[2] == null) {
                    items[0].options.add(new ItemOption(107, 1));
                } else {
                    op[2].param++;
                }
            }
        }
        if (flagCheck) {
            result((byte) 2);
            if (upgrade > 7) {
                player.saveData();
            }
        } else {
            result((byte) 3);
        }
        player.addGold(-totalGold);
        player.removeItem(items[1].indexUI, totalDa);

        player.service.refreshItem((byte) 1, items[0]);
        update();
    }

}
