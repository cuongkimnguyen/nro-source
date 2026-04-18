package com.ngocrong.map;

import _HunrProvision.boss.Boss;
import com.ngocrong.bot.boss.android.Android13;
import com.ngocrong.bot.boss.android.Android14;
import com.ngocrong.bot.boss.android.Android15;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.user.Info;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;

public class TeamAndroid13 {

    private  Android13 android13;

    private  Android14 android14;

    private  Android15 android15;

    public TeamAndroid13() {
        android13 = new Android13(this);
        android14 = new Android14(this);
        android15 = new Android15(this);
    }

    public static void clearAllboss(TMap map) {
        synchronized (map.zones) {
            for (Zone zone : map.zones) {
                var players = new ArrayList<>(zone.players);
                for (Player boss : players) {
                    try {
                        if (boss != null) {
                            if (boss.name.equals("Android 13") || boss.name.equals("Android 14") || boss.name.equals("Android 15")) {
                                zone.leave(boss);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public void born() {
        TMap map = MapManager.getInstance().getMap(104);
        clearAllboss(map);
        int zoneID = map.randomZoneID();
        android13 = new Android13(this);
        android14 = new Android14(this);
        android15 = new Android15(this);
        if (android15.isDead()) {
            android15.wakeUpFromDead();
        }
        if (android14.isDead()) {
            android14.wakeUpFromDead();
        }
        if (android13.isDead()) {
            android13.wakeUpFromDead();
        }
        android15.info.recovery(Info.ALL, 100, true);
        android14.info.recovery(Info.ALL, 100, true);
        android13.info.recovery(Info.ALL, 100, true);
        useAirshipToArrive(android15, 104, zoneID);
        useAirshipToArrive(android14, 104, zoneID);
        useAirshipToArrive(android13, 104, zoneID);
        android13.setTypePK((byte) 0);
        android14.setTypePK((byte) 0);
        android15.setTypePK((byte) 5);
    }

    public void next(Boss boss) {
//        if (boss.zone != null) {
//            boss.zone.leave(boss);
//        }
        if (boss instanceof Android15) {
            android14.info.recovery(Info.ALL, 100, true);
            android14.setTypePK((byte) 5);
        }
        if (boss instanceof Android14) {
            android13.info.recovery(Info.ALL, 100, true);
            android13.setTypePK((byte) 5);
        }
        if (boss instanceof Android13) {
            end();
        }
    }

    private void end() {
        Utils.setTimeout(this::born, 10 * 60000L);
    }

    public void useAirshipToArrive(Boss boss, int mapID, int zoneID) {
        TMap map = MapManager.getInstance().getMap(mapID);
        boss.setTeleport((byte) 3);
        boss.setX((short) Utils.nextInt(100, map.width));
        boss.setY((short) 0);
        map.enterZone(boss, zoneID);
        boss.setY(boss.zone.map.collisionLand(boss.getX(), boss.getY()));
        boss.setTeleport((byte) 0);
        boss.sendNotificationWhenAppear(map.name);
    }

    public void useAirshipToArrive(Boss boss, Zone zone) {
        boss.setX((short) Utils.nextInt(100, zone.map.width));
        boss.setY((short) 0);
        zone.enter(boss);
        boss.setY(boss.zone.map.collisionLand(boss.getX(), boss.getY()));
        boss.sendNotificationWhenAppear(zone.map.name);
    }
}
