package com.ngocrong.combine;

import _HunrProvision.boss.Boss;
import com.ngocrong.item.ItemOption;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.util.Utils;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.lib.KeyValue;
import org.apache.log4j.Logger;

import java.time.Instant;

public class NangPorata extends Combine {

    private static final org.apache.log4j.Logger logger = Logger.getLogger(NangPorata.class);
    public static final int PERCENT = 100;
    public static final int[] REQUIRE = {0, 9999, 20, 50};

    public NangPorata() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang");
        sb.append("\n");
        sb.append("Chọn bông tai Porata");
        sb.append("\n");
        sb.append("Chọn mảnh bông tai để nâng cấp, số lượng 9999 cái");
        sb.append("\n");
        sb.append("Sau đó chọn 'Nâng cấp'");
        sb.append("\n");
        setInfo(sb.toString());

        StringBuilder sb2 = new StringBuilder();
        sb2.append("Ta sẽ phù phép");
        sb2.append("\n");
        sb2.append("Cho bông tai Porata của ngươi");
        sb2.append("\n");
        sb2.append("thành cấp 2");
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

        // Thay đổi logic: chỉ cần 2 item chính (Porata + Mảnh vỡ)
        String text = String.format("Cần %d bông tai Porata và %d mảnh vỡ bông tai", REQUIRE[0], REQUIRE[1]);

        // Kiểm tra tối thiểu phải có 2 item (không bắt buộc phải có thỏi vàng trong combo)
        if (itemCombine.size() < 2) {
            player.service.dialogMessage(text);
            return;
        }

        Item[] items = new Item[3]; // [0] = Porata, [1] = Mảnh vỡ, [2] = Thỏi vàng (nullable)

        // Tìm các item trong combo
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.template.id == 454) { // Bông tai Porata
                    items[0] = item;
                }
                if (item.template.id == 933) { // Mảnh vỡ bông tai
                    items[1] = item;
                }
                if (item.template.id == 457) { // Thỏi vàng (optional)
                    items[2] = item;
                }
            }
        }

        // Kiểm tra 2 item bắt buộc
        if (items[0] == null || items[1] == null) {
            player.service.dialogMessage(text);
            return;
        }

        // Kiểm tra số lượng item bắt buộc
        if (items[0].quantity < REQUIRE[0]) {
            player.service.dialogMessage("Cần 1 Bông tai Porata");
            return;
        }
        if (items[1].quantity < REQUIRE[1]) {
            player.service.dialogMessage("Cần x9999 Mảnh vỡ bông tai");
            return;
        }

        // Kiểm tra thỏi vàng (từ combo hoặc hành trang)
        int availableGoldBars = 0;
        if (items[2] != null) {
            // Có thỏi vàng trong combo
            availableGoldBars = items[2].quantity;
        } else {
            // Tìm thỏi vàng trong hành trang
            int indexGoldBar = player.getIndexBagById(457);
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
        sb.append("|2|").append(String.format("%s [+%d]", items[0].template.name, 2)).append("\n");
        sb.append(String.format("Tỉ lệ thành công: %d%%", PERCENT)).append("\n");
        sb.append(String.format("Cần %d Mảnh vỡ bông tai", REQUIRE[1])).append("\n");
        sb.append(String.format("Cần %d Thỏi vàng", REQUIRE[2])).append("\n");
        sb.append("|7|").append(String.format("Thất bại -%d Mảnh vỡ bông tai", REQUIRE[3]));

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

        String text = String.format("Cần %d bông tai Porata và %d mảnh vỡ bông tai", REQUIRE[0], REQUIRE[1]);
        if (itemCombine.size() < 2) {
            player.service.dialogMessage(text);
            return;
        }

        Item[] items = new Item[3]; // [0] = Porata, [1] = Mảnh vỡ, [2] = Thỏi vàng (nullable)

        // Tìm các item trong combo
        for (byte index : this.itemCombine) {
            Item item = player.itemBag[index];
            if (item != null) {
                if (item.template.id == ItemName.BONG_TAI_PORATA) {
                    items[0] = item;
                }
                if (item.template.id == ItemName.MANH_VO_BONG_TAI) {
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

        // Kiểm tra số lượng item chính
        if (items[0].quantity < REQUIRE[0] || items[1].quantity < REQUIRE[1]) {
            player.service.dialogMessage(text);
            return;
        }

        // Logic xử lý thỏi vàng thông minh
        Item goldBarToUse = null;
        int goldBarIndex = -1;

        if (items[2] != null && items[2].quantity >= REQUIRE[2]) {
            // Sử dụng thỏi vàng từ combo
            goldBarToUse = items[2];
            goldBarIndex = items[2].indexUI;
        } else {
            // Tìm thỏi vàng trong hành trang
            goldBarIndex = player.getIndexBagById(ItemName.THOI_VANG);
            if (goldBarIndex >= 0) {
                goldBarToUse = player.itemBag[goldBarIndex];
            }
        }

        // Kiểm tra cuối cùng về thỏi vàng
        if (goldBarToUse == null || goldBarToUse.quantity < REQUIRE[2]) {
            player.service.serverMessage2("Không đủ thỏi vàng");
            return;
        }

        // Thực hiện nâng cấp
        player.pointThoiVang += REQUIRE[2];
        player.isChangePoint = true;
        player.removeItem(goldBarIndex, REQUIRE[2]);

        if (true) { // Luôn thành công (PERCENT = 100)
            items[1].quantity -= REQUIRE[1];
            Item item = new Item(ItemName.BONG_TAI_PORATA_CAP_2);
            item.quantity = 1;
            item.addItemOption(new ItemOption(72, 2));
            item.indexUI = items[0].indexUI;
            player.itemBag[item.indexUI] = item;
            result((byte) 2);
        } else {
            items[1].quantity -= REQUIRE[3];
            result((byte) 3);
        }

        // Cleanup item đã hết
        if (items[1].quantity <= 0) {
            player.itemBag[items[1].indexUI] = null;
        }

        player.service.setItemBag();
        update();
    }
}
