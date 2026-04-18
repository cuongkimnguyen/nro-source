/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _HunrProvision.minigame;

import com.ngocrong.item.Item;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.user.Player;
import com.ngocrong.util.Collor;
import com.ngocrong.util.Utils;

/**
 *
 * @author Administrator
 */
public class KeoBuaBao {

    public static final byte KEO = 1;
    public static final byte BUA = 2;
    public static final byte BAO = 3;

    public static void start(Player player) {
        if (player.mucCuoc == -1) {
            showMucCuoc(player);
        } else {
            String show = Collor.GREENB;
            show += player.lastPlayerSelect == -1 ? "" : getStr(player.lastServerSelect, player.lastPlayerSelect);
            show += String.format("Mức cược :%d thỏi vàng\n", player.mucCuoc);
            show += "Hãy chọn Kéo,Búa hoặc Bao\n";
            player.menus.add(new KeyValue(1123, "Kéo"));
            player.menus.add(new KeyValue(1124, "Búa"));
            player.menus.add(new KeyValue(1125, "Bao"));
            player.menus.add(new KeyValue(1126, "Đổi mức cược"));
            player.service.openUIConfirm(54, show, (short) 3049, player.menus);
        }

    }

    public static void showMucCuoc(Player player) {

        player.menus.add(new KeyValue(1119, "5 thỏi vàng"));
        player.menus.add(new KeyValue(1120, "10 thỏi vàng"));
        player.menus.add(new KeyValue(1121, "50 thỏi vàng"));
        player.menus.add(new KeyValue(1122, "100 thỏi vàng"));
        player.service.openUIConfirm(54, "Hãy chọn mức cược", (short) 3049, player.menus);
    }

    static String getStr(byte ServerSelect, byte playerSelect) {
        String playerChoice = "", serverChoice = "", result = "";

        // Chuyển đổi lựa chọn thành chuỗi
        switch (playerSelect) {
            case KEO:
                playerChoice = "Kéo";
                break;
            case BUA:
                playerChoice = "Búa";
                break;
            case BAO:
                playerChoice = "Bao";
                break;
        }

        switch (ServerSelect) {
            case KEO:
                serverChoice = "Kéo";
                break;
            case BUA:
                serverChoice = "Búa";
                break;
            case BAO:
                serverChoice = "Bao";
                break;
        }

        // Xác định kết quả
        if (playerSelect == ServerSelect) {
            result = "Hòa";
        } else if ((playerSelect == KEO && ServerSelect == BAO)
                || (playerSelect == BUA && ServerSelect == KEO)
                || (playerSelect == BAO && ServerSelect == BUA)) {
            result = "Bạn thắng";
        } else {
            result = "Bạn thua";
        }

        return String.format("Bạn ra cái <%s>"
                + "\n Tôi ra cái <%s>"
                + "\n" + Collor.DARKB + "Kết quả: %s\n",
                playerChoice,
                serverChoice,
                result
        );
    }

    public static void StartGame(int muccuoc, Player player, byte playerSelect) {

        if (player == null || muccuoc < 0 || muccuoc > 1000 || playerSelect <= 0 || playerSelect > 3) {
            return;
        }
        if (player.getSession().user.getActivated() != 1) {
            player.service.sendThongBao("Bạn cần kích hoạt tài khoản để sử dụng tính năng này");
            return;
        }
        Item thoivang = player.getItemInBag(457);
        if (thoivang == null || thoivang.quantity < muccuoc) {
            player.service.sendThongBao("Bạn không đủ thỏi vàng để chơi");
            return;
        }
        player.removeItem(thoivang.indexUI, muccuoc);

        // Xác định kết quả theo tỷ lệ
        double rand = Math.random() * 100;
        int result;
        if (rand < 60) { // 50% server thắng
            result = -1;
        } else if (rand < 80) { // 20% hòa
            result = 0;
        } else { // 30% player thắng
            result = 1;
        }

        // Chọn lựa chọn cho bot dựa vào kết quả mong muốn
        byte botSelect;
        if (result == 0) { // Muốn hòa
            botSelect = playerSelect; // Chọn giống player
        } else if (result == 1) { // Muốn player thắng
            switch (playerSelect) {
                case KEO:
                    botSelect = BAO; // Kéo thắng Bao
                    break;
                case BUA:
                    botSelect = KEO; // Búa thắng Kéo
                    break;
                default:
                    botSelect = BUA; // Bao thắng Búa
                    break;
            }
        } else { // Muốn server thắng
            switch (playerSelect) {
                case KEO:
                    botSelect = BUA; // Búa thắng Kéo
                    break;
                case BUA:
                    botSelect = BAO; // Bao thắng Búa
                    break;
                default:
                    botSelect = KEO; // Kéo thắng Bao
                    break;
            }
        }

        player.service.sendThongBao("Bạn hãy chờ 1 chút để ra kết quả");
        player.lastServerSelect = botSelect;
        player.lastPlayerSelect = playerSelect;

        Utils.setTimeout(() -> {
            switch (result) {
                case 1:
                    // Thắng nhận x2
                    Item reward = new Item(457);
                    reward.quantity = muccuoc * 2;
                    player.addItem(reward);
                    player.service.sendThongBao("Chúc mừng! Bạn đã thắng " + (muccuoc * 2) + " thỏi vàng!");
                    break;
                case 0:
                    // Hòa hoàn tiền
                    Item returnGold = new Item(457);
                    returnGold.quantity = muccuoc;
                    player.addItem(returnGold);
                    player.service.sendThongBao("Hòa! Bạn nhận lại " + muccuoc + " thỏi vàng!");
                    break;
                default:
                    player.service.sendThongBao("Rất tiếc, bạn đã thua!");
                    break;
            }
            start(player);
        }, 3000);
    }
}
