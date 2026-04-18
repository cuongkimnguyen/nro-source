/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.bot;

import _HunrProvision.boss.Boss;
import _HunrProvision.ConfigStudio;
import com.ngocrong.item.Item;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.mob.Mob;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.Skills;
import com.ngocrong.util.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class BotCold extends Boss {

    public int mapSpawn = 0;
    public int mapNext = 0;
    public long timeSpawn = 0;
    public boolean isInit = false;
    public static int TotalBotCold;
    private long lastAtt;

    public BotCold(long hp, long mp, long dame, short head, short body, short leg, String name, Zone zone) {
        super();
        this.setInfo(hp, mp, dame, 100000, 5);

        this.name = name;
        timeSpawn = System.currentTimeMillis();
        this.itemBag = new Item[100];
        setBag1();
        mapNext = mapSpawn = zone.map.mapID;
        this.setHead(head);
        this.setBody(body);
        this.setLeg(leg);
        this.isShow = false;
        this.info.power = Utils.nextLong(50_000_000_000L, 70_000_000_000L);
        initSkill();

    }

    void setBag1() {
        if (Utils.nextInt(10) <= 8) {
            byte[] bag = new byte[]{19, 20, 21, 22, 113, 114,72};
            this.clanID = Utils.nextInt(0, 100);
            this.setBag(bag[Utils.nextInt(bag.length)]);
            this.name = Utils.getAbbre("[" + ConfigStudio.SLOGAN_BOTCOLD + "]") + this.name;
        }
    }

    @Override
    public boolean isBoss() {
        return false;
    }

    @Override
    public boolean isHuman() {
        return true;
    }

    @Override
    public void update() {
        this.info.mp = this.info.mpFull = Long.MAX_VALUE;
        this.setBuaThuHut(true);
        this.info.options[96] = 20;
        this.info.options[95] = 20;
        updateEveryHalfSeconds2();
        super.update();
    }
    public Mob mobFocus = null;

    public void updateEveryHalfSeconds2() {
        if (zone == null) {
            return;
        }
        if (this.zone.map.mapID != mapNext) {
            TMap map = MapManager.getInstance().getMap(mapNext);
            if (map != null) {
                Zone zone = map.getMinPlayerZone();
                this.zone.leave(this);
                zone.enter(this);
            }
            return;
        }
        if (zone != null) {
            if (mobFocus == null) {
                List<Mob> mobs = zone.getListMob();
                double minDistance = Double.MAX_VALUE;

                int playerX = this.getX();
                int playerY = this.getY();

                for (Mob mob : mobs) {
                    if (mob != null && mob.status != 0 && mob.status != 1 && !mob.isMobMe && mob.hp > 0 && mob.levelBoss == 0) {  // Kiểm tra mob không null
                        // Tính khoảng cách giữa player và mob
                        double distance = Math.sqrt(
                                Math.pow(mob.x - playerX, 2)
                                + Math.pow(mob.y - playerY, 2)
                        );

                        // Cập nhật mob gần nhất
                        if (distance < minDistance) {
                            minDistance = distance;
                            mobFocus = mob;
                        }
                    }
                }
            }
            if (mobFocus != null) {
                for (Skill skill : skills) {
                    if (!skill.isCooldown()) {
                        this.select = skill;
                        this.select.manaUse = 0;
                    }
                }

                Mob mob = mobFocus;
                if (mob.status == 0 || mob.status == 1 || mob.isMobMe || mob.hp <= 0 || mob.levelBoss != 0) {
                    mobFocus = null;
                    return;
                }
                if (Math.abs(this.getX() - mob.x) > select.dx * 1.2 || Math.abs(this.getY() - mob.y) > select.dy * 1.2) {
                    if (this.meCanMove()) {
                        this.moveTo(mob.x, mob.y);
                    }
                    return;
                }
                if (this.meCanAttack() && System.currentTimeMillis() - lastAtt >= 550) {
                    lastAtt = System.currentTimeMillis();
                    this.zone.attackNpc(this, mobFocus, false);
                }
            }
        }
    }

    @Override
    public void initSkill() {

        try {
            skills = new ArrayList<>();
            Skill skill = Skills.getSkill((byte) 17, (byte) 7).clone();
            skill.manaUse = 0;
            skills.add(skill);
        } catch (CloneNotSupportedException ex) {
            

        }

    }

    @Override
    public void sendNotificationWhenAppear(String map) {
    }

    @Override
    public void sendNotificationWhenDead(String name) {
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
