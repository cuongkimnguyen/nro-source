package com.ngocrong.map.tzone;

import _HunrProvision.services.DaiHoiVoThuat_23Service;
import _HunrProvision.boss.Boss;
import com.ngocrong.bot.TrongTai;
import com.ngocrong.bot.boss.dhvt23.*;
import com.ngocrong.consts.CMDPk;
import com.ngocrong.consts.ItemTimeName;
import com.ngocrong.consts.MapName;
import com.ngocrong.item.ItemTime;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.skill.Skill;
import com.ngocrong.user.Info;
import com.ngocrong.user.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Getter
@Setter
public class Arena23 extends Zone {

    public TrongTai trongTai;
    private ArrayList<Integer> registrationList;
    public ReadWriteLock lock = new ReentrantReadWriteLock();
    private Player currFightingPlayer;
    private int countDownToStart;
    private int countDownToEnd;
    private long last;
    private Boss boss;
    private List<Boss> bosses;
    private boolean started;
    private boolean isFinish;
    private int countDown;

    public Arena23(TMap map, int zoneId) {
        super(map, zoneId);
        this.countDownToStart = 15;
        this.countDown = 180;
        this.countDownToEnd = 5;
        registrationList = new ArrayList<>();
        trongTai = new TrongTai();
        trongTai.setX((short) 375);
        trongTai.setY((short) 264);
        enter(trongTai);
    }

