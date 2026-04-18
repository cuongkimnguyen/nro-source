package com.ngocrong.top;

import com.ngocrong.consts.Cmd;
import com.ngocrong.network.Message;
import com.ngocrong.user.Player;
import lombok.Getter;
import org.apache.log4j.Logger;

import com.ngocrong.network.FastDataOutputStream;
import com.ngocrong.server.Config;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Getter
public abstract class Top {

    public static final byte TYPE_NONE = 1;
    public static final byte TYPE_THACH_DAU = 0;
    public static final int TOP_POWER = 0;
    public static final int TOP_EXCHANGE = 1;
    public static final int TOP_DHVT_SIEU_HANG = 2;
    public static final int REWARD_TOP_DHVT_SIEU_HANG = 3;
    public static final int TOP_VQTD = 4;
    public static final int TOP_WHIS = 5;
    public static final int TOP_WHIS_Reward = 6;
    public static final int TOP_TASK = 7;
    public static final int TOP_RAITI = 8;
    public static final int TOP_USING_GOLBAR = 9;
    public static final int TOP_DISCIPLE_POWER = 10;
    public static final int TOP_KILLBOSS = 11;
    public static final int TOP_NUOCMIASIZE_M = 12;
    public static final int TOP_NUOCMIASIZE_XXL = 13;
    public static final int TOP_DT_MABU = 14;
    public static final int TOP_DUOCBAC = 15, TOP_DUOCVANG = 16;
    public static final int TOP_HOPQUATHUONG = 17, TOP_HOPQUAVIP = 18, TOP_HOPQUADACBIET = 19;
    public static final int TOP_BANH1TRUNG = 20, TOP_BANH2TRUNG = 21, TOP_BANHDACBIET = 22;
    public static final int TOP_NEWYEAR_2026 = 100;
    public static final int TOP_HOPQUA_THUONG_TET2026 = 101;
    public boolean isToggle;
    private static Logger logger = Logger.getLogger(Top.class);
    private static ArrayList<Top> tops = new ArrayList<>();
    private static volatile boolean isRunning = true;
    private static Thread updateThread;
    private static Thread fastUpdateThread; // Thread riêng cho update 30s
    private static long lastUpdateTime = System.currentTimeMillis();
    private static long lastFastUpdateTime = System.currentTimeMillis(); // Thời gian update cuối cùng cho fast update

    private int id;
    private String name;
    private byte type;
    protected byte limit;
    public ArrayList<TopInfo> elements;
    protected long lowestScore = -1;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public ArrayList<TopInfo> getElements() {
        return elements;
    }

