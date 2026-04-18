/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.user.func;

import _HunrProvision.HoangAnhDz;
import com.ngocrong.consts.NpcName;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Administrator
 */
public class BaiSu {

    public static void Action(Player player1, Player player2) {
        if (player1 == null || player2 == null) {
            return;
        }

        boolean isFriend1 = player1.friends != null && player1.friends.stream().anyMatch(f -> f.id == player2.id);
        boolean isFriend2 = player2.friends != null && player2.friends.stream().anyMatch(f -> f.id == player1.id);
        if (!(isFriend1 && isFriend2)) {
            player1.service.sendThongBao("Hai người chưa kết bạn với nhau");
            return;
        }

        if (player1.baiSu_id != -1) {
            if (player1.baiSu_id == player2.id) {
                player1.service.sendThongBao("2 Bạn đã là sư đồ với nhau rồi");

            } else {
                player1.service.sendThongBao("Bạn đã có sư - đệ rồi");
            }
            return;
        }
        if (player2.baiSu_id != -1) {

            return;
        }
        player1.service.sendThongBao("Đã gửi lời mời rồi , hãy chờ đối phương đồng ý");

        var menus = player2.menus;
        menus.add(new KeyValue(303, "Đồng ý", player1.id));
        menus.add(new KeyValue(-1, "Từ chối", player1));
        player2.service.openUIConfirm(NpcName.CON_MEO, String.format("%s muốn bái bạn làm sư phụ\nbạn có đồng ý không ?\n"
                + "Khi cả 2 là sư đồ với nhau và train với nhau sẽ được tăng TNSM",
                player1.name), player2.getPetAvatar(), menus);
    }

    public static void accept(Player player1, Player player2) {
        player1.baiSu_id = player2.id;
        player2.baiSu_id = player1.id;
        insert(player1.id, player2.id);
        player1.service.dialogMessage(String.format("Bạn và %s đã trở thành sư đồ với nhau", player2.name));
        player2.service.dialogMessage(String.format("Bạn và %s đã trở thành sư đồ với nhau", player1.name));

    }

    public static void insert(int id1, int id2) {
        HoangAnhDz.ExcuteQuery(String.format("INSERT INTO `nr_baisu` (`player1`, `player2`) VALUES (%d, %d);", id1, id2));
    }
    
     public static void delete(int id1) {
        HoangAnhDz.ExcuteQuery(String.format("DELETE FROM `nr_baisu` where player1 = %s or player2 = %s", id1, id1));
    }

    public static int getBaisuId(int playerId) {
        try {
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(
                    "SELECT player1, player2 FROM nr_baisu WHERE player1 = ? OR player2 = ?");
            ps.setInt(1, playerId);
            ps.setInt(2, playerId);
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    int p1 = rs.getInt("player1");
                    int p2 = rs.getInt("player2");
                    return p1 == playerId ? p2 : p1;
                }
            } finally {
                rs.close();
                ps.close();
            }
        } catch (Exception e) {
            // HoangAnhDz.logError(e);
        }
        return -1;
    }
}
