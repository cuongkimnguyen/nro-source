package com.ngocrong.top;

import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.user.Player;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Ranking for Raiti boss defeats.
 */
public class TopRaiti {
    private static final int MAX_TOP = 100;
    private static final List<TopInfo> LIST = new ArrayList<>();

    private static void load() {
        LIST.clear();
        try {
            String query = "SELECT p.id, p.name, p.head, p.body, p.leg, "
                    + "JSON_EXTRACT(e.point,'$[0]') AS raiti "
                    + "FROM nr_event_tet e JOIN nr_player p ON e.player_id=p.id "
                    + "ORDER BY raiti DESC LIMIT 100";
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            int rank = 1;
            while (rs.next()) {
                TopInfo t = new TopInfo();
                t.playerID = rs.getInt("id");
                t.name = rs.getString("name");
                t.head = rs.getShort("head");
                t.body = rs.getShort("body");
                t.leg = rs.getShort("leg");
                t.score = rs.getLong("raiti");
                t.info = "Số lần hạ Raiti: " + t.score;
                t.rank = rank++;
                LIST.add(t);
                if (rank > MAX_TOP) {
                    break;
                }
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void show(Player p) {
        load();
        StringBuilder sb = new StringBuilder();
        sb.append("Top Hạ Boss Raiti\n");
        for (int i = 0; i < LIST.size() && i < 10; i++) {
            TopInfo t = LIST.get(i);
            sb.append(t.rank).append(". ").append(t.name).append(" - ").append(t.info).append("\n");
        }
        p.service.serverMessage(sb.toString());
    }
}
