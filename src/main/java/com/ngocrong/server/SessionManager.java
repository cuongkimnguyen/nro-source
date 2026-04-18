package com.ngocrong.server;

import com.ngocrong.network.Message;
import com.ngocrong.network.Service;
import com.ngocrong.network.Session;
import com.ngocrong.user.Player;
import com.ngocrong.user.User;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SessionManager {

    private static final Logger logger = Logger.getLogger(SessionManager.class);

    public static ConcurrentMap<Integer, Session> sessions = new ConcurrentHashMap<>();
    public static byte countPhaoHoa = 0;
    public static Lock lockUserLogin = new ReentrantLock();
    public static HashMap<String, Long> userLogins = new HashMap<>();

    public static void addUserLogin(String username) {
        lockUserLogin.lock();
        try {
            userLogins.put(username, System.currentTimeMillis());
        } finally {
            lockUserLogin.unlock();
        }
    }

    public static long getTimeUserLogin(String username) {
        lockUserLogin.lock();
        try {
            long time = userLogins.getOrDefault(username, 0L);
            return time;
        } finally {
            lockUserLogin.unlock();
        }
    }

    public static void addSession(Session session) {
        sessions.put(session.id, session);
    }

    public static void removeSession(Session session) {
        sessions.remove(session.id);
    }

    public static long getCountPlayer() {
        return sessions.values().stream()
                .filter(ss -> ss.isEnter && ss.socket != null && !ss.socket.isClosed() && ss._player != null)
                .count();
    }

    public static List<User> findUser(String name) {
        List<User> userList = new ArrayList<>();
        for (Session ss : sessions.values()) {
            if (ss.socket != null && !ss.socket.isClosed() && ss.user != null && ss.user.getUsername().toLowerCase().equals(name.toLowerCase())) {
                userList.add(ss.user);
            }
        }
        return userList;
    }
    private static final Object PLAYER_LOCK = new Object();

    public static List<Player> findPlayer(String name) {
        List<Player> userList = new ArrayList<>();
        for (Session ss : sessions.values()) {
            if (ss.socket != null && !ss.socket.isClosed() && ss.user != null) {
                if (ss._player != null && ss._player.name.equals(name)) {
                    userList.add(ss._player);
                }
            }
        }
        return userList;
    }

    public static Player _findPlayer(String name) {
        for (Session ss : sessions.values()) {
            if (ss.socket != null && !ss.socket.isClosed() && ss.user != null) {               
                if (ss._player != null) {
                  // //System.err.println("name Find :" + ss._player.name);
                    if( ss._player.name.equals(name))
                    {return ss._player;}
                }
            }
        }
        return null;
    }

    public static void checkValidPlayer(Player player) {
        if (player == null) {
            return;
        }
        synchronized (PLAYER_LOCK) {
            var players = findPlayer(player.name);

            if (players.size() > 1) {
                for (var pl : players) {
                    try {
                        pl.getSession().close();
                        if (pl.zone != null) {
                            pl.zone.leave(pl);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static boolean deviceInvalid(String device) {
        int num = 0;
        for (Session ss : sessions.values()) {
            if (ss.socket != null && !ss.socket.isClosed() && ss.deviceInfo != null && ss.deviceInfo.equals(device)) {
                num++;
            }
        }
        return num > Server.COUNT_SESSION_ON_IP;
    }

    public static List<User> findUserById(int id) {
        List<User> userList = new ArrayList<>();
        for (Session ss : sessions.values()) {
            if (ss.socket != null && !ss.socket.isClosed() && ss.user != null && ss.user.getId() == id) {
                userList.add(ss.user);
            }
        }
        return userList;
    }

    public static Player findChar(int id) {
        for (Session ss : sessions.values()) {
            if (ss.isEnter && ss.socket != null && !ss.socket.isClosed() && ss._player != null && ss._player.id == id) {
                return ss._player;
            }
        }
        return null;
    }

    public static void sendMessage(Message ms) {
        for (Session ss : sessions.values()) {
            try {
                if (ss.isEnter && ss.socket != null && !ss.socket.isClosed() && ss._player != null) {
                    ss.sendMessage(ms);
                }
            } catch (Exception ex) {
                
                //System.err.println("Error at 142");
                logger.error("failed!", ex);
            }
        }
    }

    public static void chatVip(String text) {
        for (Session ss : sessions.values()) {
            try {
                if (ss.isEnter && ss.socket != null && !ss.socket.isClosed() && ss._player != null) {
                    ss._player.service.chatVip(text);
                }
            } catch (Exception ex) {
                
                //System.err.println("Error at 141");
                logger.error("failed!", ex);
            }
        }
    }

    public static List<Player> getPlayers() {
        List<Player> list = new ArrayList<>();
        for (Session ss : sessions.values()) {
            try {
                if (ss.isEnter && ss.socket != null && !ss.socket.isClosed() && ss._player != null) {
                    list.add(ss._player);
                }
            } catch (Exception ex) {
                
                //System.err.println("Error at 140");
                logger.error("failed!", ex);
            }
        }
        return list;
    }

    public static void serverMessage(String text) {
        for (Session ss : sessions.values()) {
            try {
                if (ss.isEnter && ss.socket != null && !ss.socket.isClosed() && ss._player != null) {
                    ss._player.service.sendThongBao(text);
                }
            } catch (Exception ex) {
                
                //System.err.println("Error at 139");
                logger.error("failed!", ex);
            }
        }
    }

    public static void addThongBaoAll(String text) {
        for (Session ss : sessions.values()) {
            try {
                if (ss.isEnter && ss.socket != null && !ss.socket.isClosed() && ss._player != null) {
                    ss._player.service.dialogMessage(text);
                }
            } catch (Exception ex) {
                
                //System.err.println("Error at 138");
                logger.error("failed!", ex);
            }

        }
    }

    public static void addBigMessage(String text) {
        for (Session ss : sessions.values()) {
            try {
                if (ss.isEnter && ss.socket != null && !ss.socket.isClosed() && ss._player != null) {
                    ss._player.service.addBigMessage(ss._player.getPetAvatar(), text, (byte) 0, null, null);
                }
            } catch (Exception ex) {
                
                //System.err.println("Error at 138");
                logger.error("failed!", ex);
            }

        }
    }

    public static void saveData() {

        //System.err.println("Save Toàn Bộ Player");
        for (Session ss : sessions.values()) {
            try {
                if (ss.isEnter && ss.socket != null && !ss.socket.isClosed() && ss._player != null) {
                    if (System.currentTimeMillis() - ss._player.lastSaveData >= 15 * 60000) {
                        ss._player.saveData();
                    }
                }
            } catch (Exception ex) {
                
                //System.err.println("Error at 137");
                logger.error("failed!", ex);
            }
        }

    }

    public static boolean isValidSession(Session ss) {
        return ss.isEnter && ss.socket != null && !ss.socket.isClosed() && ss._player != null;
    }

    public void update() {
        for (Session ss : sessions.values()) {
            if (isValidSession(ss) && System.currentTimeMillis() - ss.lastCreateSession >= 5 * 60000) {
                ss.lastCreateSession = System.currentTimeMillis();
                Service sv = (Service) ss.getService();
                sv.sendConfirm();
            }
        }
    }

    public static void close() {
        for (Session ss : sessions.values()) {
            try {
                if (ss.isEnter && ss.socket != null && !ss.socket.isClosed() && ss._player != null) {
                    ss.close();
                }
            } catch (Exception ex) {
                
                logger.error("failed!", ex);
            }
        }
    }
}
