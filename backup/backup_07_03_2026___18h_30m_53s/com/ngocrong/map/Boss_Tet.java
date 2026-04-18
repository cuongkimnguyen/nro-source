/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.map;

import com.ngocrong.NQMP.Tet2025.BossTet2;
import _HunrProvision.boss.Boss;
import com.ngocrong.consts.MapName;
import com.ngocrong.util.Utils;

/**
 *
 * @author Administrator
 */
public class Boss_Tet {

    public static int[] MAPS = {168};

    private BossTet2[] boss;

    public Boss_Tet() {
        boss = new BossTet2[5];
        for (int i = 0; i < boss.length; i++) {
            boss[i] = new BossTet2(this);
        }
    }
    byte currBoss;

    public void born() {
        System.err.println("born Tet");
        int mapID = 168;
        TMap map = MapManager.getInstance().getMap(mapID);
        int zoneID = map.randomZoneID();
        for (int i = 0; i < boss.length; i++) {
            if (boss[i].isDead()) {
                boss[i].wakeUpFromDead();
            }
            useAirshipToArrive(boss[i], map.mapID, zoneID);
            System.err.println("born Tet - " + map.name + " - " + zoneID);
        }
        boss[0].setTypePK((byte) 5);
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

    public void next() {
        if (currBoss < 5) {
            currBoss++;
            Utils.setTimeout(() -> {
                boss[currBoss].setTypePK((byte) 5);
            }, 1000L);

        } else {
            Utils.setTimeout(() -> {
                born();
            }, 5 * 60000L);
        }
    }
}
