package com.ngocrong.combine;

import com.ngocrong.item.ItemOption;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.item.Item;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.network.Service;
import com.ngocrong.util.Utils;

public class PhaLeHoa extends Combine {

    public static final int[] FEE = {10000000, 20000000, 40000000, 80000000, 140000000, 220000000, 320000000, 500000000};
    public static final int[] DIAMOND_LOCK = {1, 1, 1, 1, 1, 1, 1, 1};
    public static final int[] DIAMOND = {1, 1, 1, 1, 1, 1, 1, 1};
    public static final double[] PERCENT = {80, 60, 20, 10, 3, 2, 1, 0.5};
    public static final double[] PERCENT_REAL = {80, 60, 20, 3, 2, 0.3, 0.05, 0.025};
    public static final byte MAX_STAR = 8;

    public int count;
    public Service service;

    public PhaLeHoa() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang").append("\n");
        sb.append("Chọn trang bị").append("\n");
        sb.append("(Áo, quần, găng, giày hoặc rada)").append("\n");
        sb.append("Sau đó chọn 'Nâng cấp'");
        setInfo(sb.toString());

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Ta sẽ phù phép").append("\n");
        sb2.append("cho trang bị của ngươi").append("\n");
        sb2.append("trở thành trang bị pha lê");
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
        Item item = null;
        if (itemCombine.size() == 1) {
            int index = this.itemCombine.get(0);
            item = player.itemBag[index];
        }
        if (item == null || !(item.template.type < 5 || item.template.type == 32)) {
            player.service.dialogMessage("Trang bị không phù hợp");
        } else {
            int sao = 0;
            String detail = "|0|";
            int size = item.options.size();
            for (int i = 0; i < size; i++) {
                ItemOption option = item.options.get(i);
                if (option.optionTemplate.id == 102) {
                    continue;
                }
                if (option.optionTemplate.id == 107) {
                    sao = option.param;
                } else {
                    detail += option.getOptionString() + "\n";
                }
            }
            if (sao >= MAX_STAR) {
                player.service.dialogMessage("Số lượng sao pha lê đã đạt tối đa");
                return;
            }
            ItemOption option2 = new ItemOption(107, sao + 1);
            detail += option2.getOptionString();
            String name = item.template.name;
            String percent = "|2|" + String.format("Tỉ lệ thành công: %s", PERCENT[sao] + "%");
            String require1 = String.format("Cần %s vàng", Utils.currencyFormat(FEE[sao]));
            boolean error = false;
            if (player.gold < FEE[sao]) {
                require1 = "|7|" + require1;
                error = true;
            } else {
                require1 = "|2|" + require1;
            }
            String info = name + "\n" + detail + "\n" + percent + "\n" + require1;
            player.menus.clear();
            if (!error) {
                if (player.gold >= FEE[sao]) {
                    player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng cấp\n1 lần", 1));
                } else {
                    player.service.sendThongBao("Bạn không có đủ vàng");
                    return;
                }
                if (player.gold >= FEE[sao] * 10L) {
                    player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng cấp\n10 lần", 10));
                }
                if (player.gold >= FEE[sao] * 100L) {
                    player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng cấp\n100 lần", 100));
                }
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
        Item item = null;
        if (itemCombine.size() == 1) {
            int index = this.itemCombine.get(0);
            item = player.itemBag[index];
        }
        if (item == null || !(item.template.type < 5 || item.template.type == 32)) {
            player.service.dialogMessage("Trang bị không phù hợp");
        } else {
            ItemOption option2 = null;
            int size = item.options.size();
            for (int i = 0; i < size; i++) {
                ItemOption option = item.options.get(i);
                if (option.optionTemplate.id == 107) {
                    option2 = option;
                    break;
                }
            }
            if (option2 == null) {
                option2 = new ItemOption(107, 0);
            }
            int sao = option2.param;
            if (sao >= MAX_STAR) {
                player.service.sendThongBao("Số lượng sao pha lê đã đạt tối đa");
                return;
            }
            int count = this.count;
//            if (player.diamond < count * DIAMOND[sao]) {
//                player.service.sendThongBao("Bạn không đủ Hồng ngọc");
//                return;
//            }
            if (player.gold < (long) count * FEE[sao]) {
                player.service.sendThongBao("Bạn không đủ vàng");
                return;
            }
            boolean isUpgrade = false;
            long gold = 0;
//            int diamond = 0;
            int percent = (int) (PERCENT_REAL[sao] * 100);
            while (!isUpgrade && count > 0 && gold + FEE[sao] <= player.gold) {
//                diamond += DIAMOND[sao];
                count--;
                gold += FEE[sao];
                isUpgrade = Utils.nextInt(10000) < percent;
            }
            player.addGold(-gold);
//            player.addDiamond(-diamond);
            if (isUpgrade) {
                if (option2.param == 0) {
                    item.addItemOption(option2);
                }
                option2.param++;
                player.service.refreshItem((byte) 1, item);
                result((byte) 2);
                if (this.count > 1) {
                    final int count_use = this.count - count;
                    Utils.setTimeout(() -> {
                        player.service.serverMessage2(String.format("Bạn đã đập %d lần", count_use));
                    }, 1000);
                }
            } else {
                result((byte) 3);
            }
        }
    }
}
