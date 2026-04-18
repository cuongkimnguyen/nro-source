package com.ngocrong.map;

import _HunrProvision.boss.Boss;
import com.ngocrong.bot.boss.fide.So2;
import com.ngocrong.bot.boss.fide.So1;
import com.ngocrong.bot.boss.fide.So3;
import com.ngocrong.bot.boss.fide.So4;
import com.ngocrong.bot.boss.fide.TieuDoiTruong;
import com.ngocrong.consts.MapName;
import com.ngocrong.util.Utils;

public class GinyuForce {

    public static int[] MAP_NAMEC = {MapName.VACH_NUI_MOORI_2, MapName.TRAM_TAU_VU_TRU_2, MapName.THUNG_LUNG_MAIMA, MapName.THUNG_LUNG_NAMEC, MapName.DAO_GURU,
        MapName.VUC_MAIMA, MapName.NUI_HOA_TIM, MapName.NUI_HOA_VANG, MapName.NAM_GURU, MapName.DONG_NAM_GURU};
    public static int[] MAP_FIDE = {79, 82};

    public static int mapNext = -1;

    private final So4 so4;
    private final So3 so3;
    private final So2 so2;
    private final So1 so1;
    private final TieuDoiTruong tieuDoiTruong;
    private final byte type;

    public byte getType() {
        return type;
    }

    public GinyuForce(byte type) {
        this.type = type;
        this.so4 = new So4(this);
        this.so3 = new So3(this);
        this.so2 = new So2(this);
        this.so1 = new So1(this);
        this.tieuDoiTruong = new TieuDoiTruong(this);
    }

    public void born() {
        if (type == 0) {
            spawnSolo(so4);
            spawnSolo(so3);
            spawnSolo(so2);
            spawnSolo(so1);
            spawnSolo(tieuDoiTruong);
            so4.setTypePK((byte) 5);
            so3.setTypePK((byte) 5);
            so2.setTypePK((byte) 5);
            so1.setTypePK((byte) 5);
            tieuDoiTruong.setTypePK((byte) 5);
        } else {
            int[] maps = MAP_FIDE;
            int mapID = maps[Utils.nextInt(maps.length)];
            if (mapNext != -1) {
                mapID = mapNext;
                mapNext = -1;
            }
            TMap map = MapManager.getInstance().getMap(mapID);
            int zoneID = map.randomZoneID();
            if (so4.isDead()) {
                so4.wakeUpFromDead();
            }
            if (so3.isDead()) {
                so3.wakeUpFromDead();
            }
            if (so2.isDead()) {
                so2.wakeUpFromDead();
            }
            if (so1.isDead()) {
                so1.wakeUpFromDead();
            }
            if (tieuDoiTruong.isDead()) {
                tieuDoiTruong.wakeUpFromDead();
            }
            useAirshipToArrive(so4, map.mapID, zoneID);
            useAirshipToArrive(so3, map.mapID, zoneID);
            useAirshipToArrive(so2, map.mapID, zoneID);
            useAirshipToArrive(so1, map.mapID, zoneID);
            useAirshipToArrive(tieuDoiTruong, map.mapID, zoneID);
            so4.setTypePK((byte) 5);
        }
    }

    public void next(Boss boss) {
        if (boss instanceof So4) {
            so3.setTypePK((byte) 5);
        }
        if (boss instanceof So3) {
            so2.setTypePK((byte) 5);

        }
        if (boss instanceof So2) {
            so1.setTypePK((byte) 5);
        }
        if (boss instanceof So1) {
            tieuDoiTruong.setTypePK((byte) 5);
        }
        if (boss instanceof TieuDoiTruong) {
            end();
        }
    }

    private void end() {
        Utils.setTimeout(() -> {
            born();
        }, type == 0 ? 15 * 60000 : 3 * 60000L);
    }

    public void spawnSolo(Boss boss) {
        int[] maps = MAP_NAMEC;
        int z = Utils.nextInt(maps.length);
        int mapID = maps[z];
        TMap map = MapManager.getInstance().getMap(mapID);
        int zoneID = map.randomZoneID();
        if (boss.isDead()) {
            boss.wakeUpFromDead();
        }
        useAirshipToArrive(boss, mapID, zoneID);
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
