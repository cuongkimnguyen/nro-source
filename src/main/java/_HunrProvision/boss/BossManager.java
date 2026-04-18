package _HunrProvision.boss;

import com.ngocrong.NQMP.Cumber.Cumber;
import _HunrProvision.MainUpdate;
//import com.ngocrong.NQMP.TamThangBa.NuThan;
//import com.ngocrong.NQMP.Tet2025.BossTet1;
import com.ngocrong.bot.boss.BaoCat;
import com.ngocrong.bot.boss.BlackGoku;
import com.ngocrong.bot.boss.Cell.SieuBoHung;
import com.ngocrong.bot.boss.Cell.XenBoHung;
import com.ngocrong.bot.boss.Cell.XenCon;
import com.ngocrong.bot.boss.ThoiKhong.Chilled;
import com.ngocrong.bot.boss.Raiti;
import com.ngocrong.bot.boss.BossDisciple.Broly;
import com.ngocrong.bot.boss.BossDisciple.SuperBroly;
import com.ngocrong.bot.boss.BossDisciple.SuperMabu;
import com.ngocrong.bot.boss.MatTroi;
import com.ngocrong.bot.boss.ThoDaiCa;
import com.ngocrong.bot.boss.MaTroi;

import com.ngocrong.bot.boss.ThoiKhong.Bardock_ThoiKhong;
import com.ngocrong.bot.boss.Zamasu.ZamasuFusion;
import com.ngocrong.bot.boss.bill.Berus;
import com.ngocrong.bot.boss.fide.*;
import com.ngocrong.consts.MapName;
//import com.ngocrong.map.Boss_Tet;
import com.ngocrong.map.GalaxySoldier;
import com.ngocrong.map.GinyuForce;
import com.ngocrong.map.HaiTacManager;
import com.ngocrong.map.TayDuManager;
import com.ngocrong.map.TeamAndroid13;
import com.ngocrong.map.TeamAndroid16;
import com.ngocrong.map.TeamAndroid19;
import com.ngocrong.server.Config;
import com.ngocrong.user.Info;
import com.ngocrong.util.Utils;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BossManager {

    private static FideGold fideGold;

    public static FideGold getFideGold() {
        return fideGold;
    }

    public static void setFideGold(FideGold fg) {
        fideGold = fg;
    }

    public static void bornBoss() {
        //bossShizuka();
        bossCooler();
        bossBlackGoku();
        bossGinyu();
        bossGalaxySoldier();

//        bossHaiTac();
//        bossTayDu();
        bossFide();
        bossKuKu();
        bossMapDauDinh();
        bossRamBo();
        bossXenBoHung();
        bossSieuBoHung();
        bossAndroid13();
        bossAndroid19();
        bossAndroid16();
        bossXenCon();
        bossSuperBroly();

        bossTestServer();
        if (Config.serverID() == 1) {
            bossRaiti();
            bossSuperMabu();
        }
//        bossMatTroi();
//        bossMaTroi();
//        bossThoDaiCa();

//        bossZamasu();
        //   NuThan();
        bossChilled();
        bossFideGold();
        bossBill();
        bossBardock();
//        bossTet2();
//        bossCumber();
        try {
            Utils.setTimeout(() -> {
                BaoCat baocat = new BaoCat();
                baocat.joinMap();
            }, 5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*Utils.setTimeout(() -> {
            FideGold fideGold = new FideGold();
            fideGold.setLocation(MapName.DONG_KARIN, 1);
        }, 300000);
        Utils.setTimeout(() -> {
            FideGold fideGold = new FideGold();
            fideGold.setLocation(MapName.THUNG_LUNG_NAMEC, 1);
        }, 300000);
        Utils.setTimeout(() -> {
            FideGold fideGold = new FideGold();
            fideGold.setLocation(MapName.THANH_PHO_VEGETA, 1);
        }, 300000);*/
//        bossDarkPic();
//        bossBlackWhite();
//        bossNguHanhSon();

    }

//    public static void bossShizuka() {
//        LocalDateTime localNow = LocalDateTime.now();
//        ZoneId currentZone = ZoneId.of("Asia/Ho_Chi_Minh");
//        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
//        ZonedDateTime zonedNext5 = zonedNow.withHour(12).withMinute(30).withSecond(0);
//        if (zonedNow.compareTo(zonedNext5) > 0) {
//            zonedNext5 = zonedNext5.plusDays(1);
//        }
//        Duration duration = Duration.between(zonedNow, zonedNext5);
//        long initalDelay = duration.getSeconds();
//        Runnable runnable = new Runnable() {
//            public void run() {
//                TMap map = MapManager.getInstance().getMap(155);
//                for (Zone z : map.zones) {
//                    Boss boss = new Shizuka();
//                    boss.setLocation(z);
//                }
//
//            }
//        };
//        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//        scheduler.scheduleAtFixedRate(runnable, initalDelay, 1 * 24 * 60 * 60L, TimeUnit.SECONDS);
//    }
    public static void bossCooler() {
        Utils.setTimeout(() -> {
            Cooler cooler = new Cooler((byte) 0);
            cooler.setLocation(110, -1);
        }, 5000);
    }

    public static void bossFide() {
        Utils.setTimeout(() -> {
            Fide fide = new Fide((byte) 0);
            fide.setLocation(80, -1);
        }, 5000);
    }

    public static void bossXenBoHung() {
        Utils.setTimeout(() -> {
            XenBoHung xenBoHung = new XenBoHung((byte) 0);
            xenBoHung.setLocation(100, -1);
        }, 5000);
    }

    public static void bossSieuBoHung() {
        Utils.setTimeout(() -> {
            SieuBoHung sieuBoHung = new SieuBoHung(false);
            sieuBoHung.setLocation(103, -1);
        }, 5000);
    }

    public static void bossXenCon() {
        int[] mapId = new int[]{92, 93, 94, 96, 97, 98, 99, 100, 102, 103};
        for (int i = 0; i < 7; i++) {
            var index = i;
            XenCon xc = new XenCon(index);
            xc.setLocation(mapId[Utils.nextInt(mapId.length)], -1);
        }
    }

    public static void bossKuKu() {
        for (int i = 0; i < 3; i++) {
            int[] mapIDs = new int[]{68, 69, 70, 71, 72};
            Utils.setTimeout(() -> {
                KuKu kuKu = new KuKu();
                kuKu.setLocation(mapIDs[Utils.nextInt(mapIDs.length)], -1);
            }, 5000);
        }
    }

    public static void bossMapDauDinh() {
        for (int i = 0; i < 3; i++) {
            int[] mapIDs = new int[]{64, 65, 63, 66, 67};
            Utils.setTimeout(() -> {
                MapDauDinh mapDauDinh = new MapDauDinh();
                mapDauDinh.setLocation(69, -1);
            }, 5000);
        }
    }

    public static void bossRamBo() {
        for (int i = 0; i < 3; i++) {
            int[] mapIDs = new int[]{73, 74, 75, 76, 77};
            Utils.setTimeout(() -> {
                RamBo ramBo = new RamBo();
                ramBo.setLocation(mapIDs[Utils.nextInt(mapIDs.length)], -1);
            }, 5000);
        }
    }

//    public static void bossDarkPic() {
//        int[] mapIDs = new int[]{MapName.NAM_BULON, MapName.DONG_BULON};
//        Utils.setTimeout(() -> {
//            DarkPic pic = new DarkPic(false);
//            pic.setLocation(mapIDs[Utils.nextInt(mapIDs.length)], -1);
//        }, 10000);
//    }
//    public static void bossBlackWhite() {
//        int[] mapIDs = new int[]{MapName.THANH_PHO_1, MapName.THANH_PHO_2, MapName.THANH_PHO_3};
//        Utils.setTimeout(() -> {
//            BlackWhite blackWhite = new BlackWhite(false);
//            blackWhite.setLocation(mapIDs[Utils.nextInt(mapIDs.length)], -1);
//        }, 10000);
//    }
    public static void bossBlackGoku() {
        for (int i = 0; i < 3; i++) {
            int[] mapIDs = new int[]{92, 93, 94, 96, 97, 98, 99, 100, 102, 103};
            Utils.setTimeout(() -> {
                BlackGoku bl = new BlackGoku(false);
                bl.setLocation(mapIDs[Utils.nextInt(mapIDs.length)], -1);
            }, 5000);
        }
    }

    public static void bossChilled() {
        Utils.setTimeout(() -> {
            for (int i = 0; i < 3; i++) {
                Chilled chilled = new Chilled(false);
                chilled.setLocation(160, -1);
            }
        }, 5000);
    }

    public static void bossFideGold() {
        Runnable task = () -> {
            if (fideGold != null && !fideGold.isDead()) {
                fideGold.info.recovery(Info.ALL, 100, true);
                return;
            }
            FideGold fg = new FideGold();
            setFideGold(fg);
            fg.setLocation(MapName.DONG_KARIN, 0);
        };
        long now = System.currentTimeMillis() / 1000;
        long delay = 3600 - (now % 3600);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(task, delay, 3600, TimeUnit.SECONDS);
    }

//
//    public static void bossNguHanhSon() {
//        int[] maps = new int[]{MapName.NGU_HANH_SON, MapName.NGU_HANH_SON_2, MapName.NGU_HANH_SON_3};
//        Utils.setTimeout(() -> {
//            Boss boss = new BatGioi();
//            boss.setLocation(maps[Utils.nextInt(maps.length)], -1);
//        }, 300000);
//    }
    public static void bossBill() {

        Utils.setTimeout(() -> {
            Boss boss = new Berus();
            boss.setLocation(19, -1);
        }, 5000);

    }

    public static void bossMatTroi() {
        Utils.setTimeout(() -> {
            if (Config.serverID() == 1) {
                MatTroi boss = new MatTroi();
                boss.setLocation(MapName.BAI_BIEN_NGAY_HE, -1);
            }
        }, 5000);
    }

    public static void bossMaTroi() {
//        MainUpdate.runTaskDayInWindow(() -> {
//            killMaTroi();
//            MaTroi b1 = new MaTroi();
//            b1.setLocation(181, 0);
//            MaTroi b2 = new MaTroi();
//            b2.setLocation(182, 0);
//            int[] MAPS = new int[]{0, 7, 14, 5, 13, 10, 20, 19, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38};
//            MaTroi b3 = new MaTroi();
//            b3.setLocation(MAPS[Utils.nextInt(MAPS.length)], 0);
//            int[] FUTURE = new int[]{92, 93, 94, 96, 97, 98, 99, 100, 102, 103};
//            MaTroi b4 = new MaTroi();
//            b4.setLocation(FUTURE[Utils.nextInt(FUTURE.length)], 0);
//            int[] COLD = new int[]{105, 106, 107, 108, 109, 110};
//            MaTroi b5 = new MaTroi();
//            b5.setLocation(COLD[Utils.nextInt(COLD.length)], 0);
//        }, "16:00", "00:00");
//        MainUpdate.runTaskDay(() -> {
//            killMaTroi();
//        }, "00:00");
    }

    private static void killMaTroi() {
        for (Boss b : new ArrayList<>(Boss.listBoss)) {
            if (b instanceof MaTroi) {
                b.startDie();
            }
        }
    }

    public static void bossThoDaiCa() {
        Utils.setTimeout(() -> {
            for (ThoDaiCa.Region region : ThoDaiCa.Region.values()) {
                ThoDaiCa boss = new ThoDaiCa(region);
                boss.setLocation(region.randomMap(), -1);
            }
        }, 5000);
    }

    public static void bossBardock() {
        int[] MAPS = new int[]{160, 161, 162, 163};
        Utils.setTimeout(() -> {
            for (int i = 0; i < 3; i++) {
                Bardock_ThoiKhong boss = new Bardock_ThoiKhong();
                boss.setLocation(MAPS[Utils.nextInt(MAPS.length)], -1);
            }
        }, 5000);
    }

    public static void bossGalaxySoldier() {
        MainUpdate.runTaskDayInWindow(() -> {
            GalaxySoldier gB = new GalaxySoldier();
            gB.next((byte) 0);
        }, "06:00", "12:00");
    }

    public static void bossHaiTac() {
        MainUpdate.runTaskDay(() -> {
            HaiTacManager haitac = new HaiTacManager();
            haitac.spawnTeam();

        }, "06:00");

    }

    public static void bossTayDu() {
        MainUpdate.runTaskDay(() -> {
            TayDuManager taydu = new TayDuManager();
            taydu.spawnTeam();
        }, "08:00");
    }

    public static void bossGinyu() {
        Utils.setTimeout(() -> {
            GinyuForce gB = new GinyuForce((byte) 0);
            gB.born();
        }, 5000);
        Utils.setTimeout(() -> {
            GinyuForce gB = new GinyuForce((byte) 1);
            gB.born();
        }, 5000);
    }

    public static void bossAndroid13() {
        Utils.setTimeout(() -> {
            TeamAndroid13 teamAndroid13 = new TeamAndroid13();
            teamAndroid13.born();
        }, 5000);
    }

    public static void bossAndroid19() {
        Utils.setTimeout(() -> {
            TeamAndroid19 teamAndroid19 = new TeamAndroid19();
            teamAndroid19.born();
        }, 5000);
    }

    public static void bossAndroid16() {
        Utils.setTimeout(() -> {
            TeamAndroid16 teamAndroid16 = new TeamAndroid16();
            teamAndroid16.born();
        }, 5000);
    }

    public static void bossTestServer() {
//        Utils.setTimeout(() -> {
//            for (int i = 0; i < 50; i++) {
//                TestServerBoss boss = new TestServerBoss();
//                boss.setLocation(38, 5);
//            }
//        }, 5000);

    }

    public static void bossSuperBroly() {
        int[] mapIDs = new int[]{5, 7, 13, 10, 20, 19, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38};
        Utils.setTimeout(() -> {
            if (Config.serverID() == 1) {
                for (int i = 0; i < 60; i++) {
                    Broly broly = new Broly();
                    broly.joinMap();
                }
            }
            if (Config.serverID() == 2) {
                for (int i = 0; i < 10; i++) {
                    SuperBroly superBroly = new SuperBroly();
                    superBroly.joinMap();
                }
            }
        }, 5000);

    }

    public static void bossSuperMabu() {
        Utils.setTimeout(() -> {
            SuperMabu broly = new SuperMabu();
            broly.setLocation(52, -1);
        }, 5000);

    }

    private static void NuThan() {
//        Utils.setTimeout(() -> {
//            for (int i = 0; i < 20; i++) {
//                NuThan boss = new NuThan();
//                boss.setLocation(177, -1);
//            }
//        }, 5000);
    }

    private static void bossTet2() {
//        for (int i = 0; i < 3; i++) {
//            int[] mapIDs = new int[]{1, 2, 8, 9, 15, 16};
//            Utils.setTimeout(() -> {
//                BossTet1 boss = new BossTet1();
//                boss.setLocation(mapIDs[Utils.nextInt(mapIDs.length)], -1);
//            }, 5000);
//        }
//        Utils.setTimeout(() -> {
//            Boss_Tet boss = new Boss_Tet();
//            boss.born();
//        }, 35000);
    }

    private static void bossCumber() {
        int[] mapIDs = new int[]{19};
        Utils.setTimeout(() -> {
            Cumber boss = new Cumber(false);
            boss.setLocation(mapIDs[Utils.nextInt(mapIDs.length)], -1);
        }, 5000);

    }

    private static void bossRaiti() {
        int[] mapIDs = new int[]{0, 7, 14};
        Utils.setTimeout(() -> {
            for (int i = 0; i < 30; i++) {
                Raiti boss = new Raiti();
                boss.setLocation(mapIDs[Utils.nextInt(mapIDs.length)], -1);
            }
        }, 5000);
    }

    static void bossZamasu() {
        Utils.setTimeout(() -> {
            for (int i = 0; i < 5; i++) {
                ZamasuFusion boss = new ZamasuFusion(false);
                boss.setLocation(182, -1);
            }
        }, 5000);
    }
}
