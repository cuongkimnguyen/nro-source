/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.top;

import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.util.Utils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class TopDuocBac extends Top {
    private static Logger logger = Logger.getLogger(TopDuocBac.class);

    final String query = "SELECT p.id, p.name, p.head2, p.body, p.leg, "
            + "CAST(JSON_EXTRACT(e.point,'$[6]') AS UNSIGNED) AS boss "
            + "FROM nr_event_tet e JOIN nr_player p ON e.player_id=p.id "
            + "ORDER BY boss DESC LIMIT 100";

    public TopDuocBac(int id, byte type, String name, byte limit) {
        super(id, type, name, limit);
    }

    @Override
    public void load() {
        try {
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
                    long point = rs.getLong("boss");
                    TopInfo info = new TopInfo();
                    info.playerID = id;
                    info.name = name;
                    info.head = head;
                    info.body = body;
                    info.leg = leg;
                    info.info = String.format("Số lần sử dụng: %d", point);
                    info.info2 = "";
                    info.score = point;
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
            
            System.err.println("Error at 122");
            logger.debug("failed", ex);
        }
    }

}
