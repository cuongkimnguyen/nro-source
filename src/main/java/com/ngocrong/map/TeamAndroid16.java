package com.ngocrong.map;

import _HunrProvision.boss.Boss;
import com.ngocrong.bot.boss.android.*;
import com.ngocrong.consts.MapName;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.user.Info;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;

public class TeamAndroid16 {

    public Poc poc;

    public Pic pic;

    public KingKong kingKong;

    public TeamAndroid16() {
        poc = new Poc(this);
        pic = new Pic(this);
        kingKong = new KingKong(this);
    }

    public static void clearAllboss(TMap map) {
        synchronized (map.zones) {
            for (Zone zone : map.zones) {
                var players = new ArrayList<>(zone.players);
                for (Player boss : players) {
                    try {
                        if (boss != null) {
                            if (boss.name.equals("Pic") || boss.name.equals("Poc") || boss.name.equals("King Kong")) {
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
        int[] maps = new int[]{MapName.THANH_PHO_PHIA_BAC};
        int z = Utils.nextInt(maps.length);

        int mapID = maps[z];
        TMap map = MapManager.getInstance().getMap(mapID);
        clearAllboss(map);
        int zoneID = map.randomZoneID();
        poc = new Poc(this);
        pic = new Pic(this);
        kingKong = new KingKong(this);
        if (poc.isDead()) {
            poc.wakeUpFromDead();
        }
        if (pic.isDead()) {
            pic.wakeUpFromDead();
        }
        if (kingKong.isDead()) {
            kingKong.wakeUpFromDead();
        }
        kingKong.info.recovery(Info.ALL, 100, true);
        poc.info.recovery(Info.ALL, 100, true);
        pic.info.recovery(Info.ALL, 100, true);

        useAirshipToArrive(poc, mapID, zoneID);
        useAirshipToArrive(pic, mapID, zoneID);
        useAirshipToArrive(kingKong, mapID, zoneID);
        poc.setTypePK((byte) 5);
        pic.setTypePK((byte) 0);
        kingKong.setTypePK((byte) 0);
    }

    public void next(Boss boss) {
        if (boss instanceof Poc) {
            Utils.setTimeout(() -> {
                pic.setTypePK((byte) 5);
            }, 1000L);
        }
        if (boss instanceof Pic) {
            Utils.setTimeout(() -> {
                kingKong.setTypePK((byte) 5);
            }, 1000L);
        }
        if (boss instanceof KingKong) {
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
