/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.bot.boss.teambojack;

import _HunrProvision.boss.Boss;
import com.ngocrong.bot.boss.fide.Cooler;
import static com.ngocrong.bot.boss.fide.Cooler.MAPS;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.map.GalaxySoldier;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.mob.Mob;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.SkillName;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author Administrator
 */
public class TeamBojack extends Boss {

    public TeamBojack() {
        super();
        setInfo(50000000, Long.MAX_VALUE, 10000, 100, 5);
        this.percentDame = 35;
        this.limit = -1;
        this.distanceToAddToList = 500;
        canReactDame = false;
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
        System.err.println(String.format("Boss %s xuất hiện tại %s khu vực %d", boss.name, map.name, zoneID));
    }

    public void joinMap() {
        int[] maps = GalaxySoldier.MAPS;
        int z = Utils.nextInt(maps.length);
        int mapID = maps[z];
        TMap map = MapManager.getInstance().getMap(mapID);
        int zoneID = map.randomZoneID();
        zone = map.getZoneByID(zoneID);
        useAirshipToArrive(this, mapID, zoneID);
    }

    @Override
    public void startDie() {
        try {
            super.startDie();
        } finally {
            zone.leave(this);
            Utils.setTimeout(() -> {
                LocalDateTime time = LocalDateTime.now();
                if (time.getHour() < 12) {
                    this.wakeUpFromDead();
                    this.joinMap();
                    this.setTypePK((byte) 5);
                }
            }, 600000);
        }

    }

    @Override
    public void update() {
        LocalDateTime time = LocalDateTime.now();
        if (time.getHour() >= 12) {
            this.startDie();
        }
        super.update();
        //  System.err.println("update boss : " + this.name + " - " + this.skills.size());
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        Player c = (Player) obj;
        int num = Utils.nextInt(4, 14);
        for (int i = 0; i < 10; i++) {
            Item item = new Item(ItemName.THOI_VANG);
            item.setDefaultOptions();
            item.quantity = 1;
            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
            itemMap.item = item;
            itemMap.playerID = -1;
            itemMap.x = (short) Utils.nextInt(50, zone.map.width - 50);
            itemMap.y = zone.map.collisionLand(itemMap.x, getY());
            zone.addItemMap(itemMap);
            zone.service.addItemMap(itemMap);
        }

    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_DRAGON, (byte) 1).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_DEMON, (byte) 1).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_GALICK, (byte) 1).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_KAMEJOKO, (byte) 1).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_MASENKO, (byte) 1).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_ANTOMIC, (byte) 1).clone());
            skills.add(Skills.getSkill((byte) SkillName.KHIEN_NANG_LUONG, (byte) 7).clone());
        } catch (Exception ex) {
            
        }
    }

    @Override
    public long injure(Player plAtt, Mob mob, long dameInput) {
        return Math.min(500000, dameInput);
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
        SessionManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s", this.name, map));
    }

    @Override
    public void sendNotificationWhenDead(String name) {
        SessionManager.chatVip(String.format("%s: Đã tiêu diệt được %s mọi người đều ngưỡng mộ.", name, this.name));
    }

    @Override
    public void setDefaultLeg() {
    }

    @Override
    public void setDefaultBody() {
    }

    @Override
    public void setDefaultHead() {
    }

}
