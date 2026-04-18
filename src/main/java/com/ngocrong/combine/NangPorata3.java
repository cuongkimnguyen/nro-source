package com.ngocrong.combine;

import com.ngocrong.item.ItemOption;
import com.ngocrong.util.Utils;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.lib.KeyValue;

public class NangPorata3 extends Combine {

    public static final int PERCENT = 50;
    public static final int[] REQUIRE = {1, 1000, 20, 100};

    public NangPorata3() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang\n");
        sb.append("Chọn bông tai Porata cấp 2\n");
        sb.append("Chọn mảnh vỡ bông tai cấp 3 số lượng 1000 cái\n");
        sb.append("Sau đó chọn 'Nâng cấp'\n");
        setInfo(sb.toString());

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Ta sẽ phù phép\n");
        sb2.append("Cho bông tai Porata của ngươi\n");
        sb2.append("thành cấp 3");
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
        if (player.isNhapThe()) {
            player.service.dialogMessage("Vui lòng tách hợp thể ra trước");
            return;
        }
        String text = String.format("Cần %d bông tai Porata cấp 2 và %d mảnh vỡ bông tai cấp 3", REQUIRE[0], REQUIRE[1]);
        if (itemCombine.size() < 2) {
            player.service.dialogMessage(text);
            return;
        }
        Item[] items = new Item[3];
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.template.id == ItemName.BONG_TAI_PORATA_CAP_2) {
                    items[0] = item;
                }
                if (item.template.id == ItemName.MANH_VO_BONG_TAI_CAP_3) {
                    items[1] = item;
                }
                if (item.template.id == ItemName.THOI_VANG) {
                    items[2] = item;
                }
            }
        }
        if (items[0] == null || items[1] == null) {
            player.service.dialogMessage(text);
            return;
        }
        if (items[1].quantity < REQUIRE[1]) {
            player.service.dialogMessage("Cần x1000 Mảnh vỡ bông tai cấp 3");
            return;
        }
        if (items[0].options.size() < 2) {
            player.service.dialogMessage("Bông tai cấp 2 cần phải mở chỉ số trước");
            return;
        }
        int availableGoldBars = 0;
        if (items[2] != null) {
            availableGoldBars = items[2].quantity;
        } else {
            int indexGoldBar = player.getIndexBagById(ItemName.THOI_VANG);
            if (indexGoldBar >= 0) {
                Item goldBarInBag = player.itemBag[indexGoldBar];
                if (goldBarInBag != null) {
                    availableGoldBars = goldBarInBag.quantity;
                }
            }
        }
        if (availableGoldBars < REQUIRE[2]) {
            player.service.dialogMessage(String.format("Cần thêm %d thỏi vàng", REQUIRE[2]));
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("|2|").append(String.format("%s [+%d]", items[0].template.name, 3)).append("\n");
        sb.append(String.format("Tỉ lệ thành công: %d%%", PERCENT)).append("\n");
        sb.append(String.format("Cần %d Mảnh vỡ bông tai cấp 3", REQUIRE[1])).append("\n");
        sb.append(String.format("Cần %d Thỏi vàng", REQUIRE[2])).append("\n");
        sb.append("|7|").append(String.format("Thất bại -%d Mảnh vỡ bông tai cấp 3", REQUIRE[3]));
        player.menus.clear();
        player.menus.add(new KeyValue(CMDMenu.COMBINE, String.format("Nâng cấp\n%s thỏi vàng", Utils.formatNumber(REQUIRE[2]))));
        player.menus.add(new KeyValue(CMDMenu.CANCEL, "Từ chối"));
        player.service.openUIConfirm(npc.templateId, sb.toString(), npc.avatar, player.menus);
    }

    @Override
    public void combine() {
        if (itemCombine == null) {
            return;
        }
        if (player.isNhapThe()) {
            player.service.dialogMessage("Vui lòng tách hợp thể ra trước");
            return;
        }
        String text = String.format("Cần %d bông tai Porata cấp 2 và %d mảnh vỡ bông tai cấp 3", REQUIRE[0], REQUIRE[1]);
        if (itemCombine.size() < 2) {
            player.service.dialogMessage(text);
            return;
        }
        Item[] items = new Item[3];
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.template.id == ItemName.BONG_TAI_PORATA_CAP_2) {
                    items[0] = item;
                }
                if (item.template.id == ItemName.MANH_VO_BONG_TAI_CAP_3) {
                    items[1] = item;
                }
                if (item.template.id == ItemName.THOI_VANG) {
                    items[2] = item;
                }
            }
        }
        if (items[0] == null || items[1] == null) {
            player.service.dialogMessage(text);
            return;
        }
        if (items[1].quantity < REQUIRE[1]) {
            player.service.dialogMessage(text);
            return;
        }
        Item goldBarToUse = null;
        int goldBarIndex = -1;
        if (items[2] != null && items[2].quantity >= REQUIRE[2]) {
            goldBarToUse = items[2];
            goldBarIndex = items[2].indexUI;
        } else {
            goldBarIndex = player.getIndexBagById(ItemName.THOI_VANG);
            if (goldBarIndex >= 0) {
                goldBarToUse = player.itemBag[goldBarIndex];
            }
        }
        if (goldBarToUse == null || goldBarToUse.quantity < REQUIRE[2]) {
            player.service.serverMessage2("Không đủ thỏi vàng");
            return;
        }
        player.removeItem(goldBarIndex, REQUIRE[2]);

        if (Utils.nextInt(100) < PERCENT) {
            items[1].quantity -= REQUIRE[1];
            Item item = new Item(ItemName.BONG_TAI_PORATA_CAP_3);
            for (ItemOption o : items[0].options) {
                int id = o.optionTemplate.id;
                int param = o.param;
                if (id == 72) {
                    param = 3;
                }
                item.addItemOption(new ItemOption(id, param));
            }
            item.indexUI = items[0].indexUI;
            player.itemBag[item.indexUI] = item;
            result((byte) 2);
        } else {
            items[1].quantity -= REQUIRE[3];
            result((byte) 3);
        }
        if (items[1].quantity <= 0) {
            player.itemBag[items[1].indexUI] = null;
        }
        player.service.setItemBag();
        update();
    }
}
