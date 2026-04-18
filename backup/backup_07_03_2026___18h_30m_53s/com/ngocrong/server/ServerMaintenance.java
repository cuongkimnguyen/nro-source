package com.ngocrong.server;

import com.ngocrong.map.MapManager;
import com.ngocrong.network.Session;
import static com.ngocrong.server.SessionManager.sessions;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

public class ServerMaintenance implements Runnable {

    private static final Logger logger = Logger.getLogger(ServerMaintenance.class);

    private final String message;
    private int seconds;

    public ServerMaintenance(String message, int seconds) {
        this.message = message;
        this.seconds = seconds;
    }

    public static void BaoTri(int second) {
        try {
            Server server = DragonBall.getInstance().getServer();
            if (!server.isMaintained) {
                ServerMaintenance serverMaintenance = new ServerMaintenance("Bảo trì định kỳ hằng ngày 17h05", second);
                Thread t = new Thread(serverMaintenance);
                t.start();
            } else {
            }
        } catch (Exception ex) {
            
            logger.error("maintenance", ex);
        }
    }

    void close(Server server) {
        try {
            for (Session ss : sessions.values()) {
                try {
                    ss.close();
                } catch (Exception ex) {
                    
                    //System.err.println("Error at 137");
                    logger.error("failed!", ex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        server.saveData();
        server.start = false;
        server.setOfflineAll();
        MapManager.getInstance().close();
        server.stop();

        System.exit(1);
    }

    @Override
    public void run() {
        
        Server server = DragonBall.getInstance().getServer();
        if (!server.isMaintained) {
            logger.debug(String.format("Máy chủ bảo trì sau %s", Utils.timeAgo(seconds)));
            server.isMaintained = true;
            String text = "Máy chủ bảo trì sau %s, vui lòng thoát trò chơi để đảm bảo không bị mất dữ liệu.\n%s";
            SessionManager.addBigMessage(String.format(text, Utils.timeAgo(seconds), this.message));
            while (seconds > 0) {
                if (seconds > 60) {
                    if (seconds % 60 == 0) {
                        SessionManager.addBigMessage(String.format(text, Utils.timeAgo(seconds), this.message));
                        logger.debug(String.format("Máy chủ bảo trì sau %s", Utils.timeAgo(seconds)));
                    }
                } else {
                }
                if (seconds % 10 == 0) {
                    SessionManager.addBigMessage(String.format(text, Utils.timeAgo(seconds), this.message));
                    logger.debug(String.format("Máy chủ bảo trì sau %s", Utils.timeAgo(seconds)));
                }
                seconds--;
                try {
                    Thread.sleep(1000L);
                } catch (Exception ex) {
                    
                    //System.err.println("Error at 142");
                    logger.error("failed!", ex);
                }
            }
            close(server);
        }
    }

}
