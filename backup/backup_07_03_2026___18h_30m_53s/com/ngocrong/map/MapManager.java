package com.ngocrong.map;

import _HunrProvision.MainUpdate;
import com.ngocrong.map.expansion.blackdragon.MBlackDragonBall;
import org.apache.log4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MapManager implements Runnable {

    private static Logger logger = Logger.getLogger(MapManager.class);
    private static MapManager instance;

    public static MapManager getInstance() {
        if (instance == null) {
            synchronized (MapManager.class) {
                if (instance == null) {
                    instance = new MapManager();
                }
            }
        }
        return instance;
    }

    public HashMap<Integer, TMap> maps;
    public HashMap<Integer, IMap> list;
    public BaseBabidi baseBabidi;
    public MBlackDragonBall blackDragonBall;
    public MartialCongress martialCongress;
//    public MartialArtsFestival martialArtsFestival;
    public boolean running;

    public MapManager() {
        this.running = true;
        maps = new HashMap<>();
        list = new HashMap<>();
        martialCongress = new MartialCongress();
    }

    public void bossGalaxySoldier() {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNext4 = zonedNow.withHour(4).withMinute(0).withSecond(0); // Đặt thời gian 4:00 sáng

        if (zonedNow.compareTo(zonedNext4) > 0) {
            zonedNext4 = zonedNext4.plusDays(1);
        }

        Duration duration = Duration.between(zonedNow, zonedNext4);
        long initialDelay = duration.getSeconds();

        Runnable runnable = new Runnable() {
            public void run() {
                GalaxySoldier gB = new GalaxySoldier();
                gB.next((byte) 0);
            }
        };

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(runnable, initialDelay, 1 * 24 * 60 * 60, TimeUnit.SECONDS);
    }

    public void openBaseBabidi() {
        MainUpdate.runTaskDayInWindow(() -> {
            baseBabidi = new BaseBabidi();
            addObj(baseBabidi);
        }, "12:00", "13:00");
    }

    public void openBlackDragonBall() {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.systemDefault(); // Lấy múi giờ mặc định của hệ thống
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNext5 = zonedNow.withHour(20).withMinute(0).withSecond(0);
        if (zonedNow.compareTo(zonedNext5) > 0) {
            zonedNext5 = zonedNext5.plusDays(1);
        }
        Duration duration = Duration.between(zonedNow, zonedNext5);
        long initialDelay = duration.getSeconds();
        Runnable runnable = new Runnable() {
            public void run() {
                blackDragonBall = new MBlackDragonBall();
                addObj(blackDragonBall);
                logger.info("Open NRSĐ");
            }
        };
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(runnable, initialDelay, 1 * 24 * 60 * 60L, TimeUnit.SECONDS);
    }

//    public void openMartialArtsFestival() {
//        openMartialArtsFestival("Nhi đồng", 1500000, 2, (byte) 1, "8h30", 8, 0);
//        openMartialArtsFestival("Nhi đồng", 1500000, 2, (byte) 1, "14h30", 14, 0);
//        openMartialArtsFestival("Nhi đồng", 1500000, 2, (byte) 1, "18h30", 18, 0);
//        openMartialArtsFestival("Siêu cấp 1", 15000000, 4, (byte) 1, "9h30", 9, 0);
//        openMartialArtsFestival("Siêu cấp 1", 15000000, 4, (byte) 1, "13h30", 13, 0);
//        openMartialArtsFestival("Siêu cấp 1", 15000000, 4, (byte) 1, "19h30", 19, 0);
//        openMartialArtsFestival("Siêu cấp 2", 15000000, 6, (byte) 1, "10h30", 10, 0);
//        openMartialArtsFestival("Siêu cấp 2", 15000000, 6, (byte) 1, "15h30", 15, 0);
//        openMartialArtsFestival("Siêu cấp 2", 15000000, 6, (byte) 1, "20h30", 20, 0);
//        openMartialArtsFestival("Siêu cấp 3", 150000000, 8, (byte) 1, "11h30", 11, 0);
//        openMartialArtsFestival("Siêu cấp 3", 150000000, 8, (byte) 1, "16h30", 16, 0);
//        openMartialArtsFestival("Siêu cấp 3", 150000000, 8, (byte) 1, "21h30", 21, 0);
//        openMartialArtsFestival("Siêu hạng", -1, 10000, (byte) 0, "12h30", 12, 0);
//        openMartialArtsFestival("Siêu hạng", -1, 10000, (byte) 0, "17h30", 17, 0);
//        openMartialArtsFestival("Siêu hạng", -1, 10000, (byte) 0, "22h30", 22, 0);
//        openMartialArtsFestival("Siêu hạng", -1, 10000, (byte) 0, "23h30", 23, 0);
//
//    }
//    public void openMartialArtsFestival(String name, long power, int fee, byte feeType, String strTimeStart, int hours, int minutes) {
//        LocalDateTime localNow = LocalDateTime.now();
//        ZoneId currentZone = ZoneId.of("Asia/Ho_Chi_Minh");
//        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
//        ZonedDateTime zonedNext5 = zonedNow.withHour(hours).withMinute(minutes).withSecond(0);//
//        if (zonedNow.compareTo(zonedNext5) > 0) {
//            zonedNext5 = zonedNext5.plusDays(1);
//        }
//        Duration duration = Duration.between(zonedNow, zonedNext5);
//        long initalDelay = duration.getSeconds();
//        Runnable runnable = new Runnable() {
//            public void run() {
//                martialArtsFestival = new MartialArtsFestival(name, power, fee, feeType, strTimeStart);
//            }
//        };
//        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//        scheduler.scheduleAtFixedRate(runnable, initalDelay, 1 * 24 * 60 * 60, TimeUnit.SECONDS);
//
//    }
    public TMap getMap(int id) {
        return maps.get(id);
    }

    public void addObj(IMap iMap) {
        list.put(iMap.getId(), iMap);
    }

    public void removeObj(IMap iMap) {
        list.remove(iMap.getId(), iMap);
    }

    public void close() {
        for (TMap t : maps.values()) {
            try {
                t.close();
            } catch (Exception e) {
                
                logger.error("close", e);
            }
        }
    }

    public void update() {
        List<IMap> removes = new ArrayList<>();
        for (IMap iMap : list.values()) {
            try {
                if (iMap.running) {
                    iMap.update();
                } else {
                    removes.add(iMap);
                }
            } catch (Exception e) {
                
                logger.error("update error map " + e.toString());
            }
        }
        for (IMap iMap : removes) {
            removeObj(iMap);
        }
    }

    @Override
    public void run() {
        while (running) {
            long delay = 1000;
            try {
                long l1 = System.currentTimeMillis();
                update();
                long l2 = System.currentTimeMillis();
                long l3 = l2 - l1;
                if (l3 > delay) {
                    continue;
                }
                Thread.sleep(delay - l3);
            } catch (Exception e) {
                
                e.printStackTrace();
            }
        }
    }

}
