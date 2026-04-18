package com.ngocrong.map;

import _HunrProvision.boss.Boss;
import com.ngocrong.bot.boss.teamtaydu.*;
import com.ngocrong.consts.MapName;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class TayDuManager {

    private static final Logger logger = Logger.getLogger(TayDuManager.class);

    // Maps where the bosses can appear
    public static final int[] MAPS = {
        MapName.NGU_HANH_SON, MapName.NGU_HANH_SON_2, MapName.NGU_HANH_SON_3
    };

    // Team bosses
    private final NgoKhong ngoKhong;
    private final DuongTang duongTang;
    private final BatGioi batGioi;

    // Current zone where bosses are spawned
    private Zone zone;

    // Counter for boss deaths
    private final AtomicInteger bossDeathCount = new AtomicInteger(0);

    public TayDuManager() {
        // Initialize all bosses
        this.ngoKhong = new NgoKhong(this);
        this.duongTang = new DuongTang(this);
        this.batGioi = new BatGioi(this);
    }

    /**
     * Checks if the current time is within the allowed time window for boss
     * spawning
     *
     * @return true if the current time is between 8:00 AM and 12:00 AM
     */
    private boolean isWithinAllowedTimeWindow() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();

        // Allow spawning between 8:00 and 23:59
        return hour >= 8 && hour < 24;
    }

    /**
     * Spawn all team members in a random map and zone
     */
    public void spawnTeam() {
        if (!isWithinAllowedTimeWindow()) {
            // Schedule a check for when the time window begins (8:00 AM)
            scheduleSpawnAtNextTimeWindow();
            return;
        }

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
        if (ngoKhong.isDead()) {
            ngoKhong.wakeUpFromDead();
        }
        if (duongTang.isDead()) {
            duongTang.wakeUpFromDead();
        }
        if (batGioi.isDead()) {
            batGioi.wakeUpFromDead();
        }

        // Spawn all bosses in the selected zone
        spawnBoss(ngoKhong, mapID, zoneID);
        spawnBoss(duongTang, mapID, zoneID);
        spawnBoss(batGioi, mapID, zoneID);

        // Set PK type for all bosses
        ngoKhong.setTypePK((byte) 5);
        duongTang.setTypePK((byte) 5);
        batGioi.setTypePK((byte) 5);

        logger.info(String.format("Đội Tây Du Ký đã xuất hiện tại %s khu vực %d", map.name, zoneID));
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

        // If all 3 bosses are dead, schedule next spawn
        if (currentDeathCount >= 3) {
            this.zone = null;

            // Schedule next spawn after 30 minutes
            Utils.setTimeout(() -> {
                if (!isWithinAllowedTimeWindow()) {
                    // If outside the allowed time window, schedule for next day
                    return;
                }
                spawnTeam();
            }, 30 * 60000); // 30 minutes in milliseconds
        }
    }

    /**
     * Schedule spawn for the next available time window
     */
    private void scheduleSpawnAtNextTimeWindow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextSpawnTime;

        if (now.getHour() < 8) {
            // If before 8 AM, schedule for 8 AM today
            nextSpawnTime = now.withHour(8).withMinute(0).withSecond(0);
        } else {
            // If after midnight, schedule for 8 AM next day
            nextSpawnTime = now.plusDays(1).withHour(8).withMinute(0).withSecond(0);
        }

        long delayMillis = java.time.Duration.between(now, nextSpawnTime).toMillis();

        // Ensure delay is positive (should always be, but just in case)
        if (delayMillis <= 0) {
            delayMillis = 10000; // Default to 10 seconds if calculation is wrong
        }

        Utils.setTimeout(this::spawnTeam, delayMillis);

        logger.info(String.format("Đội Tây Du Ký được lên lịch xuất hiện vào lúc %s",
                nextSpawnTime.toString()));
    }
}
