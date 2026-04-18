package com.ngocrong.map;

import _HunrProvision.boss.Boss;
import com.ngocrong.bot.boss.android.*;
import com.ngocrong.consts.MapName;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.user.Info;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;

public class TeamAndroid19 {

    private Android19 android19;

    private Android20 android20;

    public TeamAndroid19() {
        android19 = new Android19(this);
        android20 = new Android20(this);
    }

    public static void clearAllboss(TMap map) {
        synchronized (map.zones) {
            for (Zone zone : map.zones) {
                var players = new ArrayList<>(zone.players);
                for (Player boss : players) {
                    try {
                        if (boss != null) {
                            if (boss.name.equals("Android 19") || boss.name.equals("Android 20")) {
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
        int[] maps = new int[]{MapName.THANH_PHO_PHIA_NAM};
        int z = Utils.nextInt(maps.length);
        int mapID = maps[z];
        TMap map = MapManager.getInstance().getMap(mapID);
        clearAllboss(map);
        int zoneID = map.randomZoneID();
        android19 = new Android19(this);
        android20 = new Android20(this);
        if (android19.isDead()) {
            android19.wakeUpFromDead();
        }
        if (android20.isDead()) {
            android20.wakeUpFromDead();
        }
        android19.info.recovery(Info.ALL, 100, true);
        android19.info.recovery(Info.ALL, 100, true);
        useAirshipToArrive(android19, mapID, zoneID);
        useAirshipToArrive(android20, mapID, zoneID);
        android19.setTypePK((byte) 5);
        android20.setTypePK((byte) 0);
    }

    public void next(Boss boss) {
        if (boss instanceof Android19) {
            Utils.setTimeout(() -> {
                android20.setTypePK((byte) 5);
            }, 1000L);
        }
        if (boss instanceof Android20) {
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
}
