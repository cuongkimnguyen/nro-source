package com.ngocrong.NQMP.Whis;

import com.ngocrong.item.Item;
import com.ngocrong.server.SessionManager;
import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.top.Top;
import com.ngocrong.top.TopInfo;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RewardWhis {

    private static final Logger LOGGER = Logger.getLogger(RewardWhis.class.getName());
    private static final int MAX_REWARD_RANK = 16;
    private static final String FULL_BAG_MESSAGE = "Bạn không đủ ô trống để nhận quà từ top Whis, hãy làm trống hành trang và thoát game vào lại";
    private static final String REWARD_MESSAGE = "Bạn vừa nhận được phần thưởng từ top Whis, hãy kiểm tra hành trang";

    public static void reward() {
        Top topWhis = Top.getTop(Top.TOP_WHIS);
        if (topWhis == null) {
            LOGGER.warning("Top Whis is null");
            return;
        }

        var newTop = new ArrayList<>(topWhis.getElements());
        var claimTop = new ArrayList<TopInfo>();
        Top.getTop(Top.TOP_WHIS_Reward).elements = new ArrayList<>(topWhis.elements);
        for (TopInfo info : newTop) {
            if (info.rank > MAX_REWARD_RANK || info.rank < 1) {
                continue;
            }

            Player pl = SessionManager.findChar(info.playerID);
            if (pl == null) {
                // Lưu thông tin người chơi để có thể nhận thưởng sau
                updateOrInsertReward(info.playerID, info.rank, false);
                continue;
            }

            var items = itemReward(info.rank);
            if (items.isEmpty()) {
                continue;
            }

            if (pl.isFullBag(items.size(), FULL_BAG_MESSAGE)) {
                // Lưu thông tin người chơi để có thể nhận thưởng sau
                updateOrInsertReward(info.playerID, info.rank, false);
                continue;
            }

            for (Item item : items) {
                pl.addItem(item);
            }
            claimTop.add(info);
            pl.service.dialogMessage(REWARD_MESSAGE);
        }

        // Cập nhật lại top mới
        for (TopInfo info : claimTop) {
            newTop.remove(info);
            deleteReward(info.playerID);
        }

        // Xóa dữ liệu cũ để tạo bảng xếp hạng mới
        executeUpdate("DELETE FROM nr_whis_reward");
        executeUpdate("DELETE FROM nr_whis");

        // Lưu thông tin người chơi chưa nhận thưởng
        for (TopInfo info : newTop) {
            if (info.rank <= MAX_REWARD_RANK && info.rank >= 1) {
                updateOrInsertReward(info.playerID, info.rank, false);
            }
        }

        // Cập nhật lại top mới (sẽ trống và sẵn sàng cho người chơi leo hạng mới)
        topWhis.load();
    }

    private static void updateOrInsertReward(int playerId, int rank, boolean hasRewarded) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = MySQLConnect.getConnection();
            // Kiểm tra xem người chơi đã có trong bảng chưa
            ps = conn.prepareStatement("SELECT * FROM nr_whis_reward WHERE player_id = ?");
            ps.setInt(1, playerId);
            rs = ps.executeQuery();

            if (rs.next()) {
                // Cập nhật thông tin hiện có
                try (PreparedStatement updatePs = conn.prepareStatement(
                        "UPDATE nr_whis_reward SET point = ?, reward = ? WHERE player_id = ?")) {
                    updatePs.setInt(1, rank);
                    updatePs.setInt(2, hasRewarded ? 1 : 0);
                    updatePs.setInt(3, playerId);
                    updatePs.executeUpdate();
                }
            } else {
                // Thêm mới
                try (PreparedStatement insertPs = conn.prepareStatement(
                        "INSERT INTO nr_whis_reward (player_id, point, reward) VALUES (?, ?, ?)")) {
                    insertPs.setInt(1, playerId);
                    insertPs.setInt(2, rank);
                    insertPs.setInt(3, hasRewarded ? 1 : 0);
                    insertPs.executeUpdate();
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating reward status", e);
        } finally {
            closeResources(rs, ps, null);
        }
    }

    private static void deleteReward(int playerId) {
        try (PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(
                "DELETE FROM nr_whis_reward WHERE player_id = ?")) {
            ps.setInt(1, playerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting reward", e);
        }
    }

    public static void checkReward(Player pl) {
        if (pl == null) {
            return;
        }
        Utils.setTimeout(() -> {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                conn = MySQLConnect.getConnection();
                ps = conn.prepareStatement("SELECT * FROM nr_whis_reward WHERE player_id = ? AND reward = 0");
                ps.setInt(1, pl.id);
                rs = ps.executeQuery();

                if (rs.next()) {
                    int rank = rs.getInt("point");

                    if (rank <= MAX_REWARD_RANK && rank >= 1) {
                        var items = itemReward(rank);
                        if (items.isEmpty()) {
                            return;
                        }

                        if (pl.isFullBag(items.size(), FULL_BAG_MESSAGE)) {
                            return;
                        }

                        for (Item item : items) {
                            pl.addItem(item);
                        }

                        pl.service.dialogMessage(REWARD_MESSAGE);
                        deleteReward(pl.id);
                    }
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error checking reward", e);
            } finally {
                closeResources(rs, ps, null);
            }
        }, 5000);

    }

    public static List<Item> itemReward(int top) {
        List<Item> list = new ArrayList<>();
        if (top == 1) {
            Item item = new Item(2292);
            item.quantity = 7;
            list.add(item);
        } else if (top == 2) {
            Item item = new Item(2292);
            item.quantity = 3;
            list.add(item);
        } else if (top >= 3 && top <= 6) {
            Item item = new Item(2291);
            item.quantity = 5;
            list.add(item);
        } else if (top >= 7 && top <= 16) {
            Item item = new Item(2290);
            item.quantity = 3;
            list.add(item);
        }
        return list;
    }

    private static void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            // Không đóng connection vì nó có thể được quản lý bởi pool
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing resources", e);
        }
    }

    private static void executeUpdate(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = MySQLConnect.getConnection();
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing update: " + sql, e);
        } finally {
            closeResources(null, ps, null);
        }
    }

    private static ResultSet executeQuery(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = MySQLConnect.getConnection();
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            return ps.executeQuery();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing query: " + sql, e);
            return null;
        }
    }
}