    public static void initialize() {
            addTop(new TopPower(TOP_POWER, TYPE_NONE, "Sức mạnh", (byte) 100));
            addTop(new TopTask(TOP_TASK, TYPE_NONE, "Nhiệm Vụ", (byte) 100));
//            addTop(new TopUseGoldbar(Top.TOP_USING_GOLBAR, TYPE_NONE, "Top sử dụng thỏi vàng", (byte) 100));
            addTop(new TopDisciplePower(Top.TOP_DISCIPLE_POWER, TYPE_NONE, "Top Sức Mạnh đệ tử", (byte) 100));
//            startFastUpdate();
        
//        addTop(new TopExchange(TOP_EXCHANGE, TYPE_NONE, "Đổi Vật phẩm", (byte) 100));
        addTop(new TopDHVTSieuHang(Top.TOP_DHVT_SIEU_HANG, TYPE_THACH_DAU, "Top DHVT Siêu Hạng", (byte) 100));
//        addTop(new TopVQTD(Top.TOP_VQTD, TYPE_NONE, "Top Vòng Quay Thượng Đế", (byte) 50));
//        addTop(new TopWhis(Top.TOP_WHIS, TYPE_NONE, "Top WHIS", (byte) 20));
//        addTop(new TopWhisReward(Top.TOP_WHIS_Reward, TYPE_NONE, "Top WHIS Lần trước", (byte) 20));
//
//        addTop(new TopKillRaiti(Top.TOP_RAITI, TYPE_NONE, "Top Boss Raiti", (byte) 100));
//        addTop(new TopUseGoldbar(Top.TOP_USING_GOLBAR, TYPE_NONE, "Top sử dụng thỏi vàng", (byte) 100));
//        addTop(new TopDisciplePower(Top.TOP_DISCIPLE_POWER, TYPE_NONE, "Top Sức Mạnh đệ tử", (byte) 100));
//
//        addTop(new TopKillBoss(Top.TOP_KILLBOSS, TYPE_NONE, "Top Hạ Boss", (byte) 100));
//        addTop(new TopNuocMiaSizeM(Top.TOP_NUOCMIASIZE_M, TYPE_NONE, "Top dùng Nước mía Size Nhỏ", (byte) 100));
//        addTop(new TopNuocMiaSizeXXL(Top.TOP_NUOCMIASIZE_XXL, TYPE_NONE, "Top dùng Nước mía Size Lớn", (byte) 100));
//        if (Config.serverID() == 1) {
//            addTop(new TopDisciplePowerMabu(Top.TOP_DT_MABU, TYPE_NONE, "Top Đệ tử", (byte) 100));
//            addTop(new TopDuocBac(Top.TOP_DUOCBAC, TYPE_NONE, "Top đuốc bạc", (byte) 100));
//            addTop(new TopDuocVang(Top.TOP_DUOCVANG, TYPE_NONE, "Top đuốc vàng", (byte) 100));
//        }
        addTop(new TopHopQuaThuong(Top.TOP_HOPQUATHUONG, TYPE_NONE, "Top Hộp quà thường", (byte) 100));
        addTop(new TopHopQuaVip(Top.TOP_HOPQUAVIP, TYPE_NONE, "Top Hộp quà Cao cấp", (byte) 100));
        addTop(new TopHopQuaDacBiet(Top.TOP_HOPQUADACBIET, TYPE_NONE, "Top Hộp quà Đặc biệt", (byte) 100));

        addTop(new TopBanh1Trung(Top.TOP_BANH1TRUNG, TYPE_NONE, "Top Bánh 1 trứng", (byte) 100));
        addTop(new TopBanh2Trung(Top.TOP_BANH2TRUNG, TYPE_NONE, "Top Bánh 2 trứng", (byte) 100));
        addTop(new TopBanhDacBiet(Top.TOP_BANHDACBIET, TYPE_NONE, "Top Hộp bánh đặc biệt", (byte) 100));
        
        if (_HunrProvision.ConfigStudio.EVENT_NEWYEAR_2026) {
            addTop(new _event.newyear_2026.TopNewYear2026(Top.TOP_NEWYEAR_2026, TYPE_NONE, "Bảng Xếp Hạng Event Tết 2026", (byte) 100));
            addTop(new com.ngocrong.top.TopHopQuaThuongTet2026(Top.TOP_HOPQUA_THUONG_TET2026, TYPE_NONE, "Top mở Hộp quà thường Tết 2026", (byte) 100));
        }

        for (Top top : tops) {
            top.load();
        }
        startAutoUpdate();
        //    startFastUpdate(); // Khởi động thread update 30s
    }

