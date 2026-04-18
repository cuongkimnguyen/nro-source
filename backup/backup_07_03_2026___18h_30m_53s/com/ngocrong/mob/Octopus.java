package com.ngocrong.mob;

import com.ngocrong.bot.Disciple;
import com.ngocrong.consts.Cmd;
import com.ngocrong.item.ItemTime;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.network.FastDataOutputStream;
import com.ngocrong.network.Message;
import com.ngocrong.skill.Skill;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Octopus extends BigBoss {

    private static Logger logger = Logger.getLogger(Octopus.class);

    @Override
    public void update() {
        if (this.seconds > 0) {
            this.seconds--;
            if (this.seconds == 0) {
                this.isFreeze = false;
            }
        }
        synchronized (this.vItemTime) {
            ArrayList<ItemTime> listRemove = new ArrayList<>();
            for (ItemTime item : this.vItemTime) {
                item.update();
                if (item.seconds <= 0) {
                    switch (item.id) {
                        case 3:
                            this.isSleep = false;
                            zone.service.setEffect(null, this.mobId, Skill.REMOVE_EFFECT, Skill.MONSTER, (byte) 41);
                            break;

                        case 4:
                            this.isBlind = false;
                            zone.service.setEffect(null, this.mobId, Skill.REMOVE_EFFECT, Skill.MONSTER, (byte) 40);
                            break;

                        case 5:
                            this.clearBody();
                            zone.service.changeBodyMob(this, (byte) 0);
                            break;
                    }
                    listRemove.add(item);
                }
            }
            this.vItemTime.removeAll(listRemove);
        }
        if (this.status == 4 && this.levelBoss == 0) {
            long now = System.currentTimeMillis();
            if (now - this.lastTimeAttack > this.attackDelay) {
                this.lastTimeAttack = now;
                if (zone.getNumPlayer() > 0) {
                    attack();
                }
            }
        }

    }

    public void attack() {
        List<Player> list = zone.getListChar(Zone.TYPE_HUMAN, Zone.TYPE_PET);
        List<Player> l = new ArrayList<>();
        for (Player _c : list) {
            if (!_c.isDead() && (_c.getX() >= 442 && _c.getX() <= 960 && _c.getY() >= 400)) {
                l.add(_c);
            }
        }
        if (l.size() > 0) {
            Player p = l.get(Utils.nextInt(l.size()));
            byte action = (byte) Utils.nextInt(3, 5);
            if ((action == 3 || action == 5) && p.getY() != 576) {
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
            dameHp -= p.info.defenseFull;
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
                    p.info.hp -= dameHp;
                    dame[0] = dameHp;
                    p.info.mp -= dameMp;
                    if (p.info.hp <= 0) {
                        kill(p);
                        p.killed(this);
                        p.startDie();
                    }
                } finally {
                    p.lock.unlock();
                }
            } else {
                zone.service.attackNpc(-1, false, this, (byte) -1);
            }
            attackMessage(cAttack, dame, action);
        }
    }

    public void delete() {
        try {
            setX((short) -1000);
            setY((short) -1000);
            Message ms = new Message(Cmd.BIG_BOSS_2);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(7);
            ds.flush();
            zone.service.sendMessage(ms, null);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.debug("attack err");
        }
    }

    public void moveX(short x) {
        try {
            setX(x);
            Message ms = new Message(Cmd.BIG_BOSS_2);
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
            Message ms = new Message(Cmd.BIG_BOSS_2);
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
        delete();
    }

}
