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
public class TopDisciplePower extends Top {

    private static Logger logger = Logger.getLogger(TopDisciplePower.class);
    final String query = "SELECT p.id, p.name, p.head2, p.body, p.leg, CAST(JSON_EXTRACT(e.info,'$.power') AS UNSIGNED) AS power FROM nr_disciple e JOIN nr_player p ON e.id=-p.id ORDER BY power DESC LIMIT 100";

    public TopDisciplePower(int id, byte type, String name, byte limit) {
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
                    long power = rs.getLong("power");
                    TopInfo info = new TopInfo();
                    info.playerID = id;
                    info.name = name;
                    info.head = head;
                    info.body = body;
                    info.leg = leg;
                    info.info = String.format("Sức mạnh đệ tử : %s",  Utils.formatNumber(power));
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
            
            System.err.println("Error at 122");
            logger.debug("failed", ex);
        }
    }

}
