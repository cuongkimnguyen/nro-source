package com.ngocrong.NQMP.DHVT_SH;

import com.ngocrong.server.SessionManager;
import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RewardSuperRank {

    private static final Logger LOGGER = Logger.getLogger(RewardSuperRank.class.getName());

    private static int rewardForRank(int rank) {
        if (rank == 1) {
            return 200;
        } else if (rank >= 2 && rank <= 10) {
            return 100;
        } else if (rank >= 11 && rank <= 50) {
            return 50;
        } else if (rank >= 51 && rank <= 100) {
            return 10;
        }
        return 0;
    }

    public static synchronized void reward() {
        try {
            LOGGER.log(Level.SEVERE, "Start reward SuperRank");
            Top_SieuHang.load();
            long now = System.currentTimeMillis();

            // Tạo bản sao của danh sách để tránh ConcurrentModificationException
            List<Top_SieuHang> topList;
            synchronized (Top_SieuHang.elements) {
                topList = new ArrayList<>(Top_SieuHang.elements);
            }

            for (Top_SieuHang top : topList) {
                int ruby = rewardForRank(top.rank);
                LOGGER.log(Level.SEVERE, "Start reward SuperRank 1");
                if (ruby <= 0) {
                    continue;
                }
                Player pl = SessionManager.findChar(top.player_Id);
                if (pl != null) {
                    // Synchronize việc thêm diamond để tránh race condition
                    synchronized (pl) {
                        pl.addDiamondLock(ruby);
                        pl.service.dialogMessage(String.format(
                                "Bạn đạt Top %d ở Giải Đấu Siêu Hạng\nBạn nhận được %d hồng ngọc",
                                top.rank, ruby));
                    }
                } else {
                    try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(
                            "INSERT INTO nr_top_superrank (player_id, rank, create_date) VALUES (?, ?, ?)")) {
                        ps.setInt(1, top.player_Id);
                        ps.setInt(2, top.rank);
                        ps.setTimestamp(3, new Timestamp(now));
                        ps.executeUpdate();
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.log(Level.SEVERE, "Error insert pending reward", e);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Error reward super rank", e);
        }
    }

    public static void checkReward(Player player) {
        if (player == null) {
            return;
        }
        Utils.setTimeout(() -> {
            try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(
                    "SELECT * FROM nr_top_superrank WHERE player_id = ? ORDER BY id DESC LIMIT 1")) {
                ps.setInt(1, player.id);
                var rs = ps.executeQuery();
                try {
                    if (rs.next()) {
                        int rank = rs.getInt("rank");
                        long create = rs.getTimestamp("create_date").getTime();
                        long diff = System.currentTimeMillis() - create;
                        if (diff <= 24L * 60 * 60 * 1000) {
                            int ruby = rewardForRank(rank);
                            if (ruby > 0) {
                                player.addDiamondLock(ruby);
                                player.service.dialogMessage(String.format(
                                        "Bạn đạt Top %d ở Giải Đấu Siêu Hạng\nBạn nhận được %d hồng ngọc",
                                        rank, ruby));
                            }
                        }
                    }
                } finally {
                    rs.close();
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error check reward", e);
            }
            try (PreparedStatement del = MySQLConnect.getConnection().prepareStatement(
                    "DELETE FROM nr_top_superrank WHERE player_id = ?")) {
                del.setInt(1, player.id);
                del.executeUpdate();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error delete pending reward", e);
            }
        }, 5000);
    }
}