    private void addToList(Integer id) {
        lock.writeLock().lock();
        try {
            registrationList.add(id);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void removeFromList(Integer id) {
        lock.writeLock().lock();
        try {
            registrationList.remove(id);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean checkExistsInList(int charID) {
        lock.readLock().lock();
        try {
            for (int id : registrationList) {
                if (id == charID) {
                    return true;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return false;
    }

    public void register(int id) {
        boolean isExist = checkExistsInList(id);
        if (!isExist) {
            addToList(id);
        }
    }

    public void reSkill() {
        for (Skill skill : currFightingPlayer.skills) {
            if (skill.coolDown > 0) {
                skill.lastTimeUseThisSkill = System.currentTimeMillis() - skill.coolDown;
            }
        }
        currFightingPlayer.service.updateCoolDown(currFightingPlayer.skills);
    }

    public void fight() {
        started = true;
        leave(trongTai);
        currFightingPlayer.setX((short) 322);
        currFightingPlayer.setY((short) 312);
        service.setPosition(currFightingPlayer, (byte) 0);
        currFightingPlayer.info.recovery(Info.ALL, 100, true);
        reSkill();

        if (boss != null) {
            currFightingPlayer.testCharId = boss.id;
            boss.testCharId = currFightingPlayer.id;
            currFightingPlayer.setCommandPK(CMDPk.DAI_HOI_VO_THUAT);
            boss.setCommandPK(CMDPk.DAI_HOI_VO_THUAT);
            currFightingPlayer.setTypePK((byte) 3);
            boss.setTypePK((byte) 3);
        } else {
            close();
        }
    }

    public void load() {
        int round = currFightingPlayer.roundDHVT23;
        var player = currFightingPlayer;
        reSkill();
        ItemTime item = new ItemTime(ItemTimeName.THOI_MIEN, 3782, 12, false);
        player.addItemTime(item);
        player.setSleep(true);
        service.setEffect(null, player.id, Skill.ADD_EFFECT, Skill.CHARACTER, (byte) 41);
        if (boss != null) {
            boss.addItemTime(item);
            boss.setSleep(true);
            service.setEffect(null, boss.id, Skill.ADD_EFFECT, Skill.CHARACTER, (byte) 41);
        }
        player.info.hp = player.info.hpFull;
        player.info.mp = player.info.mpFull;
        service.playerLoadAll(player);
    }

    @Override
    public void update() {
        try {
            super.update();
        } catch (Exception e) {
            
            e.printStackTrace();
        };

        if (currFightingPlayer != null && currFightingPlayer.zone != this) {
            close();
        }
        if (currFightingPlayer != null && trongTai != null) {
            if (boss == null) {
                setNextBoss(currFightingPlayer.roundDHVT23);
            }
            long now = System.currentTimeMillis();
            if (now - last >= 1000) {
                int round = currFightingPlayer.roundDHVT23;
                var player = currFightingPlayer;
                last = now;
                if (countDownToStart > 0) {
                    countDownToStart--;
                    if (countDownToStart == 15) {
                        service.chat(trongTai, "Trận đấu sắp diễn ra");
                    } else if (countDownToStart == 12) {
                        service.chat(trongTai, "Xin quý vị khán giả cho 1 tràng pháo tay cổ vũ cho 2 đấu thủ nào");

                    } else if (countDownToStart == 10) {
                        service.chat(trongTai, "Mọi người hãy ổn định chỗ ngồi, trận đấu sẽ bắt đầu sau 3 giây nữa");
                    } else if (countDownToStart == 8) {
                        service.chat(trongTai, "3");
                    } else if (countDownToStart == 6) {
                        service.chat(trongTai, "2");
                    } else if (countDownToStart == 4) {
                        service.chat(trongTai, "1");
                    } else if (countDownToStart == 2) {
                        service.chat(trongTai, "Trận đấu bắt đầu");
                    } else if (countDownToStart == 0) {
                        fight();
                    }
                }
                if (started) {
                    if (countDown > 0) {
                        countDown--;
                        if (countDown == 0) {
                            if (!isFinish) {
                                currFightingPlayer.service.serverMessage("Bạn đã thất bại");
                                close();
                            }
                            return;
                        }
                        checkResult();
                    }
                }
            }
        }
    }

    public void checkResult() {
        if (!isFinish && started) {
            if ((!currFightingPlayer.isDead() && boss.isDead()) && currFightingPlayer.zone == this) {
                currFightingPlayer.service.serverMessage("Đối thủ đã kiệt sức, bạn đã thắng");
                enter(trongTai);
                setNextBoss(currFightingPlayer.roundDHVT23);
                started = false;
                this.countDown = 180;
                this.countDownToStart = 16;
                currFightingPlayer.setTypePK((byte) 0);
            } else if (currFightingPlayer.isDead() || currFightingPlayer.zone != this) {
                currFightingPlayer.service.serverMessage("Bạn đã thất bại");
                close();
            }
        }
    }

    public void setNextBoss(int level) {
        load();
        if (level > 11) {
            service.chat(currFightingPlayer, "Đã hết đối thủ thách đấu, bạn đã dành chiến thắng");
            close();
        }
        switch (level) {
            case 0:
                boss = new SoiHecQuyn(currFightingPlayer);
                break;
            case 1:
                boss = new ODo(currFightingPlayer);
                break;
            case 2:
                boss = new Xinbato(currFightingPlayer);
                break;
            case 3:
                boss = new ChaPa(currFightingPlayer);
                break;
            case 4:
                boss = new PonPut(currFightingPlayer);
                break;
            case 5:
                boss = new ChanXu(currFightingPlayer);
                break;
            case 6:
                boss = new TauPayPay(currFightingPlayer);
                break;
            case 7:
                boss = new Yamcha(currFightingPlayer);
                break;
            case 8:
                boss = new JackyChun(currFightingPlayer);
                break;
            case 9:
                boss = new ThienXinHang(currFightingPlayer);
                break;
            case 10:
                boss = new LiuLiu(currFightingPlayer);
                break;
//            case 11:
//                boss = new Bardock(currFightingPlayer);
//                break;
            default:
                close();
                break;
        }
        if (boss != null) {
            boss.setX((short) 443);
            boss.setY((short) 312);
            setBoss(boss);
            enter(boss);
            boss.zone.service.setPosition(boss, (byte) 0);
        } else {
            close();
        }

    }

    public void winner() {
        this.isFinish = true;
        this.countDown = 5;
        enter(trongTai);
    }

    public void close() {

        running = false;
//            TMap map = MapManager.getInstance().getMap(MapName.DAI_HOI_VO_THUAT);
        if (currFightingPlayer.zone == this) {
            if (currFightingPlayer.isDead()) {
                currFightingPlayer.setTypePK((byte) 0);
                currFightingPlayer.returnTownFromDead();
            } else {
                leave(currFightingPlayer);
                TMap map = MapManager.getInstance().getMap(MapName.DAI_HOI_VO_THUAT_3);
                int zoneId = map.getZoneID();
                map.enterZone(currFightingPlayer, zoneId);
            }
        }
        if (boss != null && boss.zone != null) {
            boss.setTypePK((byte) 0);
            leave(boss);
        }
        currFightingPlayer.setTypePK((byte) 0);
    }
}
