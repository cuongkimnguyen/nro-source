package _event.newyear_2026;

import _HunrProvision.ConfigStudio;
import com.ngocrong.NQMP.DHVT_SH.DHVT_SH_Service;
import com.ngocrong.NQMP.DHVT_SH.SuperRank;
import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.user.Player;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EventNewYear2026 {

    private static final Logger logger = Logger.getLogger(EventNewYear2026.class);
    private static final int[] POINTS_BY_RANK = {20, 18, 16, 14, 12, 10, 8, 6, 4, 2};
    private static final int MAX_RANK_FOR_POINTS = 10;
    public static final boolean MODE_DEBUG = false;
    public static final long HOLD_TIME_REQUIRED = MODE_DEBUG ? (1000L * 60 * 2) : (1000L * 60 * 30);

    public static boolean isActive() {
        return ConfigStudio.EVENT_NEWYEAR_2026;
    }

    public static NewYear2026RankData getOrCreateData(Player player) {
        if (player == null) {
            return null;
        }
        if (player.id <= 0) {
            return null;
        }
        if (!isActive()) {
            return null;
        }

        if (player.superrank == null) {
            SuperRank.loadSuperRank(player);
            if (player.superrank == null) {
                DHVT_SH_Service.gI().checkTop(player);
                SuperRank.loadSuperRank(player);
            }
        }

        if (player.superrank == null) {
            return null;
        }

        try {
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(
                    "SELECT event_newyear2026 FROM nr_super_rank WHERE player_id = ? LIMIT 1");
            ps.setInt(1, player.id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String eventData = rs.getString("event_newyear2026");
                rs.close();
                ps.close();
                if (eventData != null && !eventData.isEmpty() && !eventData.equals("null")) {
                    NewYear2026RankData data = NewYear2026RankData.fromJSON(eventData);
                    if (data.currentRank != player.superrank.rank && !data.isHoldingRank()) {
                        data.currentRank = player.superrank.rank;
                    }
                    return data;
                }
            } else {
                rs.close();
                ps.close();
            }

            NewYear2026RankData newData = new NewYear2026RankData();
            newData.currentRank = player.superrank.rank;
            saveData(player, newData);
            return newData;
        } catch (SQLException e) {
            logger.error("Error loading NewYear2026 data", e);
            return new NewYear2026RankData();
        }
    }

    public static void saveData(Player player, NewYear2026RankData data) {
        if (player == null || data == null || !isActive()) {
            return;
        }
        if (player.id <= 0) {
            return;
        }

        try {
            if (player.superrank == null) {
                SuperRank.loadSuperRank(player);
                if (player.superrank == null) {
                    DHVT_SH_Service.gI().checkTop(player);
                    SuperRank.loadSuperRank(player);
                }
            }
            if (player.superrank == null) {
                return;
            }

            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(
                    "UPDATE nr_super_rank SET event_newyear2026 = ? WHERE player_id = ?");
            ps.setString(1, data.toJSON());
            ps.setInt(2, player.id);
            int updated = ps.executeUpdate();
            ps.close();
            
            if (updated == 0) {
                try {
                    PreparedStatement insertPs = MySQLConnect.getConnection().prepareStatement(
                            "INSERT INTO nr_super_rank (player_id, rank, info, data, event_newyear2026) VALUES (?, ?, ?, ?, ?)");
                    insertPs.setInt(1, player.id);
                    insertPs.setInt(2, player.superrank.rank);
                    insertPs.setString(3, "");
                    insertPs.setString(4, player.superrank.toJSON());
                    insertPs.setString(5, data.toJSON());
                    insertPs.executeUpdate();
                    insertPs.close();
                } catch (SQLException insertEx) {
                    if (insertEx.getMessage() != null && insertEx.getMessage().contains("Duplicate entry")) {
                        PreparedStatement updatePs = MySQLConnect.getConnection().prepareStatement(
                                "UPDATE nr_super_rank SET event_newyear2026 = ? WHERE player_id = ?");
                        updatePs.setString(1, data.toJSON());
                        updatePs.setInt(2, player.id);
                        updatePs.executeUpdate();
                        updatePs.close();
                    } else {
                        throw insertEx;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving NewYear2026 data", e);
        }
    }

    public static int getPointsForRank(int rank) {
        if (rank < 1 || rank > MAX_RANK_FOR_POINTS) {
            return 0;
        }
        return POINTS_BY_RANK[rank - 1];
    }

    public static boolean onFight(Player player) {
        if (!isActive() || player == null || player.id <= 0) {
            return true;
        }
        
        if (player.superrank == null) {
            SuperRank.loadSuperRank(player);
            if (player.superrank == null) {
                return true;
            }
        }
        
        int currentRank = player.superrank.rank;
        if (currentRank > MAX_RANK_FOR_POINTS || currentRank <= 0) {
            return true;
        }
        
        NewYear2026RankData data = getOrCreateData(player);
        if (data == null) {
            return true;
        }
        
        data.currentRank = currentRank;
        data.holdStartTime = System.currentTimeMillis();
        data.points = 0;
        data.lastRewardTime = 0;
        saveData(player, data);
        
        com.ngocrong.NQMP.DHVT_SH.Top_SieuHang.load();
        
        return true;
    }
    
    public static void onRankUp(Player player, int newRank, int oldRank) {
        if (!isActive() || player == null || player.id <= 0) {
            return;
        }

        NewYear2026RankData data = getOrCreateData(player);
        if (data == null) {
            return;
        }

        if (newRank <= MAX_RANK_FOR_POINTS && oldRank != newRank) {
            data.currentRank = newRank;
            data.holdStartTime = System.currentTimeMillis();
            data.points = 0;
            data.lastRewardTime = 0;
            saveData(player, data);
            
            com.ngocrong.NQMP.DHVT_SH.Top_SieuHang.load();
        }
    }

    public static void onRankDown(Player player, int newRank, int oldRank) {
        if (!isActive() || player == null || player.id <= 0) {
            return;
        }

        NewYear2026RankData data = getOrCreateData(player);
        if (data == null) {
            return;
        }

        if (oldRank != newRank) {
            if (newRank <= MAX_RANK_FOR_POINTS) {
                data.currentRank = newRank;
                data.holdStartTime = System.currentTimeMillis();
                data.points = 0;
                data.lastRewardTime = 0;
                saveData(player, data);
                
                com.ngocrong.NQMP.DHVT_SH.Top_SieuHang.load();
            } else {
                data.holdStartTime = 0;
                data.currentRank = newRank;
                data.points = 0;
                data.lastRewardTime = 0;
                saveData(player, data);
            }
        }
    }

    public static void checkHoldTime(Player player) {
        if (!isActive() || player == null) {
            return;
        }
        if (player.id <= 0) {
            return;
        }

        NewYear2026RankData data = getOrCreateData(player);
        if (data == null) {
            return;
        }

        if (data.points > 0) {
            return;
        }
        
        if (data.isHoldingRank()) {
            if (data.hasHeldLongEnough() && data.points == 0) {
                int points = getPointsForRank(data.currentRank);
                if (points > 0) {
                    data.points = points;
                    data.totalPoints += points;
                    data.lastRewardTime = System.currentTimeMillis();
                    saveData(player, data);
                    
                    String timeText = MODE_DEBUG ? "2 phút" : "30 phút";
                    player.service.dialogMessage(String.format(
                            "Bạn đã giữ hạng đủ %s!\n" +
                            "Nhận được %d điểm sự kiện\n" +
                            "Tổng điểm: %d điểm\n" +
                            "Đánh tiếp đê. chưa top 1 thì đừng nghỉ ?",
                            timeText, points, data.totalPoints));
                }
            }
        }
    }

    public static String getRemainingTime(Player player) {
        if (!isActive() || player == null) {
            return "";
        }
        if (player.id <= 0) {
            return "";
        }

        NewYear2026RankData data = getOrCreateData(player);
        if (data == null || !data.isHoldingRank()) {
            return "";
        }

        long remaining = data.getRemainingHoldTime();
        if (remaining <= 0) {
            return "00:00";
        }

        long minutes = remaining / (1000 * 60);
        long seconds = (remaining % (1000 * 60)) / 1000;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public static boolean checkTicket(Player player) {
        if (!isActive() || player == null) {
            return false;
        }
        if (player.id <= 0) {
            return false;
        }

        NewYear2026RankData data = getOrCreateData(player);
        if (data == null) {
            return false;
        }

        NewYear2026RankData.resetTicketsIfNewDay(data);

        if (data.freeTicketsToday > 0) {
            data.freeTicketsToday--;
            saveData(player, data);
            return true;
        }

        com.ngocrong.item.Item thoivang = player.getItemInBag(com.ngocrong.consts.ItemName.THOI_VANG);
        if (thoivang != null && thoivang.quantity >= 5) {
            player.subThoiVang(5);
            return true;
        }

        return false;
    }

    public static byte getFreeTicketsRemaining(Player player) {
        if (!isActive() || player == null) {
            return 0;
        }
        if (player.id <= 0) {
            return 0;
        }

        NewYear2026RankData data = getOrCreateData(player);
        if (data == null) {
            return 0;
        }

        NewYear2026RankData.resetTicketsIfNewDay(data);
        return data.freeTicketsToday;
    }

    public static int getTotalPoints(Player player) {
        if (!isActive() || player == null) {
            return 0;
        }

        NewYear2026RankData data = getOrCreateData(player);
        if (data == null) {
            return 0;
        }

        return data.totalPoints;
    }

    /**
     * Thêm điểm sự kiện vào totalPoints
     * @param player Player cần thêm điểm
     * @param points Số điểm cần thêm
     */
    public static void addEventPoint(Player player, int points) {
        if (!isActive() || player == null || points <= 0) {
            return;
        }

        NewYear2026RankData data = getOrCreateData(player);
        if (data == null) {
            return;
        }

        data.totalPoints += points;
        saveData(player, data);
    }
}
