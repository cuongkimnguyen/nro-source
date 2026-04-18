package com.ngocrong.map;

import _HunrProvision.boss.Boss;
import com.ngocrong.bot.boss.teamhaitac.*;
import com.ngocrong.consts.MapName;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class HaiTacManager {

    private static final Logger logger = Logger.getLogger(HaiTacManager.class);

    // Maps where the bosses can appear
    public static final int[] MAPS = {
        MapName.RUNG_NAM, MapName.RUNG_BAMBOO, MapName.RUNG_DUONG_XI, MapName.RUNG_XUONG,
        MapName.DAO_KAME, MapName.NAM_KAME, MapName.DAO_BULONG, MapName.DONG_KARIN
    };

    // Team bosses
    private final Luffy luffy;
    private final Zoro zoro;
    private final Sanji sanji;
    private final Brook brook;
    private final Chopper chopper;
    private final Nami nami;
    private final Franky franky;
    private final Usopp usopp;
    private final Robin robin;

    // Current zone where bosses are spawned
    private Zone zone;

    // Counter for boss deaths
    private final AtomicInteger bossDeathCount = new AtomicInteger(0);

    public HaiTacManager() {
        // Initialize all bosses
        this.luffy = new Luffy(this);
        this.zoro = new Zoro(this);
        this.sanji = new Sanji(this);
        this.brook = new Brook(this);
        this.chopper = new Chopper(this);
        this.nami = new Nami(this);
        this.franky = new Franky(this);
        this.usopp = new Usopp(this);
        this.robin = new Robin(this);
    }

    /**
     * Spawn all team members in a random map and zone
     */
    public void spawnTeam() {
        // Reset gold counter before spawning a new team
        TeamHaiTac.resetGoldCounter();

        // Reset boss death counter
        bossDeathCount.set(0);

        // Select a random map and zone
        int[] maps = MAPS;
        int mapIndex = Utils.nextInt(maps.length);
        int mapID = maps[mapIndex];
        TMap map = MapManager.getInstance().getMap(mapID);
        int zoneID = map.randomZoneID();
        zone = map.getZoneByID(zoneID);

        // Wake up all bosses if they're dead
        if (luffy.isDead()) {
            luffy.wakeUpFromDead();
        }
        if (zoro.isDead()) {
            zoro.wakeUpFromDead();
        }
        if (sanji.isDead()) {
            sanji.wakeUpFromDead();
        }
        if (brook.isDead()) {
            brook.wakeUpFromDead();
        }
        if (chopper.isDead()) {
            chopper.wakeUpFromDead();
        }
        if (nami.isDead()) {
            nami.wakeUpFromDead();
        }
        if (franky.isDead()) {
            franky.wakeUpFromDead();
        }
        if (usopp.isDead()) {
            usopp.wakeUpFromDead();
        }
        if (robin.isDead()) {
            robin.wakeUpFromDead();
        }

        // Spawn all bosses in the selected zone
        spawnBoss(luffy, mapID, zoneID);
        spawnBoss(zoro, mapID, zoneID);
        spawnBoss(sanji, mapID, zoneID);
        spawnBoss(brook, mapID, zoneID);
        spawnBoss(chopper, mapID, zoneID);
        spawnBoss(nami, mapID, zoneID);
        spawnBoss(franky, mapID, zoneID);
        spawnBoss(usopp, mapID, zoneID);
        spawnBoss(robin, mapID, zoneID);

        // Set PK type for all bosses
        luffy.setTypePK((byte) 5);
        zoro.setTypePK((byte) 5);
        sanji.setTypePK((byte) 5);
        brook.setTypePK((byte) 5);
        chopper.setTypePK((byte) 5);
        nami.setTypePK((byte) 5);
        franky.setTypePK((byte) 5);
        usopp.setTypePK((byte) 5);
        robin.setTypePK((byte) 5);

        logger.info(String.format("Đội Hải Tặc Mũ Rơm đã xuất hiện tại %s khu vực %d", map.name, zoneID));
    }

    /**
     * Spawn a boss in the specified map and zone
     *
     * @param boss The boss to spawn
     * @param mapID The map ID
     * @param zoneID The zone ID
     */
    private void spawnBoss(Boss boss, int mapID, int zoneID) {
        TMap map = MapManager.getInstance().getMap(mapID);
        boss.setTeleport((byte) 1);
        boss.setX((short) Utils.nextInt(100, map.width - 100));
        boss.setY((short) 0);
        map.enterZone(boss, zoneID);
        boss.setY((short) boss.zone.map.collisionLand(boss.getX(), boss.getY()));
        boss.setTeleport((byte) 0);
        boss.sendNotificationWhenAppear(map.name);
    }

    /**
     * Check if all bosses are dead and schedule next spawn
     */
    public void checkBossDeath() {
        int currentDeathCount = bossDeathCount.incrementAndGet();

        // If all 9 bosses are dead, schedule next spawn
        if (currentDeathCount >= 9) {
            this.zone = null;

            // Schedule next spawn after 15 minutes
            Utils.setTimeout(() -> {
                LocalDateTime futureTime = LocalDateTime.now();
                // Don't spawn after midnight
                if (futureTime.getHour() >= 12) {
                    return;
                }
                spawnTeam();
            }, 15 * 60000); // 15 minutes in milliseconds
        }
    }
}
