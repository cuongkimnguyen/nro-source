package com.ngocrong.NQMP.DHVT_SH;

import com.ngocrong.NQMP.Whis.RewardWhis;
import com.ngocrong.item.Item;
import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;

public class SuperRank {

    public int rank;
    public byte ticket;
    public long lastBonusTicket;
    public long lastAttack;
    public long lastReward;
    public static long DAY = 1000 * 60 * 60 * 24;
    public static long HalfDay = 1000 * 60 * 60 * 12;

    public SuperRank() {
        ticket = 3;
        lastAttack = System.currentTimeMillis();
        lastBonusTicket = System.currentTimeMillis();
    }

    public static SuperRank getJSON(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            SuperRank rank = new SuperRank();

            if (jsonArray.length() >= 2) {
                rank.ticket = (byte) jsonArray.getInt(0);
                rank.lastAttack = jsonArray.getLong(1);

                if (jsonArray.length() >= 3) {
                    rank.lastBonusTicket = jsonArray.getLong(2);
                }
                if (jsonArray.length() >= 4) {
                    rank.lastReward = jsonArray.getLong(3);
                }

                if (System.currentTimeMillis() - rank.lastBonusTicket >= DAY) {
                    rank.lastBonusTicket = System.currentTimeMillis();
                    if (rank.ticket < 3) {
                        rank.ticket++;
                    }
                }
            }

            return rank;
        } catch (JSONException e) {
            // Xử lý ngoại lệ nếu có
            e.printStackTrace();
            return new SuperRank();
        }
    }

    public static void loadSuperRank(Player player) {
        if (player.superrank != null) {
            return;
        }
        try {
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement("select * from `nr_super_rank` where player_id = ? limit 1");
            ps.setInt(1, player.id);
            ResultSet rs = ps.executeQuery();
            try {
                boolean found = rs.next();
                if (found) {
                    String data = rs.getString("data");
                    if (!data.equals("[-1,-1,-1,-1]")) {
                        player.superrank = getJSON(data);
                    } else {
                        player.superrank = new SuperRank();
                    }
                    player.superrank.rank = rs.getInt("rank");
                }
            } catch (Exception e) {
                
                e.printStackTrace();
            } finally {
                rs.close();
                ps.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveSuperRank(Player player) {
        try {
            if (player == null || player.superrank == null) {
                return;
            }
            try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement("update nr_super_rank set data = ? where player_id = ?")) {
                ps.setString(1, player.superrank.toJSON());
                ps.setInt(2, player.id);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(SuperRank.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String toJSON() {
        var rank = this;
        if (rank == null) {
            return String.format("[%d,%d,%d,%d]", 3, System.currentTimeMillis(), System.currentTimeMillis(), System.currentTimeMillis());
        }
        return String.format("[%d,%d,%d,%d]", rank.ticket, rank.lastAttack, rank.lastBonusTicket, rank.lastReward);
    }

    public static void checkReward(Player player) {
        Utils.setTimeout(()
                -> {
            if (player.superrank.rank < 100 && System.currentTimeMillis() - player.superrank.lastAttack >= 30 * 60000 && System.currentTimeMillis() - player.superrank.lastReward >= HalfDay) {

                var rank = player.superrank.rank;
                player.superrank.lastReward = System.currentTimeMillis();
                player.superrank.lastAttack = System.currentTimeMillis();
                int ruby = 10;
                if (rank == 1) {
                    ruby = 200;
                } else if (rank >= 2 && rank <= 10) {
                    ruby = 100;
                } else if (rank >= 11 && rank <= 50) {
                    ruby = 50;
                } else if (rank >= 51 && rank <= 100) {
                    ruby = 10;
                }
                player.addDiamondLock(ruby);
                player.service.dialogMessage(String.format(
                        "Bạn đạt Top %d ở Giải Đấu Siêu Hạng\n"
                        + "Bạn nhận được %d hồng ngọc", player.superrank.rank, ruby));
            }

        },
                5000);

    }

}
