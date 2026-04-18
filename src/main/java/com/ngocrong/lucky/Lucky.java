package com.ngocrong.lucky;

import com.ngocrong.consts.NpcName;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.lib.RandomCollection;
import com.ngocrong.model.Npc;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public abstract class Lucky {

    private static final Logger logger = Logger.getLogger(Lucky.class);

    public static final int LUCKY_GOLD = 0;
    public static final int LUCKY_GOLDBAR = 1;
    public static final int LUCKY_GEM = 2;
    public static final int LUCKY_GEMLOCK = 3;

    public static final byte LUCKY_NORMAL = 0;
    public static final byte LUCKY_VIP = 1;

    public static int timeRemaining = 300;
    public static int timeDelay = 60;
    public static boolean isCountdownRemainingTime = true;
    private static ArrayList<Lucky> luckys = new ArrayList<>();
    public static boolean isRunning = true;

    public static void addLucky(Lucky lucky) {
        luckys.add(lucky);
    }

    public static Lucky getLucky(int id) {
        for (Lucky lucky : luckys) {
            if (lucky.id == id) {
                return lucky;
            }
        }
        return null;
    }

    public static void menu(Player _c, Npc npc) {
        _c.menus.add(new KeyValue(779, "Thể lệ"));
        if (isCountdownRemainingTime) {
            _c.menus.add(new KeyValue(780, "Chọn\nVàng"));
            _c.menus.add(new KeyValue(781, "Chọn\nThỏi vàng"));
            _c.menus.add(new KeyValue(782, "Chọn\nHồng ngọc"));
            _c.menus.add(new KeyValue(783, "Chọn\nNgọc xanh"));
            _c.service.openUIConfirm(NpcName.LY_TIEU_NUONG, "Trò chơi Chọn Ai Đây đang được diễn ra, nếu bạn tin tưởng mình đang tràn đầy may mắn thì có thể tham gia thử.", (short) 3049, _c.menus);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Chúc mừng các bạn may mắn được chọn lần trước là");
            for (Lucky lucky : luckys) {
                if (lucky.winnerNormal != null) {
                    String name = lucky.winnerNormal;
                    int quantity = lucky.totalNormal - (lucky.totalNormal / 100);
                    sb.append("\n" + name + " +");
                    sb.append(Utils.currencyFormat(quantity));
                    if (lucky.id == LUCKY_GOLD) {
                        sb.append(" vàng");
                    } else if (lucky.id == LUCKY_GOLDBAR) {
                        sb.append(" thỏi vàng");
                    } else {
                        sb.append(" hồng ngọc");
                    }
                }
                if (lucky.winnerVip != null) {
                    String name = lucky.winnerVip;
                    int quantity = lucky.totalVip - (lucky.totalVip / 100);
                    sb.append("\n" + name + " +");
                    sb.append(Utils.currencyFormat(quantity));
                    if (lucky.id == LUCKY_GOLD) {
                        sb.append(" vàng");
                    } else if (lucky.id == LUCKY_GOLDBAR) {
                        sb.append(" thỏi vàng");
                    } else {
                        sb.append(" hồng ngọc");
                    }
                }
            }
            sb.append(String.format("\nTrò chơi sẽ bắt đầu sau: %d giây nữa.", timeDelay));
            _c.menus.add(new KeyValue(CMDMenu.CANCEL, "OK"));
            _c.service.openUIConfirm(NpcName.LY_TIEU_NUONG, sb.toString(), (short) 3049, _c.menus);
        }
    }

    public static void update() {
        try {
            if (isCountdownRemainingTime) {
                timeRemaining--;
                if (timeRemaining <= 0) {
                    isCountdownRemainingTime = false;
                    timeDelay = 60;
                    for (Lucky l : luckys) {
                        l.result();
                    }
                }
            } else {
                timeDelay--;
                if (timeDelay <= 0) {
                    isCountdownRemainingTime = true;
                    timeRemaining = 300;
                    for (Lucky l : luckys) {
                        l.refresh();
                    }

                }
            }
//            BotVVManager.update();
            Thread.sleep(1000L);
        } catch (Exception ex) {
            
            logger.error("failed!", ex);
        }
    }

    public static void start() {
        Thread t = new Thread(() -> {
            while (isRunning) {
                update();
            }
        });
        t.start();
    }

    private int id;
    private String name;
    public int totalNormal;
    public int totalVip;
    public String winnerNormal;
    public String winnerVip;
    public ArrayList<Gamer> luckyNormal;
    public ArrayList<Gamer> luckyVip;

    public Lucky(int id, String name) {
        this.id = id;
        this.name = name;
        this.totalNormal = 0;
        this.totalVip = 0;
        this.luckyNormal = new ArrayList<>();
        this.luckyVip = new ArrayList<>();
    }

    public Gamer findPlayer(int id, int type) {
        ArrayList<Gamer> list = null;
        if (type == Lucky.LUCKY_NORMAL) {
            list = luckyNormal;
        }
        if (type == Lucky.LUCKY_VIP) {
            list = luckyVip;
        }
        for (Gamer pl : list) {
            if (pl.id == id) {
                return pl;
            }
        }
        return null;
    }

    public void addPlayer(Gamer pl, byte type) {
        ArrayList<Gamer> list = null;
        if (type == Lucky.LUCKY_NORMAL) {
            list = luckyNormal;
        }
        if (type == Lucky.LUCKY_VIP) {
            list = luckyVip;
        }
        list.add(pl);
    }

    public RandomCollection<Integer> getRandom(ArrayList<Gamer> list) {
        RandomCollection<Integer> rand = new RandomCollection<>();
        for (Gamer pl : list) {
//            if (BotVVManager.isBot(pl.id)) {
//                rand.add(pl.quantity * 10, pl.id);
//            } else {
//                rand.add(pl.quantity, pl.id);
//            }
            if (pl != null && pl.quantity > 0 && pl.quantity <= Integer.MAX_VALUE) {
                rand.add(pl.quantity, pl.id);
            }
        }
        return rand;
    }

    public void refresh() {
        this.totalNormal = 0;
        this.totalVip = 0;
        this.luckyNormal.clear();
        this.luckyVip.clear();
        this.winnerNormal = null;
        this.winnerVip = null;
    }

    public void result() {
        try {
            RandomCollection<Integer> rd = getRandom(luckyNormal);
            if (!rd.isEmpty()) {
                int playerID = rd.next();
                Gamer pl = findPlayer(playerID, Lucky.LUCKY_NORMAL);
                if (pl != null) {
                    winnerNormal = pl.name;
                    reward(pl, totalNormal - totalNormal / 100);
                }
            }
            rd = getRandom(luckyVip);
            if (!rd.isEmpty()) {
                int playerID = rd.next();
                Gamer pl = findPlayer(playerID, Lucky.LUCKY_VIP);
                if (pl != null) {
                    winnerVip = pl.name;
                    reward(pl, totalVip - totalVip / 100);
                }
            }
        } catch (Exception e) {
            
            logger.error("failed!", e);
        }
    }

    public abstract void reward(Gamer pl, int quantity);

    public abstract void join(Player _c, byte type);

    public abstract void show(Player _player);
}
