/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.mob._BigBoss;

import com.ngocrong.bot.Disciple;
import com.ngocrong.consts.Cmd;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemOption;
import com.ngocrong.item.ItemTime;
import com.ngocrong.map.tzone.Zone;
import static com.ngocrong.map.tzone.Zone.allCountMob;
import com.ngocrong.mob.BigBoss;
import com.ngocrong.mob.Mob;
import com.ngocrong.mob.MobFactory;
import com.ngocrong.mob.MobTemplate;
import com.ngocrong.mob.MobType;
import com.ngocrong.mob.NewBoss;
import com.ngocrong.network.Message;
import com.ngocrong.skill.Skill;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import com.ngocrong.network.FastDataOutputStream;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author Administrator
 */
public class Hirudegarn extends NewBoss {

    private static Logger logger = Logger.getLogger(Hirudegarn.class);
    public static boolean init;
    private Map<Integer, Long> damagePlayers = new HashMap<>();
    private long maxHpBoss;

    public Hirudegarn() {
        super();
        logger.info("Add Hirudegarn");
    }

    public static void addMob(Zone zone) {
        Hirudegarn m = (Hirudegarn) MobFactory.getMob(MobType.HIRUDEGARN);
        byte templateID = 70;
        m.setMobId(allCountMob--);
        MobTemplate template = Mob.getMobTemplate(templateID);
        m.setX((short) 333);
        m.setY((short) 360);
        m.setTemplateId(templateID);
        m.setLevel(template.level);
        m.setHpDefault(2000000);
        m.setDefault();
        m.maxHpBoss = m.maxHp;
        m.zone = zone;
        m.setAttackDelay(1500);
        zone.addMob(m);
    }

    public void addDamage(Player player, long dame) {
        if (player == null || dame <= 0) {
            return;
        }
        damagePlayers.merge(player.id, dame, Long::sum);
    }

    @Override
    public void update() {

        long now = System.currentTimeMillis();
        if (now - this.lastTimeAttack > this.attackDelay && this.zone.zoneID == 0) {
            this.lastTimeAttack = now;
            if (this.status == 4 && this.levelBoss == 0 && this.zone.getNumPlayer() > 0) {
                attack();
            } else {
            }
        }
    }

    @Override
    public void throwItem(Player _c) {
        if (_c == null) {
            return;
        }
        Player c = _c;

//        item.setDefaultOptions();
//        item.addItemOption(new ItemOption(86, 0));
//        item.quantity = 1;
//       
//        itemMap.item = item;
//        itemMap.playerID = 999999;
//        itemMap.x = (short) Utils.nextInt(getX() - 100, getX() + 100);
//        itemMap.y = zone.map.collisionLand(getX(), getY());
//        zone.addItemMap(itemMap);
//        zone.service.addItemMap(itemMap);
        if (Utils.nextInt(100) <= 5) {
            Item item = null;
            int[] ao = {ItemName.AO_THAN_LINH, ItemName.AO_THAN_XAYDA, ItemName.AO_THAN_NAMEC};
            ItemMap itemMap = new ItemMap(zone.autoIncrease++);
            item = new Item(ao[Utils.nextInt(ao.length)]);
            item.setDefaultOptions();
            item.quantity = 1;
            itemMap = new ItemMap(zone.autoIncrease++);
            itemMap.item = item;
            itemMap.playerID = Math.abs(c.id);
            itemMap.x = (short) Utils.nextInt(getX() - 100, getX() + 100);
            itemMap.y = zone.map.collisionLand(getX(), getY());
            zone.addItemMap(itemMap);
            zone.service.addItemMap(itemMap);
        }
        delete();
    }

