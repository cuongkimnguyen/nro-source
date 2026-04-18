package com.ngocrong.map;

import _HunrProvision.boss.Boss;
import com.ngocrong.bot.boss.teambojack.Bido;
import com.ngocrong.bot.boss.teambojack.Bojack;
import com.ngocrong.bot.boss.teambojack.Bujin;
import com.ngocrong.bot.boss.teambojack.Kogu;
import com.ngocrong.bot.boss.teambojack.SuperBojack;
import com.ngocrong.bot.boss.teambojack.Zangya;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.util.Utils;
import com.ngocrong.consts.MapName;
import java.time.LocalDateTime;
import org.apache.log4j.Logger;

public class GalaxySoldier {

    private static Logger logger = Logger.getLogger(GalaxySoldier.class);

    public static int[] MAPS = {MapName.RUNG_NAM, MapName.RUNG_BAMBOO, MapName.RUNG_DUONG_XI, MapName.RUNG_XUONG, MapName.DAO_KAME,
        MapName.NAM_KAME, MapName.DAO_BULONG, MapName.DONG_KARIN};

    private Bojack bojack;
    private Bido bido;
    private Zangya zangya;
    private Kogu kogu;
    private Bujin bujin;
    private SuperBojack superBojack1, superBojack2;
    private Zone zone;

    public GalaxySoldier() {
        this.bojack = new Bojack(this);
        this.bido = new Bido(this);
        this.zangya = new Zangya(this);
        this.kogu = new Kogu(this);
        this.bujin = new Bujin(this);
        this.superBojack1 = new SuperBojack(this, (byte) 0);
        this.superBojack2 = new SuperBojack(this, (byte) 1);
    }

    public void next(byte i) {
        if (i == 0) {
            if (superBojack1.isDead()) {
                superBojack1.wakeUpFromDead();
            }
            if (bojack.isDead()) {
                bojack.wakeUpFromDead();
            }
            if (bido.isDead()) {
                bido.wakeUpFromDead();
            }
            if (zangya.isDead()) {
                zangya.wakeUpFromDead();
            }
            if (kogu.isDead()) {
                kogu.wakeUpFromDead();
            }
            if (bujin.isDead()) {
                bujin.wakeUpFromDead();
            }
            superBojack1.joinMap();
            superBojack1.setTypePK((byte) 5);

            bojack.joinMap();
            bojack.setTypePK((byte) 5);

            bido.joinMap();
            bido.setTypePK((byte) 5);

            zangya.joinMap();
            zangya.setTypePK((byte) 5);

            kogu.joinMap();
            kogu.setTypePK((byte) 5);

            bujin.joinMap();
            bujin.setTypePK((byte) 5);
        }
//        if (i == 1) {
//            int[] maps = MAPS;
//            int z = Utils.nextInt(maps.length);
//            int mapID = maps[z];
//            TMap map = MapManager.getInstance().getMap(mapID);
//            int zoneID = map.randomZoneID();
//            zone = map.getZoneByID(zoneID);
//            if (bojack.isDead()) {
//                bojack.wakeUpFromDead();
//            }
//            if (bido.isDead()) {
//                bido.wakeUpFromDead();
//            }
//            if (zangya.isDead()) {
//                zangya.wakeUpFromDead();
//            }
//            if (kogu.isDead()) {
//                kogu.wakeUpFromDead();
//            }
//            if (bujin.isDead()) {
//                bujin.wakeUpFromDead();
//            }
//            useAirshipToArrive(bojack, mapID, zoneID);
//            useAirshipToArrive(bido, mapID, zoneID);
//            useAirshipToArrive(zangya, mapID, zoneID);
//            useAirshipToArrive(kogu, mapID, zoneID);
//            useAirshipToArrive(bujin, mapID, zoneID);
//            bojack.setTypePK((byte) 5);
//            bido.setTypePK((byte) 5);
//            zangya.setTypePK((byte) 5);
//            kogu.setTypePK((byte) 5);
//            bujin.setTypePK((byte) 5);
//        }
//        if (i == 2) {
//            if (bojack.isDead() && bido.isDead() && zangya.isDead() && kogu.isDead() && bujin.isDead()) {
//                if (superBojack2.isDead()) {
//                    superBojack2.wakeUpFromDead();
//                }
//                useAirshipToArrive(superBojack2, zone.map.mapID, zone.zoneID);
//                superBojack2.setTypePK((byte) 5);
//            }
//        }
//        if (i == 3) {
//            this.zone = null;
//            Utils.setTimeout(() -> {
//                LocalDateTime futureTime = LocalDateTime.now();
//                if (futureTime.getHour() >= 12) {
//                    return;
//                }
//                next((byte) 0);
//            }, 15 * 60000);
//        }
    }

    public void useAirshipToArrive(Boss boss, int mapID, int zoneID) {
        TMap map = MapManager.getInstance().getMap(mapID);
        boss.setTeleport((byte) 1);
        boss.setX((short) Utils.nextInt(100, map.width));
        boss.setY((short) 0);
        map.enterZone(boss, zoneID);
        boss.setY(boss.zone.map.collisionLand(boss.getX(), boss.getY()));
        boss.setTeleport((byte) 0);
        boss.sendNotificationWhenAppear(map.name);
    }
}
