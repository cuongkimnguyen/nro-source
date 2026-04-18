package com.ngocrong.top;

import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TopWhis extends Top {

    private static Logger logger = Logger.getLogger(TopPower.class);

    public TopWhis(int id, byte type, String name, byte limit) {
        super(id, type, name, limit);
    }

    @Override
    public void load() {
        elements.clear();
        try {
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement("SELECT np.id, np.name , np.head2 , np.body , np.leg, neo.current_level as point from nr_player np\n"
                    + "left join nr_whis neo\n"
                    + "on np.id  = neo.player_id\n"
                    + "order by point desc limit 20;");
            ResultSet rs = ps.executeQuery();
            try {
                int i = 0;
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    short head = rs.getShort("head2");
                    short body = rs.getShort("body");
                    short leg = rs.getShort("leg");
                    long power = rs.getLong("point");
                    TopInfo info = new TopInfo();
                    info.rank = i + 1;
                    info.playerID = id;
                    info.name = name;
                    info.head = head;
                    info.body = body;
                    info.leg = leg;
                    info.info = String.format("Level cao nhất: %s", Utils.formatNumber(power));
                    info.info2 = "";
                    info.score = power;
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
