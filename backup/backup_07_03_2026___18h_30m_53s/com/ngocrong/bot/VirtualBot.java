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
import com.ngocrong.network.Service;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Info;
import com.ngocrong.util.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class VirtualBot extends Boss {

    public int mapSpawn = 0;
    public int mapNext = 0;
    public long timeSpawn = 0;
    public boolean isInit = false;
    public boolean isTrain = false;

    public VirtualBot(String name) {
        super();
        this.name = name;
        this.id = Utils.nextInt(1000000, 9999999);
        this.itemBag = new Item[0];
    }
    public static int TotalBot;

    public VirtualBot(long hp, long mp, long dame, short head, short body, short leg, String name, Zone zone) {
        super();
        this.setInfo(hp, mp, dame, 100, 5);
        this.setBuaThuHut(true);
        this.name = name;
        if (zone.map.mapID != 5) {
            this.setX((short) Utils.nextInt(0, zone.map.width - 50));
        } else {
            if (Utils.nextInt(10) % 2 == 0) {
                this.setX((short) Utils.nextInt(0, zone.map.width - 50));
            } else {
                this.setX((short) Utils.nextInt(888, 1280));
            }
        }
        timeSpawn = System.currentTimeMillis();
        lastAutoIncrease = System.currentTimeMillis();
        lastUsePotential = System.currentTimeMillis();
        this.itemBag = new Item[100];
        setBag1();
        service = new Service(this);
        this.id = Utils.nextInt(1000000, 9999999);
    }

    @Override
    public void setInfo(long hp, long mp, long dame, int def, int crit) {
        info.originalHP = hp;
        info.originalMP = Long.MAX_VALUE;
        info.originalDamage = dame;
        info.originalDefense = def;
        info.originalCritical = crit;
        info.setInfo();
        info.recovery(Info.ALL, 100, false);
    }

    void setBag1() {
        if (!isTrain) {
            if (!isTrain && Utils.nextInt(10) <= 7) {
                byte[] bag = new byte[]{19, 20, 21, 22};
                this.clanID = Utils.nextInt(0, 100);
                this.setBag(bag[Utils.nextInt(bag.length)]);
                this.name = Utils.getAbbre("[" + ConfigStudio.SLOGAN_BOTTRAIN + "]") + this.name;
            }
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

    long lastupdate = 0;
    long lastAutoIncrease = 0;
    long nextAutoIncreaseInterval = 0;
    long lastUsePotential = 0;
    long nextUsePotentialInterval = 0;
    public static List<Integer> mapTrains = new ArrayList<>(List.of(0, 7, 14,
            1, 2, 3, 4, 6, 27, 28, 29, 30,
            8, 9, 10, 11, 12, 13, 31, 32, 33, 34,
            15, 16, 17, 18, 19, 20, 36, 37, 38));

    @Override
    public void update() {
        this.info.mp = this.info.mpFull = Long.MAX_VALUE;
        
        if (this.zone != null && !isTrain) {
            long now = System.currentTimeMillis();
            
            if (lastAutoIncrease == 0) {
                lastAutoIncrease = now;
                nextAutoIncreaseInterval = Utils.nextInt(30000, 60000);
            }
            
            if (now - lastAutoIncrease >= nextAutoIncreaseInterval) {
                autoIncreaseStats();
                lastAutoIncrease = now;
                nextAutoIncreaseInterval = Utils.nextInt(30000, 60000);
            }
            
            if (lastUsePotential == 0) {
                lastUsePotential = now;
                nextUsePotentialInterval = Utils.nextInt(60000, 120000);
            }
            
            if (now - lastUsePotential >= nextUsePotentialInterval && info.potential > 0) {
                usePotentialToAddStats();
                lastUsePotential = now;
                nextUsePotentialInterval = Utils.nextInt(60000, 120000);
            }
            
            if (System.currentTimeMillis() - lastupdate >= Utils.nextInt(5000, 60000)) {
                try {
                    super.update();
                } catch (Exception e) {
                }
                lastupdate = System.currentTimeMillis();
            }
        }

    }
    
    private void autoIncreaseStats() {
        try {
            long timeAlive = (System.currentTimeMillis() - timeSpawn) / 60000;
            
            long hpIncrease = Utils.nextInt(500, 1500) + (timeAlive * 50);
            long newHP = info.originalHP + hpIncrease;
            
            long dameIncrease = Utils.nextInt(5, 20) + (timeAlive * 1);
            long newDame = info.originalDamage + dameIncrease;
            
            long powerIncrease = Utils.nextInt(10, 50) + (timeAlive * 5);
            long newPower = info.power + powerIncrease;
            
            if (newHP > 50_000_000) newHP = 50_000_000;
            if (newDame > 1_000_000) newDame = 1_000_000;
            if (newPower > 100_000_000_000L) newPower = 100_000_000_000L;
            
            info.originalHP = newHP;
            info.originalDamage = newDame;
            info.power = newPower;
            
            info.setInfo();
            info.recovery(Info.ALL, 100, false);
            
        } catch (Exception e) {
        }
    }
    
    private void usePotentialToAddStats() {
        try {
            if (info.potential <= 0) {
                return;
            }
            
            int randomType = Utils.nextInt(3);
            short num = 1;
            
            switch (randomType) {
                case 0:
                    if (info.originalHP >= info.powerLimitMark.hp + 2000) {
                        randomType = 1;
                        break;
                    }
                    long pointNeed1 = num * (2 * (info.originalHP + 1000) + ((num * Info.HP_FROM_1000_TIEM_NANG) - 20)) / 2;
                    if (info.potential >= pointNeed1) {
                        info.potential -= pointNeed1;
                        info.originalHP += (num * Info.HP_FROM_1000_TIEM_NANG);
                    }
                    break;
                    
                case 1:
                    if (info.originalMP >= info.powerLimitMark.mp + 2000) {
                        randomType = 2;
                        break;
                    }
                    long pointNeed2 = num * (2 * (info.originalMP + 1000) + ((num * Info.MP_FROM_1000_TIEM_NANG) - 20)) / 2;
                    if (info.potential >= pointNeed2) {
                        info.potential -= pointNeed2;
                        info.originalMP += (num * Info.MP_FROM_1000_TIEM_NANG);
                    }
                    break;
                    
                case 2:
                    if (info.originalDamage >= info.powerLimitMark.damage + 100) {
                        return;
                    }
                    long pointNeed3 = num * (2 * info.originalDamage + ((num * Info.DAMAGE_FROM_1000_TIEM_NANG) - 1)) / 2 * Info.EXP_FOR_ONE_ADD;
                    if (info.potential >= pointNeed3) {
                        info.potential -= pointNeed3;
                        info.originalDamage += (num * Info.DAMAGE_FROM_1000_TIEM_NANG);
                    }
                    break;
            }
            
            info.setInfo();
            info.recovery(Info.ALL, 100, false);
            
        } catch (Exception e) {
        }
    }
    
    @Override
    public void kill(Object victim) {
        super.kill(victim);
        
        if (victim instanceof Mob) {
            Mob mob = (Mob) victim;
            if (mob.level > 0 && zone != null) {
                long exp = mob.level * 100L;
                exp = Zone.callEXP(this, exp);
                if (exp > 0) {
                    info.addPowerOrPotential(Info.POWER_AND_POTENTIAL, exp);
                }
            }
        }
    }
    public Mob mobFocus = null;
    public long lastAtt;

    public void attack() {
        if (zone != null) {
            if (mobFocus == null) {
                List<Mob> mobs = zone.getListMob();
                double minDistance = Double.MAX_VALUE;

                int playerX = this.getX();
                int playerY = this.getY();

                for (Mob mob : mobs) {
                    if (mob != null && mob.status != 0 && mob.status != 1 && !mob.isMobMe && mob.hp > 0) {  // Kiểm tra mob không null
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
                this.select = skills.get(0);
                this.select.manaUse = 0;
                Mob mob = mobFocus;
                if (mob.status == 0 || mob.status == 1 || mob.isMobMe || mob.hp <= 0) {
                    mobFocus = null;
                    return;
                }
                if (Math.abs(this.getX() - mob.x) > select.dx * 1.2 || Math.abs(this.getY() - mob.y) > select.dy * 1.2) {
                    if (this.meCanMove()) {
                        int stepSize = 100; // Khoảng cách di chuyển mỗi lần
                        int directionX = (mob.x > this.getX()) ? 1 : -1;
                        int directionY = (mob.y > this.getY()) ? 1 : -1;
                        int currentX = this.getX();
                        int currentY = this.getY();
                        int newX = currentX;
                        int newY = currentY;

                        if (Math.abs(mob.x - currentX) > stepSize) {
                            newX = mob.x;
                        } else {
                            newX = mob.x;
                        }

                        if (Math.abs(mob.y - currentY) > stepSize) {
                            newY = mob.y;
                        } else {
                            newY = mob.y;
                        }

                        this.moveTo(newX, newY);
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
            skills.add(Skills.getSkill((byte) (gender == 0 ? 0 : gender == 1 ? 9 : 17), (byte) 7).clone());
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