    private static void startAutoUpdate() {
        if (updateThread != null && updateThread.isAlive()) {
            return;
        }

        updateThread = new Thread(() -> {
            while (isRunning) {
                try {
                    // Tính thời gian đến lần update tiếp theo
                    Calendar now = Calendar.getInstance();
                    int minute = now.get(Calendar.MINUTE);
                    int nextUpdateMinute;

                    if (minute < 15) {
                        nextUpdateMinute = 15;
                    } else if (minute < 30) {
                        nextUpdateMinute = 30;
                    } else if (minute < 45) {
                        nextUpdateMinute = 45;
                    } else {
                        nextUpdateMinute = 60;
                    }

                    int sleepMinutes = nextUpdateMinute - minute;
                    int sleepMillis = sleepMinutes * 60 * 1000 - (now.get(Calendar.SECOND) * 1000 + now.get(Calendar.MILLISECOND));

                    Thread.sleep(sleepMillis);

                    logger.info("Starting scheduled top update...");
                    lastUpdateTime = System.currentTimeMillis();

                    synchronized (tops) {
                        for (Top top : tops) {
                            // Bỏ qua TOP_RAITI và TOP_USING_GOLBAR vì chúng được update ở thread khác
                            if (top.id == TOP_RAITI || top.id == TOP_USING_GOLBAR) {
                                continue;
                            }

                            try {
                                // Clear data cũ
                                top.lock.writeLock().lock();
                                try {
                                    top.elements.clear();
                                    top.lowestScore = -1;
                                } finally {
                                    top.lock.writeLock().unlock();
                                }

                                // Load data mới
                                top.load();
                                top.update();
                                top.updateLowestScore();

                                logger.info("Updated top: " + top.getName());
                            } catch (Exception e) {
                                
                                System.err.println("Error at 129");
                                logger.error("Error updating top " + top.getName(), e);
                            }
                        }
                    }

                    logger.info("Scheduled top update completed");

                } catch (InterruptedException e) {
                    
                    System.err.println("Error at 128");
                    logger.warn("Top update thread interrupted", e);
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    
                    System.err.println("Error at 127");
                    logger.error("Error in top update thread", e);
                }
            }
        }, "TopUpdateThread");

        updateThread.setDaemon(true);
        updateThread.start();
    }

