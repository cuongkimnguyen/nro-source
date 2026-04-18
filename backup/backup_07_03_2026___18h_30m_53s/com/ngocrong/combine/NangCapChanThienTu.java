package com.ngocrong.combine;

import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.util.Utils;

public class NangCapChanThienTu extends Combine {

    // {tinh the, ma quai, percent display, percent real, bonus stat}
    private static final double[][] DATA = {
        {5, 20, 70, 30, 1},
        {10, 40, 50, 15, 2},
        {15, 60, 30, 10, 3},
        {20, 80, 20, 5, 4},
        {30, 120, 10, 3, 5},
        {40, 160, 5, 2, 6},
        {50, 200, 3, 1.5, 7},
        {75, 300, 2, 1, 8}
    };
    private static final int[] STAT_IDS = {77, 103, 50}; // HP, KI, SD
    private static final long goldRequire = 1_000_000_000;
    public int count;

    public NangCapChanThienTu() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang\n");
        sb.append("Chọn Chân thiên tử và vật phẩm cần thiết\n");
        sb.append("Sau đó chọn 'Nâng cấp'");
        setInfo(sb.toString());

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Ta sẽ phù phép\n");
        sb2.append("Chân thiên tử của ngươi trở nên mạnh mẻ");
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
        if (itemCombine.size() > 3) {
            player.service.dialogMessage("Cần Chân thiên tử, Tinh thể và Ma quái");
            return;
        }
        Item chan = null;
        Item tinh = null;
        Item ma = null;
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item == null) {
                continue;
            }
            int id = item.template.id;
            if (id >= ItemName.CHAN_THIEN_TU_CAP_1 && id <= ItemName.CHAN_THIEN_TU_CAP_8) {
                chan = item;
            } else if (id == ItemName.TINH_THE) {
                tinh = item;
            } else if (id == ItemName.MA_QUAI) {
                ma = item;
            }
        }
        if (chan == null) {
            player.service.dialogMessage("Cần Chân thiên tử");
            return;
        }
        if (chan.getItemOption(93) != null) {
            player.service.dialogMessage("Không thể nâng cấp Chân thiên tử có hạn sử dụng");
            return;
        }
        int level = chan.template.id - ItemName.CHAN_THIEN_TU_CAP_1 + 1;
        if (level >= 9) {
            player.service.dialogMessage("Chân thiên tử đã đạt cấp tối đa");
            return;
        }
        int idx = level - 1;
        int requireTinh = (int) DATA[idx][0];
        int requireMa = (int) DATA[idx][1];
        if (tinh == null || ma == null) {
            player.service.dialogMessage(String.format("Cần %d Tinh thể và %d Ma quái", requireTinh, requireMa));
            return;
        }
        if (tinh.quantity < requireTinh || ma.quantity < requireMa) {
            player.service.dialogMessage(String.format("Cần %d Tinh thể và %d Ma quái", requireTinh, requireMa));
            return;
        }
        double percentDisplay = DATA[idx][2];
        StringBuilder sb = new StringBuilder();
        sb.append("|2|Chân Mệnh Thiên Tử Cấp ").append(level).append(" -> Cấp ").append(level + 1).append("\n");
        sb.append("Tỉ lệ thành công: ").append(percentDisplay).append("%\n");
        sb.append("Cần ").append(requireTinh).append(" Tinh thể\n");
        sb.append("Cần ").append(requireMa).append(" Ma quái\n");
        sb.append("Cần ").append("1 tỷ vàng");
        player.menus.clear();
        player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng cấp\n1 lần", 1));
        if (tinh.quantity >= requireTinh * 10 && ma.quantity >= requireMa * 10 && player.gold >= goldRequire * 10) {
            player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng cấp\n10 lần", 10));
        }
        if (tinh.quantity >= requireTinh * 100 && ma.quantity >= requireMa * 100 && player.gold >= goldRequire * 100) {
            player.menus.add(new KeyValue(CMDMenu.COMBINE, "Nâng cấp\n100 lần", 100));
        }
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
        player.service.openUIConfirm(npc.templateId, sb.toString(), npc.avatar, player.menus);
    }

    @Override
    public void combine() {
        if (itemCombine == null) {
            return;
        }
        if (itemCombine.size() > 3) {
            player.service.dialogMessage("Cần Chân thiên tử, Tinh thể và Ma quái");
            return;
        }
        Item chan = null;
        Item tinh = null;
        Item ma = null;
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item == null) {
                continue;
            }
            int id = item.template.id;
            if (id >= ItemName.CHAN_THIEN_TU_CAP_1 && id <= ItemName.CHAN_THIEN_TU_CAP_8) {
                chan = item;
            } else if (id == ItemName.TINH_THE) {
                tinh = item;
            } else if (id == ItemName.MA_QUAI) {
                ma = item;
            }
        }
        if (chan == null) {
            player.service.dialogMessage("Cần Chân thiên tử");
            return;
        }
        if (chan.getItemOption(93) != null) {
            player.service.sendThongBao("Không thể nâng cấp Chân thiên tử có hạn sử dụng");
            return;
        }
        int level = chan.template.id - ItemName.CHAN_THIEN_TU_CAP_1 + 1;
        if (level >= 9) {
            player.service.dialogMessage("Chân thiên tử đã đạt cấp tối đa");
            return;
        }
        int idx = level - 1;
        int requireTinh = (int) DATA[idx][0];
        int requireMa = (int) DATA[idx][1];
        if (tinh == null || ma == null) {
            player.service.dialogMessage(String.format("Cần %d Tinh thể và %d Ma quái", requireTinh, requireMa));
            return;
        }
        int count = this.count;
        if (tinh.quantity < requireTinh * count || ma.quantity < requireMa * count) {
            player.service.sendThongBao(String.format("Cần %d Tinh thể và %d Ma quái", requireTinh * count, requireMa * count));
            return;
        }
        if (player.gold < goldRequire * count) {
            player.service.sendThongBao(String.format("Cần %d tỷ vàng", count));
            return;
        }
        int usedTinh = 0;
        int usedMa = 0;
        long gold = 0;
        boolean success = false;
        double percentReal = DATA[idx][3];
        while (!success && count > 0 && usedTinh + requireTinh <= tinh.quantity && usedMa + requireMa <= ma.quantity) {
            int levelUp = chan.template.id - 2410;
            usedTinh += requireTinh;
            usedMa += requireMa;
            gold += goldRequire;
            count--;
            success = Utils.isTrue(percentReal, levelUp < 4 ? 100d : 200d);
        }
        player.addGold(-gold);
        player.removeItem(tinh.indexUI, usedTinh);
        player.removeItem(ma.indexUI, usedMa);
        if (success) {
            int nextId = chan.template.id + 1;
            Item newItem = new Item(nextId);
            newItem.quantity = 1;
            for (ItemOption op : chan.options) {
                newItem.addItemOption(new ItemOption(op.optionTemplate.id, op.param));
            }
            int optionId = STAT_IDS[Utils.nextInt(STAT_IDS.length)];
            int bonus = (int) DATA[idx][4];
            ItemOption opt = newItem.getItemOption(optionId);
            if (opt == null) {
                newItem.addItemOption(new ItemOption(optionId, bonus));
            } else {
                opt.param += bonus;
            }
            newItem.indexUI = chan.indexUI;
            player.itemBag[chan.indexUI] = newItem;
            result((byte) 2);
            if (this.count > 1) {
                int count_use = this.count - count;
                Utils.setTimeout(() -> player.service.serverMessage2(String.format("Bạn đã đập %d lần", count_use)), 1000);
            }
        } else {
            result((byte) 3);
        }
        player.service.setItemBag();
        update();
    }
}
