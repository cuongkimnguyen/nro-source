/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.NQMP.DHVT_SH;

import com.ngocrong.consts.NpcName;
import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.top.Top;
import com.ngocrong.user.Player;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class DHVT_SH_Service {

    public static final String THONG_TIN_SIEU_HANG = "Giải đấu thể hiện đẳng cấp thực sự\b"
            + "Các trận đấu diễn ra liên tục bất kể ngày đêm\b"
            + "Bạn hãy tham gia thi đấu để nâng hạng\b"
            + "và nhận giải thưởng khủng nhé\n"
            + "Cơ cấu giải thưởng như sau\b"
            + "(chốt và trao giải ngẫu nhiên từ 20h-23h mỗi ngày)\b"
            + "Top 1 thưởng 200 hồng ngọc\bTop 2-10 thưởng 100 hồng ngọc\b"
            + "Top 11-50 thưởng 50 hồng ngọc\n"
            + "Top 51-100 thưởng 10 hồng ngọc\n"
            + "Mỗi ngày các bạn được tặng 1 vé tham dự miễn phí\b"
            + "(tích lũy tối đa 3 vé) khi thua sẽ mất đi 1 vé\b"
            + "Khi hết vé bạn phải trả 200tr vàng để đấu tiếp\b"
            + "(trừ vàng khi trận đấu kết thúc)\n"
            + "Bạn không thể thi đấu với đấu thủ\b"
            + "có hạng nhỏ hơn mình\b"
            + "Chúc bạn may mắn, chào đoàn kết và quyết thắng";

    static DHVT_SH_Service instance;

    public static DHVT_SH_Service gI() {
        if (instance == null) {
            instance = new DHVT_SH_Service();
        }
        Top_SieuHang.load();
        return instance;
    }

    public byte getTicket(Player player) {
        if (player.superrank == null) {
            player.superrank = new SuperRank();
        }
        return player.superrank.ticket;
    }

    public void viewTop(Player player, byte type) {
        if (player == null) {
            return;
        }
        if (player.superrank == null) {
            SuperRank.loadSuperRank(player);
            if (player.superrank == null) {
                checkTop(player);
                SuperRank.loadSuperRank(player);
            }
        }
        if (type == 0) {
            Top_SieuHang.show(player, -1);
            player.viewTop = -1;
        } else {
            if (player.superrank == null) {
                player.superrank = new SuperRank();
            }
            Top_SieuHang.show(player, player.superrank.rank);
            player.viewTop = 0;
        }
    }

    public void sendInfo(Player player) {
        player.service.openUISay(NpcName.CON_MEO, THONG_TIN_SIEU_HANG, player.getPetAvatar());
    }

    public void checkTop(Player player) {
        if (player == null) {
            return;
        }
        if (player.id <= 0) {
            return;
        }
        try {
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement("select * from `nr_super_rank` where player_id = ? limit 1");
            ps.setInt(1, player.id);
            ResultSet rs = ps.executeQuery();
            try {
                boolean found = rs.next();
                if (!found) {
                    try {
                        var p2 = MySQLConnect.getConnection().prepareStatement("INSERT INTO `nr_super_rank` (`player_id`, `rank`, `info`, `data`) VALUES (?,?,?,?)");
                        p2.setInt(1, player.id);
                        p2.setInt(2, player.id);
                        p2.setString(3, "");
                        p2.setString(4, "[-1,-1,-1,-1]");
                        p2.executeUpdate();
                        p2.close();
                        resetAllRank();
                        SuperRank.loadSuperRank(player);
                    } catch (SQLException insertEx) {
                        if (insertEx.getMessage() != null && insertEx.getMessage().contains("Duplicate entry")) {
                            resetAllRank();
                            SuperRank.loadSuperRank(player);
                        } else {
                            throw insertEx;
                        }
                    }
                }
            } catch (Exception e) {
            
                e.printStackTrace();
            } finally {
                rs.close();
                ps.close();
            }
        } catch (Exception ex) {
            
        }
    }

    public void resetAllRank() {
        try {
            var conn = MySQLConnect.getConnection();

            // Xóa các bản ghi không còn tồn tại ở bảng người chơi
            PreparedStatement p0 = conn.prepareStatement("DELETE FROM `nr_super_rank` WHERE `player_id` NOT IN (SELECT `id` FROM `nr_player`)");
            p0.executeUpdate();
            p0.close();

            // Đầu tiên thực thi câu lệnh SET @rank := 0
            PreparedStatement p1 = conn.prepareStatement("SET @rank := 0");
            p1.executeUpdate();
            p1.close();

            // Sau đó thực thi câu lệnh UPDATE
            PreparedStatement p2 = conn.prepareStatement("UPDATE `nr_super_rank` SET `rank` = (@rank := @rank + 1) ORDER BY `rank` ASC");
            p2.executeUpdate();
            p2.close();

            // Reload top list after reset to ensure continuity
            Top_SieuHang.load();

        } catch (SQLException ex) {
            
            Logger.getLogger(DHVT_SH_Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateRank(int pid, int rank) {
        try {
            var conn = MySQLConnect.getConnection();

            // Sau đó thực thi câu lệnh UPDATE
            PreparedStatement p2 = conn.prepareStatement("UPDATE `nr_super_rank` SET `rank` = ? where player_id = ?");
            p2.setInt(1, rank);
            p2.setInt(2, pid);
            int updated = p2.executeUpdate();
            p2.close();

        } catch (SQLException ex) {
            
            Logger.getLogger(DHVT_SH_Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