    public void attack() {
        List<Player> list = zone.getListChar(Zone.TYPE_HUMAN, Zone.TYPE_PET);
        List<Player> l = new ArrayList<>();
        for (Player _c : list) {
            if (!_c.isDead()) {
                l.add(_c);
            }
        }
        if (!l.isEmpty()) {
            Player p = l.get(Utils.nextInt(l.size()));
            byte action = (byte) Utils.nextInt(3, 5);
            if ((action == 3 || action == 5)) {
                action = 4;
            }
            if (action == 5) {
                moveX(p.getX());
                return;
            }
            if (action == 3) {
                this.x += (p.getX() - this.x) / 4;
                this.y += (p.getY() - this.y) / 4;
            }
            Player[] cAttack = new Player[1];
            long[] dame = new long[1];
            cAttack[0] = p;

            boolean isMiss = Utils.nextInt(100) < (p.info.percentMiss + 5);
            long dameHp = Utils.nextLong(this.damage2, this.damage1);
            if (isChangeBody) {
                dameHp -= Utils.percentOf(dameHp, this.dameDown);
            }
            if (dameHp <= 0) {
                dameHp = 1;
            }
            if (p.checkEffectOfSatellite(344) > 0) {
                dameHp -= dameHp / 5;
            }
            if (p.isBuaDaTrau()) {
                dameHp /= 2;
            }
            if (p.isDisciple()) {
                Disciple disciple = (Disciple) p;
                if (disciple.master.isBuaDeTu()) {
                    dameHp /= 2;
                }
            }
            if (p.isGiapXen()) {
                dameHp /= 2;
            }
            dameHp -= Utils.percentOf(dameHp, p.info.options[94]);
            if (p.info.options[157] > 0) {
                long pM = p.info.mp * 100 / p.info.mpFull;
                if (pM < 20) {
                    dameHp -= Utils.percentOf(dameHp, p.info.options[157]);
                }
            }
            if (p.isDisciple()) {
                Disciple disciple = (Disciple) p;
                if (disciple.master.isBuaManhMe()) {
                    dameHp /= 2;
                }
            }
            if (this.levelBoss != 0) {
                dameHp = p.info.hpFull / 10;
            }
            if (zone.map.isNguHanhSon()) {
                dameHp = p.info.hp / 20;
            }
            if (p.isBuaBatTu()) {
                if (p.info.hp == 1) {
                    isMiss = true;
                }
                if (dameHp >= p.info.hp) {
                    dameHp = p.info.hp - 1;
                }
            }
            if (dameHp > 0) {
                long reactDame = Utils.percentOf(dameHp, p.info.options[97]);
                if (reactDame >= this.hp) {
                    reactDame = this.hp - 1;
                }
                if (reactDame > 0) {
                    if (this.hp > 1) {
                        this.hp -= reactDame;
                        zone.service.attackNpc(reactDame, false, this, (byte) 36);
                    } else if (this.hp == 1) {
                        zone.service.attackNpc(-1, false, this, (byte) 36);
                    }
                }
            }
            if (!isMiss && p.isProtected()) {
                dameHp = 1;
                if (dameHp >= p.info.hp) {
                    isMiss = true;
                }
            }
            int dameMp = 0;
            if (!isMiss) {
                p.lock.lock();
                try {
                    if (isDead() || p.isDead()) {
                        return;
                    }
                    dameHp = p.info.hpFull / 20;
                    dame[0] = dameHp;
                    p.info.hp -= dameHp;

                    p.info.mp -= dameMp;

                    if (p.info.hp <= 0) {
                        kill(p);
                        p.killed(this);
                        p.startDie();
                    }
                    zone.service.attackNpc(dameHp, false, this, (byte) -1);
                    p.service.loadHP();
                    p.service.loadMP();
                } finally {
                    p.lock.unlock();
                }
            } else {
                zone.service.attackNpc(-1, false, this, (byte) -1);
            }
            attackMessage(cAttack, dame, action);
        } else {
        }
    }

    public void delete() {
        try {
            setX((short) -1000);
            setY((short) -1000);
            Message ms = new Message(Cmd.BIG_BOSS);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(7);
            ds.flush();
            zone.service.sendMessage(ms, null);
            zone.removeMob(this);
            ms.cleanup();
            Utils.setTimeout(()
                    -> {
                synchronized (zone.players) {
                    for (Player player : zone.players) {
                        player.service.setMapInfo();
                    }
                }
            }, 1000);

        } catch (IOException ex) {
            
            logger.debug("attack err");
        }
    }

    public void moveX(short x) {
        try {
            setX(x);
            Message ms = new Message(Cmd.BIG_BOSS);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(5);
            ds.writeShort(x);
            ds.flush();
            zone.service.sendMessage(ms, null);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.debug("attack err");
        }
    }

    public void attackMessage(Player[] cAttack, long[] dame, byte type) {
        try {
            Message ms = new Message(Cmd.BIG_BOSS);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(type);
            ds.writeByte(cAttack.length);
            for (int i = 0; i < cAttack.length; i++) {
                ds.writeInt(cAttack[i].id);
                ds.writeLong(dame[i]);
            }
            ds.flush();
            zone.service.sendMessage(ms, null);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.debug("attack err");
        }

    }

    public void startDie(int dame, boolean isCrit, Player killer) {
        super.startDie(dame, isCrit, killer);
//        long threshold = maxHpBoss / 10;
//        for (Map.Entry<Integer, Long> e : damagePlayers.entrySet()) {
//            if (e.getValue() >= threshold) {
//                Player p = com.ngocrong.server.SessionManager.findChar(e.getKey());
//                if (p != null && p.zone == this.zone) {
//                    Item egg = new Item(ItemName.QUA_TRUNG);
//                    egg.setDefaultOptions();
//                    egg.quantity = 1;
//                    if (!p.addItemBag(egg)) {
//                        p.service.sendThongBao("Hành trang đầy, không thể nhận Quả trứng");
//                    } else {
//                        p.service.sendThongBao("Bạn nhận được 1 Quả trứng");
//                    }
//                }
//            }
//        }
        delete();
    }

}