    // Thread riêng cho update TOP_RAITI và TOP_USING_GOLBAR mỗi 30s
    private static void startFastUpdate() {
        if (fastUpdateThread != null && fastUpdateThread.isAlive()) {
            return;
        }

        fastUpdateThread = new Thread(() -> {
            while (isRunning) {
                try {
                    Thread.sleep(30000); // 30 seconds

                    logger.info("Starting fast top update for RAITI and GOLBAR...");
                    lastFastUpdateTime = System.currentTimeMillis(); // Cập nhật thời gian

                    synchronized (tops) {
                        for (Top top : tops) {
                            // Chỉ update TOP_RAITI và TOP_USING_GOLBAR
                            if (top.id != TOP_RAITI && top.id != TOP_USING_GOLBAR) {
                                continue;
                            }

                            try {
                                // Clear data cũ
                                top.lock.writeLock().lock();
                                try {
                                    top.elements.clear();
                                    top.lowestScore = -1;
                                } finally {
                                    top.lock.writeLock().unlock();
                                }

                                // Load data mới
                                top.load();
                                top.update();
                                top.updateLowestScore();

                                logger.info("Fast updated top: " + top.getName());
                            } catch (Exception e) {
                                
                                System.err.println("Error in fast update");
                                logger.error("Error fast updating top " + top.getName(), e);
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    
                    logger.warn("Fast update thread interrupted", e);
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    
                    logger.error("Error in fast update thread", e);
                }
            }
        }, "TopFastUpdateThread");

        fastUpdateThread.setDaemon(true);
        fastUpdateThread.start();
    }

    public static void addTop(Top top) {
        synchronized (tops) {
            tops.add(top);
        }
    }

    public static void updateTop(int id) {
        synchronized (tops) {
            for (Top top : tops) {
                if (top.id == id) {
                    top.load();
                }
            }
        }
    }

    public static Top getTop(int id) {
        synchronized (tops) {
            for (Top top : tops) {
                if (top.id == id) {
                    return top;
                }
            }
        }
        return null;
    }

    public static void stopAutoUpdate() {
        isRunning = false;

        // Stop main update thread
        if (updateThread != null) {
            updateThread.interrupt();
            try {
                updateThread.join(5000);
            } catch (InterruptedException e) {
                
                System.err.println("Error at 126");
                logger.error("Error stopping update thread", e);
                Thread.currentThread().interrupt();
            }
        }

        // Stop fast update thread
        if (fastUpdateThread != null) {
            fastUpdateThread.interrupt();
            try {
                fastUpdateThread.join(5000);
            } catch (InterruptedException e) {
                
                logger.error("Error stopping fast update thread", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public Top(int id, byte type, String name, byte limit) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.limit = limit;
        elements = new ArrayList<>();
    }

    private String getTimeUntilNextUpdate() {
        // Kiểm tra xem có phải TOP_RAITI hoặc TOP_USING_GOLBAR không
        if (this.id == TOP_RAITI || this.id == TOP_USING_GOLBAR) {
            long currentTime = System.currentTimeMillis();
            long timeSinceLastUpdate = currentTime - lastFastUpdateTime;
            long remainingTime = 30000 - timeSinceLastUpdate; // 30 seconds = 30000ms

            if (remainingTime <= 0) {
                return "Thời gian cập nhật lại : 0 giây";
            }

            int remainingSeconds = (int) (remainingTime / 1000);
            return String.format("Thời gian cập nhật lại : %d giây", remainingSeconds);
        }

        Calendar now = Calendar.getInstance();
        int minute = now.get(Calendar.MINUTE);
        int nextUpdateMinute;

        if (minute < 15) {
            nextUpdateMinute = 15;
        } else if (minute < 30) {
            nextUpdateMinute = 30;
        } else if (minute < 45) {
            nextUpdateMinute = 45;
        } else {
            nextUpdateMinute = 60;
        }

        int remainingMinutes = nextUpdateMinute - minute - 1;
        int remainingSeconds = 59 - now.get(Calendar.SECOND);

        return String.format("Thời gian cập nhật lại : %dp%ds", remainingMinutes, remainingSeconds);
    }

    public TopInfo getTopInfo(int playerID) {
        lock.readLock().lock();
        try {
            for (TopInfo top : elements) {
                if (top.playerID == playerID) {
                    return top;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return null;
    }

    public void update() {
        lock.readLock().lock();
        try {
            elements.sort((o1, o2) -> isToggle
                    ? ((Long) o1.score).compareTo(((Long) o2.score))
                    : ((Long) o2.score).compareTo(((Long) o1.score)));
        } finally {
            lock.readLock().unlock();
        }
    }

    public void updateLowestScore() {
        if (elements.size() < limit) {
            lowestScore = 0;
        } else {
            lock.readLock().lock();
            try {
                long lowest = -1;
                for (TopInfo top : elements) {
                    if (lowest == -1) {
                        lowest = top.score;
                    } else {
                        if (top.score < lowest) {
                            lowest = top.score;
                        }
                    }
                }
                lowestScore = lowest;
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    public void addTopInfo(TopInfo top) {
        lock.writeLock().lock();
        try {
            if (elements.size() >= limit) {
                elements.set(limit - 1, top);
            } else {
                elements.add(top);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public abstract void load();

    public void show(Player _player) {
        try {
            Message ms = new Message(Cmd.TOP);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(type);
            ds.writeUTF(name + "\n" + getTimeUntilNextUpdate());
            ds.writeByte(elements.size());
            int i = 1;
            for (TopInfo top : elements) {
                ds.writeInt(i++);
                ds.writeInt(top.playerID);
                ds.writeShort(top.head);
                ds.writeShort(top.body);
                ds.writeShort(top.leg);
                ds.writeUTF(top.name);
                ds.writeUTF(top.info);
                ds.writeUTF(top.info2);
            }
            ds.flush();
            _player.service.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            
            System.err.println("Error at 125");
            logger.error("failed!", ex);
        }
    }
}
