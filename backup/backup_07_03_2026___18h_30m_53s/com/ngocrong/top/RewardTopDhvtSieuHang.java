package com.ngocrong.top;

import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RewardTopDhvtSieuHang extends Top {

    private static Logger logger = Logger.getLogger(TopPower.class);

    public RewardTopDhvtSieuHang(int id, byte type, String name, byte limit) {
        super(id, type, name, limit);
    }

    @Override
    public void load() {
        elements.clear();
        try {
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement("SELECT np.id, np.name , np.head2 , np.body , np.leg, neo.point as point from nr_player np\n"
                    + "inner join nr_dhvt_sieu_hang_reward neo\n"
                    + "on np.id  = neo.player_id\n"
                    + "where neo.point > 0 order by point desc limit 50;");
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
                    short goldReward;
                    if (power == 1) {
                        goldReward = 200;
                    } else if (power < 10) {
                        goldReward = 50;
                    } else if (power < 50) {
                        goldReward = 10;
                    } else {
                        goldReward = 0;
                    }
                    info.info = String.format("Số thỏi vàng được nhận: %s", Utils.formatNumber(goldReward));
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
        } catch (Exception ex) {
            
            logger.debug("failed", ex);
        }
    }

}
