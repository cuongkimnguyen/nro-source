package com.ngocrong.top;

import com.ngocrong.server.mysql.MySQLConnect;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;

public class TopHopQuaThuongTet2026 extends Top {
    private static final Logger logger = Logger.getLogger(TopHopQuaThuongTet2026.class);

    final String query = "SELECT p.id, p.name, p.head2, p.body, p.leg, "
            + "CAST(JSON_EXTRACT(e.point,'$[14]') AS UNSIGNED) AS c "
            + "FROM nr_event_tet e JOIN nr_player p ON e.player_id=p.id "
            + "ORDER BY c DESC LIMIT 100";

    public TopHopQuaThuongTet2026(int id, byte type, String name, byte limit) {
        super(id, type, name, limit);
    }

    @Override
    public void load() {
        try {
            elements.clear();
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            try {
                int i = 0;
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    short head = rs.getShort("head2");
                    short body = rs.getShort("body");
                    short leg = rs.getShort("leg");
                    long count = rs.getLong("c");
                    if (count <= 0) {
                        continue;
                    }
                    TopInfo info = new TopInfo();
                    info.playerID = id;
                    info.name = name;
                    info.head = head;
                    info.body = body;
                    info.leg = leg;
                    info.info = String.format("Số lần mở: %d", count);
                    info.info2 = "";
                    info.score = count;
                    i++;
                    elements.add(info);
                    if (i >= limit) {
                        break;
                    }
                }
            } finally {
                rs.close();
                ps.close();
            }
            update();
            updateLowestScore();
        } catch (Exception ex) {
            logger.debug("failed", ex);
        }
    }
}

