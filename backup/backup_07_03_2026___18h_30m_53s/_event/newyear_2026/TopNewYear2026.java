package _event.newyear_2026;

import _HunrProvision.ConfigStudio;
import com.ngocrong.server.mysql.MySQLConnect;
import com.ngocrong.top.Top;
import com.ngocrong.top.TopInfo;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TopNewYear2026 extends Top {

    private static Logger logger = Logger.getLogger(TopNewYear2026.class);
    public static final int TOP_NEWYEAR_2026 = 100;

    public TopNewYear2026(int id, byte type, String name, byte limit) {
        super(id, type, name, limit);
    }

    @Override
    public void load() {
        if (!ConfigStudio.EVENT_NEWYEAR_2026) {
            return;
        }
        
        try {
            PreparedStatement ps = MySQLConnect.getConnection().prepareStatement(
                    "SELECT np.id, np.name, np.head2, np.body, np.leg, neo.event_newyear2026 " +
                    "FROM nr_player np " +
                    "INNER JOIN nr_super_rank neo ON np.id = neo.player_id " +
                    "WHERE neo.event_newyear2026 IS NOT NULL " +
                    "AND neo.event_newyear2026 != '' " +
                    "AND neo.event_newyear2026 != 'null' " +
                    "AND np.id > 0");
            ResultSet rs = ps.executeQuery();
            
            try {
                elements.clear();
                java.util.List<TopInfo> tempList = new java.util.ArrayList<>();
                
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    short head = rs.getShort("head2");
                    short body = rs.getShort("body");
                    short leg = rs.getShort("leg");
                    String eventData = rs.getString("event_newyear2026");
                    
                    if (eventData == null || eventData.isEmpty() || eventData.equals("null")) {
                        continue;
                    }
                    
                    try {
                        NewYear2026RankData rankData = NewYear2026RankData.fromJSON(eventData);
                        
                        String timeStr = "...";
                        if (rankData.isHoldingRank()) {
                            long remaining = rankData.getRemainingHoldTime();
                            if (remaining > 0) {
                                long minutes = remaining / (1000 * 60);
                                long seconds = (remaining % (1000 * 60)) / 1000;
                                timeStr = String.format("%dp%ds", minutes, seconds);
                            } else {
                                timeStr = "00p00s";
                            }
                        }
                        
                        TopInfo info = new TopInfo();
                        info.playerID = id;
                        info.name = name;
                        info.head = head;
                        info.body = body;
                        info.leg = leg;
                        info.score = rankData.totalPoints;
                        info.info = String.format("Point: %s | %s", Utils.formatNumber(rankData.totalPoints), timeStr);
                        info.info2 = String.format("Hạng hiện tại: %d", rankData.currentRank > 0 ? rankData.currentRank : -1);
                        
                        tempList.add(info);
                    } catch (Exception e) {
                    }
                }
                
                tempList.sort((o1, o2) -> Long.compare(o2.score, o1.score));
                
                int finalCount = Math.min(limit, tempList.size());
                for (int i = 0; i < finalCount; i++) {
                    elements.add(tempList.get(i));
                }
            } finally {
                rs.close();
                ps.close();
            }
            
            update();
            updateLowestScore();
        } catch (Exception ex) {
            logger.error("Error loading TopNewYear2026", ex);
            System.err.println("Error loading TopNewYear2026: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
