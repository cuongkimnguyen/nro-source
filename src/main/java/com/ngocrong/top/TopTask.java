package com.ngocrong.top;

import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TopTask extends Top {

    private static Logger logger = Logger.getLogger(TopTask.class);

    public TopTask(int id, byte type, String name, byte limit) {
        super(id, type, name, limit);
    }

    @Override
    public void load() {
        try {
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement("SELECT \n"
                    + "    pl.`id`, \n"
                    + "    pl.`name`, \n"
                    + "    pl.`head2`, \n"
                    + "    pl.`body`, \n"
                    + "    pl.`leg`, \n"
                    + "    CAST(JSON_UNQUOTE(JSON_EXTRACT(pl.task, \"$.id\")) AS UNSIGNED) AS task,\n"
                    + "    CAST(JSON_UNQUOTE(JSON_EXTRACT(pl.task, \"$.index\")) AS UNSIGNED) AS indexTask,\n"
                    + "    CAST(JSON_UNQUOTE(JSON_EXTRACT(pl.task, \"$.count\")) AS UNSIGNED) AS countTask,\n"
                    + "		CAST(JSON_UNQUOTE(JSON_EXTRACT(pl.task, \"$.lastTask\")) AS UNSIGNED) AS lastTask\n"
                    + "FROM `nr_player` pl \n"
                    + "ORDER BY task DESC, indexTask DESC, countTask DESC,lastTask ASC\n"
                    + "LIMIT 100;");
            ResultSet rs = ps.executeQuery();
            try {
                int i = 0;
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    short head = rs.getShort("head2");
                    short body = rs.getShort("body");
                    short leg = rs.getShort("leg");
                    long mainTask = rs.getLong("task");
                    long subTask = rs.getLong("indexTask");
                    long count = rs.getLong("countTask");
                    TopInfo info = new TopInfo();
                    info.playerID = id;
                    info.name = name;
                    info.head = head;
                    info.body = body;
                    info.leg = leg;
                    info.info = String.format("Nv chính: %s, Nv phụ: %s - %s", mainTask, subTask,count);
                    info.info2 = "";
                    info.score = mainTask;
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
