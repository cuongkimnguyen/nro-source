package com.ngocrong.map.tzone;

import com.ngocrong.NQMP.DHVTSieuHang.CloneSieuHang;
import com.ngocrong.consts.MobName;
import _HunrProvision.boss.Boss;
import com.ngocrong.bot.MiniDisciple;
import com.ngocrong.bot.Disciple;
import com.ngocrong.bot.VirtualBot;
import com.ngocrong.bot.boss.BossDisciple.SuperBroly;
import com.ngocrong.bot.boss.BossDisciple.Broly;
//import com.ngocrong.bot.boss.DuongTang;
import com.ngocrong.bot.boss.barrack.GeneralWhite;
import com.ngocrong.bot.boss.barrack.MajorMetallitron;
import com.ngocrong.bot.boss.mabu.BuiBui;
import com.ngocrong.bot.boss.mabu.Drabura;
import com.ngocrong.bot.boss.mabu.Mabu;
import com.ngocrong.bot.boss.mabu.Yacon;
import com.ngocrong.bot.boss.MatTroi;
import com.ngocrong.consts.ItemTimeName;
import com.ngocrong.consts.MapName;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemTime;
import com.ngocrong.map.MapService;
import com.ngocrong.map.TMap;
import com.ngocrong.mob.*;
import com.ngocrong.model.Npc;
import com.ngocrong.server.Config;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.SkillName;
import com.ngocrong.skill.SpecialSkill;
import com.ngocrong.task.Task;
import com.ngocrong.user.Player;
import com.ngocrong.user.Info;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Zone extends Thread {

    private static final Logger logger = Logger.getLogger(Zone.class);

    public static final byte PTS_GREEN = 0;
    public static final byte PTS_YELLOW = 1;
    public static final byte PTS_RED = 2;
    public static final byte TYPE_ALL = 0;
    public static final byte TYPE_HUMAN = 1;
    public static final byte TYPE_PET = 2;
    public static final byte TYPE_BOSS = 3;
    public static final byte TYPE_MINIPET = 4;
    public static final byte TYPE_ESCORT = 5;

    public short autoIncrease;

    public ReadWriteLock lockChar = new ReentrantReadWriteLock();
    public ReadWriteLock lockMob = new ReentrantReadWriteLock();
    public ReadWriteLock lockItemMap = new ReentrantReadWriteLock();
    public ReadWriteLock lockNpc = new ReentrantReadWriteLock();
    public ReadWriteLock lockSatellite = new ReentrantReadWriteLock();
    public ReadWriteLock lockRespawn = new ReentrantReadWriteLock();

    public TMap map;
    public MapService service;
    protected ArrayList<Npc> npcs;
    protected ArrayList<Mob> mobs;
    protected ArrayList<ItemMap> items;
    protected ArrayList<ItemMap> satellites;
    public ArrayList<Player> players;
    public ArrayList<Mob> waitForRespawn;
    public int zoneID;
    protected int numPlayer, maxPlayer;
    public boolean isHadSuperMob;
    public long[] lastUpdates = new long[100];
    public boolean running;
    public static int allCountMob = -1000000000;
    public boolean isDoneNRD;
    public boolean isActiveZone;
    public byte maxVirtual = (byte) Utils.nextInt(4, 8);

    public Zone(TMap map, int zoneId) {
        this.map = map;
        this.zoneID = zoneId;
        this.running = true;
        initial();
        start();
    }

    public void setMaxPlayer(int max) {
        maxPlayer = max;
    }

    public void initial() {
        long now = System.currentTimeMillis();
        service = new MapService(this);
        Arrays.fill(lastUpdates, now);
        this.numPlayer = 0;
        this.maxPlayer = 15;
        this.isHadSuperMob = false;
        this.mobs = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.players = new ArrayList<>();
        this.items = new ArrayList<>();
        this.satellites = new ArrayList<>();
        this.waitForRespawn = new ArrayList<>();
        for (MobCoordinate mobCoordinate : this.map.mobs) {
            Mob m = MobFactory.getMob(MobType.MOB);
            m.setMobId(allCountMob--);
            byte templateID = mobCoordinate.getTemplateID();
            MobTemplate template = Mob.getMobTemplate(templateID);
            m.setX(mobCoordinate.getX());
            m.setY(mobCoordinate.getY());
            m.setTemplateId(templateID);
            m.setHpDefault(template.hp);
            if (mobCoordinate.getHpMax() != -1) {
                m.setHPforMob(mobCoordinate.getHpMax());
            }
            m.setLevel(template.level);
            m.setDefault();
            m.setZone(this);
            addMob(m);
        }
        for (Npc npc : this.map.npcs) {
            Npc n = npc.clone();
            addNpc(n);
        }
        if (this.map.mapID == 175) {
            this.setMaxPlayer(127);
        }
    }

    public void addMob(Mob mob) {
        lockMob.writeLock().lock();
        try {
            mobs.add(mob);
        } finally {
            lockMob.writeLock().unlock();
        }

    }

    public void removeMob(Mob mob) {
        lockMob.writeLock().lock();
        try {
            mobs.remove(mob);
        } finally {
            lockMob.writeLock().unlock();
        }
    }

    public void addNpc(Npc npc) {
        lockNpc.writeLock().lock();
        try {
            npcs.add(npc);
        } finally {
            lockNpc.writeLock().unlock();
        }

    }

    public void removeNpc(Npc npc) {
        lockNpc.writeLock().lock();
        try {
            npcs.remove(npc);
        } finally {
            lockNpc.writeLock().unlock();
        }
    }

    public Npc findNpcByID(int id) {
        lockNpc.readLock().lock();
        try {
            for (Npc npc : this.npcs) {
                if (npc.template.npcTemplateId == id) {
                    return npc;
                }
            }
        } finally {
            lockNpc.readLock().unlock();
        }
        return null;
    }

    private void addChar(Player _player) {
        if (_player == null) {
            return;
        }
        lockChar.writeLock().lock();
        try {
            players.add(_player);
            if (_player.isHuman()) {
                this.numPlayer = getListChar(TYPE_HUMAN).size();
            }
        } finally {
            lockChar.writeLock().unlock();
        }
    }

    private void removeChar(Player _player) {
        if (_player == null) {
            return;
        }
        lockChar.writeLock().lock();
        try {
            players.remove(_player);
            if (_player.isHuman()) {
                this.numPlayer = getListChar(TYPE_HUMAN).size();
            }
        } finally {
            lockChar.writeLock().unlock();
        }
    }

    public Player findCharByID(int id) {
        lockChar.readLock().lock();
        try {
            for (Player p : players) {
                if (p.id == id) {
                    return p;
                }
            }
            return null;
        } finally {
            lockChar.readLock().unlock();
        }
    }

    //    public List<Char> getListChar() {
//        List<Char> list = new ArrayList<>();
//        lockChar.readLock().lock();
//        try {
//            for (Char _c : chars.values()) {
//                if (!_c.isLoggedOut()) {
//                    list.add(_c);
//                }
//            }
//        } finally {
//            lockChar.readLock().unlock();
//        }
//        return list;
//    }
    public List<Player> getListChar(int... arrs) {
        List<Player> list = new ArrayList<>();
        lockChar.readLock().lock();
        try {
            for (Player _c : players) {
                if (!_c.isLoggedOut()) {
                    boolean flag = false;
                    if (arrs.length > 0) {
                        for (int t : arrs) {
                            if (t == TYPE_ALL || (t == TYPE_HUMAN && _c.isHuman()) || (t == TYPE_PET && _c.isDisciple())
                                    || (t == TYPE_BOSS && _c.isBoss()) || (t == TYPE_MINIPET && _c.isMiniDisciple())
                                    || (t == TYPE_ESCORT && _c.isEscort())) {
                                flag = true;
                                break;
                            }
                        }
                    } else {
                        flag = true;
                    }
                    if (flag) {
                        list.add(_c);
                    }
                }
            }
        } finally {
            lockChar.readLock().unlock();
        }
        return list;
    }

    public List<Mob> getListMob() {
        List<Mob> list = new ArrayList<>();
        lockMob.readLock().lock();
        try {
            for (Mob mob : mobs) {
                list.add(mob);
            }
        } finally {
            lockMob.readLock().unlock();
        }
        return list;
    }

    public void addItemMap(ItemMap item) {
        addItemMap(item, false);
    }

    public void addItemMap(ItemMap item, boolean thuhut) {
        if (item.item.template.type == 22) {
            lockSatellite.writeLock().lock();
            try {
                satellites.add(item);
            } finally {
                lockSatellite.writeLock().unlock();
            }
        } else {
            if (item.playerID != -1 && this.findCharByID(item.playerID) != null && thuhut) {
                var player = this.findCharByID(item.playerID);
                if (player.isBuaThuHut()) {
                    player.pickItem(item, 0);
                    player.service.sendThongBao("Bạn nhặt được " + item.item.template.name);
                    return;
                }
            }
            lockItemMap.writeLock().lock();
            try {
                items.add(item);
            } finally {
                lockItemMap.writeLock().unlock();
            }
        }
    }

    public List<Player> getMemberSameClan(Player _c) {
        List<Player> list = new ArrayList<>();
        if (_c != null && _c.clan != null) {
            lockChar.readLock().lock();
            try {
                for (Player _player : players) {
                    if (_player.clan == _c.clan) {
                        list.add(_player);
                    }
                }
            } finally {
                lockChar.readLock().unlock();
            }
        }
        return list;
    }

    public List<ItemMap> getListItemMap(Task... tasks) {
        ArrayList<ItemMap> items = new ArrayList<>();
        lockItemMap.readLock().lock();
        try {
            for (ItemMap item : this.items) {
                items.add(item);
            }
        } finally {
            lockItemMap.readLock().unlock();
        }
        lockSatellite.readLock().lock();
        try {
            for (ItemMap item : this.satellites) {
                items.add(item);
            }
        } finally {
            lockSatellite.readLock().unlock();
        }
        return items;
    }

    public List<ItemMap> getListSatellite() {
        ArrayList<ItemMap> items = new ArrayList<>();
        lockSatellite.readLock().lock();
        try {
            for (ItemMap item : this.satellites) {
                items.add(item);
            }
        } finally {
            lockSatellite.readLock().unlock();
        }
        return items;
    }

    public List<Npc> getListNpc(Player _c) {
        List<Npc> list = new ArrayList<>();
        lockNpc.readLock().lock();
        try {
            for (Npc npc : npcs) {
                if (npc.templateId == 42) {
//                    if (_c.info.power < 17999000000L) {
//                        continue;
//                    }
                }
                if (_c.taskMain != null && _c.taskMain.id >= 20) {
                    if ((map.mapID == 27 || map.mapID == 28 || map.mapID == 29) && npc.templateId == 38) {
//                        if (Utils.nextInt(2) == 0) {
//                            continue;
//                        }
                        if (_c.taskMain != null && _c.checkCanEnter(102)) {
                            npc.x = (short) Utils.nextInt(100, map.width - 100);
                            npc.y = map.collisionLand(npc.x, (short) 24);
                        }
                    }
                }

                list.add(npc);
            }
        } finally {
            lockNpc.readLock().unlock();
        }
        return list;
    }

    public void removeItemMap(ItemMap item, boolean isUpdate) {
        if (isUpdate) {
            service.removeItemMap(item);
        }
        if (item.item.template.type == 22) {
            lockSatellite.writeLock().lock();
            try {
                satellites.remove(item);
            } finally {
                lockSatellite.writeLock().unlock();
            }
        } else {
            lockItemMap.writeLock().lock();
            try {
                items.remove(item);

            } finally {
                lockItemMap.writeLock().unlock();
            }
        }
    }

    public ItemMap findItemMapByID(int id) {
        lockItemMap.readLock().lock();
        try {
            for (ItemMap item : items) {
                if (item.id == id) {
                    return item;
                }
            }
            return null;
        } finally {
            lockItemMap.readLock().unlock();
        }
    }

    public ItemMap findItemMapByItemID(int id) {
        lockItemMap.readLock().lock();
        try {
            for (ItemMap item : items) {
                if (item.item.template.id == id) {
                    return item;
                }
            }
            return null;
        } finally {
            lockItemMap.readLock().unlock();
        }
    }

    public Player findItemLoot(int id) {
        lockChar.readLock().lock();
        try {
            for (Player pl : players) {
                if (pl.getItemLoot() != null && pl.getItemLoot().template.id == id) {
                    return pl;
                }
            }
            return null;
        } finally {
            lockChar.readLock().unlock();
        }
    }

    public void setOwnerForSatellite(Player _c) {
        List<ItemMap> list = getListSatellite();
        for (ItemMap item : list) {
            if (item.owner.id == _c.id) {
                item.owner = _c;
            }
        }
    }

    public void enter(Player _player) {

        _player.lastEnterMap = System.currentTimeMillis();
        try {
            _player.zone = this;
            if (_player.myDisciple != null && !_player.myDisciple.isDead() && _player.myDisciple.discipleStatus != 3 && !_player.isNhapThe()) {
                if (!map.isMapSingle() && !map.isDauTruong()) {
                    _player.myDisciple.followMaster();
                    enter(_player.myDisciple);
                } else {
                    _player.myDisciple.clearEffect();
                    if (_player.myDisciple.isMonkey()) {
                        _player.myDisciple.timeOutIsMonkey();
                    }
                }
            }
            if ((_player.isHuman() || _player.isDisciple()) && map.isCold() != _player.isCold()) {
                _player.info.setInfo();
                _player.service.loadPoint();
                service.playerLoadBody(_player);
                _player.setCold(map.isCold());
                if (map.isCold()) {
                    _player.service.sendThongBao("Bạn đã đến hành tinh Cold");
                    _player.service.sendThongBao("Sức tấn công và HP của bạn bị giảm 50% vì lạnh");
                } else {
                    _player.service.sendThongBao("Sức tấn công và HP của bạn đã trở lại bình thường");
                }
            }
            if (_player instanceof CloneSieuHang) {
                try {
                    _player.info.setInfo();
                } catch (Exception e) {
                    
                }
                _player.service.loadPoint();
                service.playerLoadBody(_player);
            }
            setOwnerForSatellite(_player);
            if (_player.service != null) {
                _player.service.setMapInfo();
            }
        } finally {
            addChar(_player);
            MiniDisciple mini = _player.getMiniDisciple();
            if (mini != null) {
                enter(mini);
                mini.move();
            }
        }
        if (!map.isBaseBabidi()) {
            if (_player.flag == 9 || _player.flag == 10) {
                _player.setAccumulatedPoint(null);
                _player.setFlag((byte) 0);
            }
        }
        if (_player.myDisciple != null) {
            if (_player.myDisciple.flag != _player.flag) {
                _player.myDisciple.setFlag();
            }
        }
        if (_player.isDead()) {
            _player.revival(100);
        }
        service.playerAdd(_player);
        _player.loadBody();
        service.updateBag(_player);
        _player.setFreeze(false);
        _player.freezSeconds = 0;
        List<Player> list = getListChar(Zone.TYPE_ALL);
        for (Player _c : list) {
            if (_c != _player) {
                _player.loadEffectSkillPlayer(_c);
                if (_player.getPetFollow() != null) {
                    _c.service.petFollow(_player, (byte) 1);
                }
                if (_player.mobMe != null) {
                    _c.service.mobMeUpdate(_player, null, -1, (byte) -1, (byte) 0);
                }
            }
        }
    }

    public void leave(Player _player) {
        if (_player == null) {
            return;
        }
        try {
            if (_player.myDisciple != null && !_player.myDisciple.isDead() && _player.myDisciple.discipleStatus != 3 && !_player.isNhapThe()) {
                Zone z = _player.myDisciple.zone;
                if (z != null) {
                    z.leave(_player.myDisciple);
                }
            }
            if (_player.hold != null) {
                _player.hold.close();
            }
            if (_player.mobMe != null) {
                service.mobMeUpdate(_player, null, -1, (byte) -1, (byte) 7);
            }
        } finally {
            removeChar(_player);
            MiniDisciple mini = _player.getMiniDisciple();
            if (mini != null) {
                leave(mini);
            }
//            Escort escort = _player.getEscortedPerson();
//            if (escort != null) {
//                leave(escort);
//            }
        }
        _player.clearMap();
        service.playerRemove(_player);
    }

    public int getNumPlayer() {
        return this.numPlayer;
    }

    public int getMaxPlayer() {
        return this.maxPlayer;
    }

    public int getPts() {
        if (this.numPlayer < 8) {
            return PTS_GREEN;
        } else if (this.numPlayer < 10) {
            return PTS_YELLOW;
        }
        return PTS_RED;
    }

    public void update() {
        long now = System.currentTimeMillis();
        List<Player> list = getListChar(Zone.TYPE_ALL);
        for (Player _c : list) {
            try {
                if (_c != null && _c.zone != null && _c.zone.equals(this)) {
                    _c.update();
                }
            } catch (Exception e) {
                if (!(_c instanceof VirtualBot)) {
                }
            }
        }
        if (now - lastUpdates[TMap.UPDATE_THIRTY_SECONDS] >= 10000) {
            lastUpdates[TMap.UPDATE_THIRTY_SECONDS] = now;
            try {
                updateSatellite();
            } catch (Exception e) {
                
                logger.error("update satellite err", e);
            }
        }
        if (now - lastUpdates[TMap.UPDATE_ONE_SECONDS] >= 1000) {
            lastUpdates[TMap.UPDATE_ONE_SECONDS] = now;
            try {
                List<Mob> list2 = getListMob();
                for (Mob mob : list2) {
                    if (mob != null) {
                        mob.update();
                    } else {
                        logger.error("mob is null");
                    }
                }
            } catch (Exception e) {
                
                logger.error("update mob err", e);
            }
            try {
                updateItemMap();
            } catch (Exception e) {
                
                logger.error("update item map err", e);
            }
            ArrayList<Mob> respawn = new ArrayList<>();
            try {
                try {
                    lockRespawn.readLock().lock();
                    try {
                        for (Mob mob : this.waitForRespawn) {
                            if (mob != null) {
                                if (now - mob.deadTime >= Mob.DELAY_RESPAWN) {
                                    respawn.add(mob);
                                }
                            } else {
                                logger.error("mob is null");
                            }
                        }
                    } finally {
                        lockRespawn.readLock().unlock();
                    }
                } catch (Exception e) {
                    
                    logger.error("get list respawn err", e);
                }
                if (respawn.size() > 0) {
                    respawn(respawn);
                }
            } catch (Exception e) {
                
                logger.error("respawn mob err", e);
            }
        }

    }

    public void addWaitForRespawn(Mob mob) {
        lockRespawn.writeLock().lock();
        try {
            waitForRespawn.add(mob);
        } finally {
            lockRespawn.writeLock().unlock();
        }
    }

    public void removeWaitForRespawn(Mob mob) {
        lockRespawn.writeLock().lock();
        try {
            waitForRespawn.remove(mob);
        } finally {
            lockRespawn.writeLock().unlock();
        }
    }

    private void updateSatellite() {
        List<ItemMap> list = getListSatellite();
        for (ItemMap item : list) {
            Player _c = item.owner;
            if (_c.clan != null) {
                List<Player> mems = getMemberSameClan(_c);
                for (Player _player : mems) {
                    int d = Utils.getDistance(item.x, item.y, _player.getX(), _player.getY());
                    if (d < item.r) {
                        if (item.item.id == 342) {
                            _player.info.recovery(Info.MP, 5, true);
                        } else if (item.item.id == 345) {
                            _player.info.recovery(Info.HP, 5, true);
                        }
                    }
                }
            } else {
                if (!_c.isLoggedOut()) {
                    int d = Utils.getDistance(item.x, item.y, _c.getX(), _c.getY());
                    if (d < item.r) {
                        if (item.item.id == 342) {
                            _c.info.recovery(Info.MP, 5, true);
                        } else if (item.item.id == 345) {
                            _c.info.recovery(Info.HP, 5, true);
                        }
                    }
                }
            }
        }
    }

    private void updateItemMap() {
        ArrayList<ItemMap> list = new ArrayList<>();
        long now = System.currentTimeMillis();
        lockItemMap.readLock().lock();
        try {
            for (ItemMap item : items) {
                long time = now - item.throwTime;
                if (time >= 40000L && !item.isBarrack) {
                    if (!item.isDragonBallNamec) {
                        list.add(item);
                    }
                }
            }
        } finally {
            lockItemMap.readLock().unlock();
        }
        lockSatellite.readLock().lock();
        try {
            for (ItemMap item : satellites) {
                long time = now - item.throwTime;
                if (time >= 1800000L) {
                    list.add(item);
                }
            }
        } finally {
            lockSatellite.readLock().unlock();
        }
        for (ItemMap item : list) {
            item.isPickedUp = true;
            removeItemMap(item, true);
        }
    }

    public Mob findMobByID(int id) {
        lockMob.readLock().lock();
        try {
            for (Mob mob : mobs) {
                if (mob.mobId == id) {
                    return mob;
                }
            }
            return null;
        } finally {
            lockMob.readLock().unlock();
        }
    }

    public Mob findMobByTemplateID(int id, boolean isDead) {
        lockMob.readLock().lock();
        try {
            for (Mob mob : mobs) {
                if (mob.isDead() == isDead && mob.templateId == id) {
                    return mob;
                }
            }
        } finally {
            lockMob.readLock().unlock();
        }
        return null;
    }

    public void attackPlayer(Player _player, Player target) {
        if (_player.isDead() || (target.isDead() && _player.select.template.id != SkillName.TRI_THUONG)) {
            return;
        }
        if (System.currentTimeMillis() - target.lastWakeUp <= 2000) {
            return;
        }
        ArrayList<Player> targets = new ArrayList<>();
        targets.add(target);
        Skill skill = _player.select;

        if (skill == null) {
            return;
        }
        if (this.map.isMapPet()) {
            if (!(_player instanceof Disciple)) {
                if (_player.select.template.id != SkillName.TROI && _player.select.template.id != SkillName.TRI_THUONG) {
                    return;
                }
            }
        }
        boolean flagSkill = System.currentTimeMillis() - skill.lastTimeUseThisSkill > skill.coolDown;
        long manaUse = skill.manaUse;
        if (skill.template.manaUseType == 1) {
            manaUse = Utils.percentOf(_player.info.mpFull, manaUse);
        }
        if (_player.isSkillSpecial() || _player.isBoss()) {
            manaUse = 0;
        }

        if (_player.info.mp < manaUse) {

            _player.service.sendThongBao("Không đủ KI đế sử dụng");
            return;
        }
        if (skill.template.type == 3) {

            return;
        }
        int distance1 = Utils.getDistance(_player.getX(), _player.getY(), target.getX(), target.getY());
        int distance2 = Utils.getDistance(0, 0, skill.dx, skill.dy);
        //if (!_char.select.isCooldown() || (_char.isSkillSpecial() && ((!_char.isCharge() && _char.getSeconds() == 0) || (_char.getSeconds() >= 500 && _char.getSeconds() <= 1000)))) {
        if (!skill.isCooldown() || _player.isSkillSpecial()) {
            int percentDame = skill.damage;
            boolean isMiss = Utils.nextInt(100) < target.info.percentMiss;
            boolean isCrit = Utils.nextInt(100) < _player.info.criticalFull;
            if (_player.isCritFirstHit()) {
                isCrit = true;
                _player.setCritFirstHit(false);
            }
            if (target.hold != null && target.isHeld() && target.hold.detainee == target) {
                isCrit = true;
            }
            boolean flagXChuong = false;
            SpecialSkill sp = _player.getSpecialSkill();
            switch (skill.template.id) {
                case SkillName.QUA_CAU_KENH_KHI:
                    isMiss = false;
                    break;
                case SkillName.THOI_MIEN: {
                    if (flagSkill) {
                        skill.lastTimeUseThisSkill = System.currentTimeMillis();
                        _player.info.mp -= manaUse;
                        service.setSkillPaint_2(_player, targets, (byte) skill.id);
                        ItemTime item = new ItemTime(ItemTimeName.THOI_MIEN, 3782, percentDame, false);
                        target.addItemTime(item);
                        target.setSleep(true);
                        service.setEffect(null, target.id, Skill.ADD_EFFECT, Skill.CHARACTER, (byte) 41);
                        if (sp != null) {
                            if (sp.id == 17) {
                                _player.setPercentDamageBonus(sp.param);
                            }
                        }
                    }
                    return;
                }
                case SkillName.DICH_CHUYEN_TUC_THOI: {
                    if (flagSkill) {
                        skill.lastTimeUseThisSkill = System.currentTimeMillis();
                        _player.setX(target.getX());
                        _player.setY(target.getY());
                        service.setEffect(null, target.id, Skill.ADD_EFFECT, Skill.CHARACTER, (byte) 40);
                        ItemTime item = new ItemTime(ItemTimeName.DICH_CHUYEN_TUC_THOI, 3779, 3, false);
                        target.addItemTime(item);
                        target.setBlind(true);
                        percentDame = 200 + (skill.point * 10);
                        _player.setCritFirstHit(true);
                        if (sp != null) {
                            if (sp.id == 16) {
                                _player.setPercentDamageBonus(sp.param);
                            }
                        }
                    }
                    break;
                }
                case SkillName.BIEN_SOCOLA: {
                    target.transformIntoChocolate(skill.damage, 30);
                    _player.info.mp -= manaUse;
                    service.setSkillPaint_2(_player, targets, (byte) skill.id);
                    _player.setCritFirstHit(true);
                    if (sp != null) {
                        if (sp.id == 27) {
                            _player.setPercentDamageBonus(sp.param);
                        }
                    }
                    return;
                }

                case SkillName.CHIEU_KAMEJOKO:
                case SkillName.CHIEU_MASENKO:
                case SkillName.CHIEU_ANTOMIC:
                    if (_player.achievements != null) {
                        _player.achievements.get(4).addCount(1);// Nội công cao cường
                    }
                    flagXChuong = true;
                    break;
                default:
                    break;
            }
            long damageFull = _player.info.damageFull;

            long dame = damageFull + Utils.percentOf(damageFull, (percentDame - 100));
            dame = Utils.nextLong(dame - (dame / 10), dame);

            if ((skill.template.id == SkillName.CHIEU_KAMEJOKO)) {
                dame += (dame * _player.info.optionKame / 100);
            }
            if ((skill.template.id == SkillName.CHIEU_KAMEJOKO && _player.isSetSongoku())) {
                dame += Utils.percentOf(dame, 100);
            }
            if ((skill.template.id == SkillName.CHIEU_DAM_GALICK && _player.isSetKakarot())) {
                dame += Utils.percentOf(dame, 150);
            }
            if (flagXChuong && _player.info.options[159] > 0 && System.currentTimeMillis() - _player.lastXChuong >= 60000) {
                if (target.isHuman()) {
                    dame *= _player.info.options[159];
                    _player.lastXChuong = System.currentTimeMillis();
                }
            }
            boolean xuyenGiap = false;
            if (_player.info.options[98] > 0 && (skill.template.id == SkillName.CHIEU_KAMEJOKO || skill.template.id == SkillName.CHIEU_MASENKO || skill.template.id == SkillName.CHIEU_ANTOMIC)) {
                int rd = Utils.nextInt(100);
                if (rd < _player.info.options[98]) {
                    xuyenGiap = true;
                }
            }
            int pPhanDonCanChien = 0;
            if (skill.template.id == SkillName.CHIEU_DAM_DRAGON || skill.template.id == SkillName.CHIEU_DAM_DEMON || skill.template.id == SkillName.CHIEU_DAM_GALICK || skill.template.id == SkillName.KAIOKEN || skill.template.id == SkillName.LIEN_HOAN) {
                if (_player.info.options[99] > 0) {
                    int rd = Utils.nextInt(100);
                    if (rd < _player.info.options[99]) {
                        xuyenGiap = true;
                    }
                }
                pPhanDonCanChien = _player.info.options[15];
            }

            if (map.isBaseBabidi() && !target.isBoss() && !_player.isBoss()) {
                dame = target.info.hpFull / 10;
            }

            if (dame <= 0) {
                dame = 1;
            }

            if (target instanceof GeneralWhite) {
                Mob mob = findMobByTemplateID(22, false);
                if (mob != null) {
                    isMiss = true;
                }
            }
            if (target.info.options[3] > 0 && (skill.template.id == SkillName.CHIEU_KAMEJOKO || skill.template.id == SkillName.CHIEU_MASENKO || skill.template.id == SkillName.CHIEU_ANTOMIC)) {
                long mp = Utils.percentOf(dame, target.info.options[3]);
                target.info.recovery(Info.MP, mp);
                isMiss = true;
            }
            if (target.isGiapXen()) {
                dame /= 2;
            }

            if (target.info.options[157] > 0) {
                long pM = target.info.mp * 100 / target.info.mpFull;
                if (pM < 20) {
                    dame -= Utils.percentOf(dame, target.info.options[157]);
                }
            }

            if (sp != null) {
                if ((sp.id == 1 && skill.template.id == SkillName.CHIEU_DAM_GALICK) || (sp.id == 2 && skill.template.id == SkillName.CHIEU_ANTOMIC) || (sp.id == 3 && _player.isMonkey())
                        || (sp.id == 11 && skill.template.id == SkillName.CHIEU_DAM_DRAGON) || (sp.id == 12 && skill.template.id == SkillName.CHIEU_KAMEJOKO)
                        || (sp.id == 21 && skill.template.id == SkillName.CHIEU_DAM_DEMON) || (sp.id == 22 && skill.template.id == SkillName.CHIEU_MASENKO) || (sp.id == 26 && skill.template.id == SkillName.LIEN_HOAN)) {
                    dame += Utils.percentOf(dame, sp.param);
                }
                if (sp.id == 31) {
                    long pHP = _player.info.hp * 100 / _player.info.hpFull;
                    if (pHP < sp.param) {
                        isCrit = true;
                    }
                }
            }

            if (skill.template.id == SkillName.CHIEU_DAM_DRAGON || skill.template.id == SkillName.CHIEU_KAMEJOKO || skill.template.id == SkillName.CHIEU_DAM_DEMON || skill.template.id == SkillName.CHIEU_MASENKO || skill.template.id == SkillName.CHIEU_DAM_GALICK || skill.template.id == SkillName.CHIEU_ANTOMIC || skill.template.id == SkillName.LIEN_HOAN || skill.template.id == SkillName.KAIOKEN) {
                int percentDamageBonus = _player.getPercentDamageBonus();
                if (percentDamageBonus > 0) {
                    dame += Utils.percentOf(dame, percentDamageBonus);
                    _player.setPercentDamageBonus(0);
                }
            }

            boolean flag = false;
            if (skill.template.id == SkillName.MAKANKOSAPPO) {
                dame = Utils.percentOf(_player.info.mp, percentDame);
                dame += (dame * _player.info.optionLaze / 100);
                isCrit = false;
                isMiss = false;
                _player.info.mp = 1;
                flag = true;
            } else if (skill.template.id == SkillName.QUA_CAU_KENH_KHI) {
                long hp = getTotalHP();
                dame = (hp / 10) + (_player.info.damageFull * 10);
                if (target.isBoss()) {
                    dame /= 2;
                }
                if (_player.isSetKirin()) {
                    dame += Utils.percentOf(dame, 100);
                }
                flag = true;
            } else if (skill.template.id == SkillName.LIEN_HOAN) {
                if (_player.isSetOcTieu()) {
                    dame += Utils.percentOf(dame, 100);
                }
            }
            if (_player.info.options[111] > Utils.nextInt(100)) {
                if (!target.exitsItemTime(ItemTimeName.KHANG_XINBATO)) {
                    isMiss = true;
                }
            }
            if (_player.isSkillSpecial()) {
                isCrit = false;
            }
            if (isCrit) {
                int rd = Utils.nextInt(100);
                if (rd < target.info.options[191]) {
                    isCrit = false;
                }
            }
            if (isCrit) {
                dame *= 2;
                dame += Utils.percentOf(dame, _player.info.options[5]);
            }

            if (target.limitDame != -1) {
                if (_player.mapPhuHo == 114) {
                    if (target instanceof BuiBui || target instanceof Drabura || target instanceof Mabu || target instanceof Yacon) {
                        if (dame > target.limitDame * 2) {
                            dame = target.limitDame * 2;
                        }
                    }
                } else {
                    if (dame > target.limitDame) {
                        dame = target.limitDame;
                    } else if (flag && dame > target.limitDame * 10) {
                        dame = target.limitDame * 10;
                    }
                }
            }

            if (_player.isBoss()) {
                Boss boss = (Boss) _player;
                if (boss.percentDame != -1) {
                    dame = Utils.percentOf(target.info.hpFull, boss.percentDame);
                }
            }
            dame -= Utils.percentOf(dame, target.info.options[94]);
            if (dame > 0) {

                dame = target.injure(_player, null, dame);

                long reactDame = Utils.percentOf(dame, target.info.options[97] + pPhanDonCanChien);
                if (!(_player instanceof Broly) && !(_player instanceof SuperBroly)) {
                    reactDame = _player.injure(target, null, reactDame);
                }
                if (!_player.canReactDame) {
                    reactDame = -1;
                }
                if (reactDame >= _player.info.hp) {
                    reactDame = _player.info.hp - 1;
                }
                if (target instanceof Broly || target instanceof SuperBroly) {
                    reactDame = -1;
                }
                if (reactDame > 0) {
                    if (_player.info.hp > 1) {
                        _player.info.hp -= reactDame;
                        service.attackPlayer(_player, reactDame, false, (byte) 36);
                    } else if (_player.info.hp == 1) {
                        service.attackPlayer(_player, 0, false, (byte) 36);
                    }
                }
            }
            if (!isMiss && target.isProtected()) {
                if (dame > target.info.hpFull) {
                    target.setTimeForItemtime(0, 0);
                    target.service.sendThongBao("Khiên năng lượng đã vỡ");
                }
                dame = 1;
                if (target.info.hp <= dame) {
                    isMiss = true;
                }
            }
            if (isMiss) {
                dame = 0;
            }
            if (dame == 0) {
                isCrit = false;
            }
            if (distance1 > distance2 + 50) {
                dame = 0;
            }
            if (target instanceof MajorMetallitron) {
                long p = target.info.hpFull / 100;
                if (dame > p) {
                    int skillTemplateId = skill.template.id;
                    if (skillTemplateId == SkillName.CHIEU_DAM_DRAGON || skillTemplateId == SkillName.CHIEU_DAM_DEMON || skillTemplateId == SkillName.CHIEU_DAM_GALICK) {
                        dame = p;
                    }
                }
            }
//            dame = target.injure(_player, null, dame);
            if (dame < 0) {
                dame = 0;
            }
            if (!isMiss && target.isProtected()) {
                if (dame > target.info.hpFull) {
                    target.setTimeForItemtime(0, 0);
                    target.service.sendThongBao("Khiên năng lượng đã vỡ");
                }
                dame = 1;
                if (target.info.hp <= dame) {
                    isMiss = true;
                }
            }
            if (_player.useSkill(target)) {
                if (!_player.isSkillSpecial()) {
                    long now = System.currentTimeMillis();
                    skill.lastTimeUseThisSkill = now;
                }
                _player.info.mp -= manaUse;
                if (!_player.isBoss()) {
                    _player.info.updateStamina(-1);
                }
                target.lock.lock();
                try {
                    if (_player.isDead() || target.isDead()) {
                        return;
                    }
                    service.setSkillPaint_2(_player, targets, (byte) skill.id);
                    if (skill.template.id == SkillName.DICH_CHUYEN_TUC_THOI) {
                        service.setPosition(_player, (byte) 1);
                    }
                    if (target.myDisciple != null && target.myDisciple.zone == this) {
                        target.myDisciple.addTarget(_player);
                    }
                    if (target.isDisciple()) {
                        Disciple disciple = (Disciple) target;
                        disciple.addTarget(_player);
                    }
                    if (dame > 0) {
                        if (target.dameSTC != 0) {
                            if (target.dameSTC == 1 && dame % 2 == 0) {
                                dame++;
                            }
                            if (target.dameSTC == 2 && dame % 2 == 1) {
                                dame++;
                            }
                            if (target.dameSTC == 3) {
                                if (dame % 10 < 5) {
                                    dame += 5;
                                }
                                if (dame % 2 == 1) {
                                    dame++;
                                }
                            }
                            if (target.dameSTC == 4) {

                                if (dame % 10 < 5) {
                                    dame += 5;
                                }
                                if (dame % 2 == 0) {
                                    dame++;
                                }
                            }
                            if (target.dameSTC == 5) {
                                if (dame % 10 >= 5) {
                                    dame -= 5;
                                }
                                if (dame % 2 == 1) {
                                    dame++;
                                }
                            }
                            if (target.dameSTC == 6) {
                                if (dame % 10 >= 5) {
                                    dame -= 5;
                                }
                                if (dame % 2 == 0) {
                                    dame++;
                                }
                            }
                            target.dameSTC = 0;
                        }
                        target.info.hp -= dame;
                        if (skill.template.id == SkillName.QUA_CAU_KENH_KHI) {
                            lockMob.readLock().lock();
                            try {
                                for (Mob mob : mobs) {
                                    if (!mob.isDead()) {
                                        int distance = Utils.getDistance(mob.x, mob.y, target.getX(), target.getY());
                                        if (distance < distance2) {
                                            mob.hp -= dame;
                                            service.attackNpc(dame, isCrit, mob, (byte) -1);
                                            if (mob.hp <= 0) {
                                                _player.kill(mob);
                                                mob.startDie(dame, isCrit, _player);
                                            }
                                        }
                                    }
                                }
                            } finally {
                                lockMob.readLock().unlock();
                            }
                        }
                        if (target.isBoss()) {
                            Boss boss = (Boss) target;
                            boss.addTarget(_player);
                        }
                        _player.info.recovery(Info.HP, Utils.percentOf(dame, _player.info.options[95]));
                        _player.info.recovery(Info.MP, Utils.percentOf(dame, _player.info.options[96]));
//                        if (!target.isBoss()) {
//                            long exp = dame / 10;
//                            if (exp <= 0) {
//                                exp = 1;
//                            }
//                            _player.addExp(Info.POWER_AND_POTENTIAL, exp, true, true);
//                        }
                    }
                    service.attackPlayer(target, dame, isCrit, (byte) -1);
                    if (target.info.hp <= 0) {
                        _player.kill(target);
                        target.killed(_player);
                        target.startDie();
                        if (_player.isAutoPlay()) {
                            Mob mob = findMob();
                            if (mob != null) {
                                _player.setX(mob.x);
                                _player.setY(mob.y);
                                service.setPosition(_player, (byte) 0);
                            }
                        }
                    }
                } finally {
                    target.lock.unlock();
                }
            }
            _player.setSkillSpecial(false);
            if (_player.mobMe != null) {
                if (target.info.hpFull <= 0) {
                    return;
                }
                double targetPercent = (target.info.hp * 100.0) / target.info.hpFull;
                if (targetPercent < 5.0) {
                    return;
                }
                _player.mobMe.attack(_player, target);
            }
        }
    }

    public long getTotalHP() {
        long totalHP = 0;
        lockChar.readLock().lock();
        try {
            for (Player _c : players) {
                if (_c.isHuman()) {
                    totalHP += _c.info.hp;
                }
            }
        } finally {
            lockChar.readLock().unlock();
        }
        lockMob.readLock().lock();
        try {
            for (Mob mob : mobs) {
                totalHP += mob.hp;
            }
        } finally {
            lockMob.readLock().unlock();
        }
        return totalHP;
    }

    public Mob findMob() {
        lockMob.readLock().lock();
        try {
            for (Mob mob : mobs) {
                if (!mob.isDead()) {
                    return mob;
                }
            }
        } finally {
            lockMob.readLock().unlock();
        }
        return null;
    }

    public void attackNpc(Player _player, Mob target, boolean isMobMe) {
        if (_player.isDead() || target.isDead()) {
            return;
        }
        Skill skill = _player.select;
        if (skill == null) {
            return;
        }
        if (_player.isBuaBatTu() && _player.info.hp == 1) {
            _player.service.sendThongBao("Bạn đang được bảo vệ bởi bùa bất tử, không thể đánh");
            return;
        }
        if (this.map.isMapPet()) {
            if (!(_player instanceof Disciple)) {
                if (_player.select.template.id != SkillName.TROI && _player.select.template.id != SkillName.TRI_THUONG) {
                    return;
                }
            }
        }
        long manaUse = skill.manaUse;
        if (skill.template.manaUseType == 1) {
            manaUse = Utils.percentOf(_player.info.mpFull, manaUse);
        }
        if (_player.isSkillSpecial() || _player.isBoss()) {
            manaUse = 0;
        }
        if (_player.info.mp < manaUse) {
            _player.service.sendThongBao("Không đủ KI đế sử dụng");
            return;
        }
        if (skill.template.type == 3) {
            return;
        }
        int distance1 = Utils.getDistance(_player.getX(), _player.getY(), target.x, target.y);
        int distance2 = Utils.getDistance(0, 0, skill.dx, skill.dy);
        //if (!_char.select.isCooldown() || (_char.isSkillSpecial() && ((!_char.isCharge() && _char.getSeconds() == 0) || (_char.getSeconds() >= 500 && _char.getSeconds() <= 1000)))) {
        //long time = System.currentTimeMillis() - skill.lastTimeUseThisSkill;
        if (!skill.isCooldown() || _player.isSkillSpecial()) {
            int percentDame = skill.damage;
            boolean isMiss = Utils.nextInt(10) == 0;
            boolean isCrit = Utils.nextInt(100) < _player.info.criticalFull;
            if (_player.isCritFirstHit()) {
                isCrit = true;
                _player.setCritFirstHit(false);
            }

            SpecialSkill noitai = _player.getSpecialSkill();
            switch (skill.template.id) {
                case SkillName.QUA_CAU_KENH_KHI:
                    isMiss = false;
                    break;
                case SkillName.THOI_MIEN: {
                    _player.info.mp -= manaUse;
                    service.setSkillPaint_1(target, _player, (byte) skill.id);
                    ItemTime item = new ItemTime(ItemTimeName.THOI_MIEN, 3782, percentDame, false);
                    target.addItemTime(item);
                    target.isSleep = true;
                    service.setEffect(null, target.mobId, Skill.ADD_EFFECT, Skill.MONSTER, (byte) 41);
                    if (noitai != null) {
                        if (noitai.id == 17) {
                            _player.setPercentDamageBonus(noitai.param);
                        }
                    }
                    return;
                }
                case SkillName.DICH_CHUYEN_TUC_THOI: {
                    _player.setX(target.x);
                    _player.setY(target.y);
                    service.setEffect(null, target.mobId, Skill.ADD_EFFECT, Skill.MONSTER, (byte) 40);
                    ItemTime item = new ItemTime(ItemTimeName.DICH_CHUYEN_TUC_THOI, 3783, 3, false);
                    target.addItemTime(item);
                    target.isBlind = true;
                    percentDame = 200 + (skill.point * 10);
                    _player.setCritFirstHit(true);
                    if (noitai != null) {
                        if (noitai.id == 16) {
                            _player.setPercentDamageBonus(noitai.param);
                        }
                    }

                    break;
                }
                case SkillName.BIEN_SOCOLA: {
                    if (!isMobMe) {
                        target.setBody((short) 4132);
                        target.dameDown = skill.damage;
                        ItemTime item = new ItemTime(ItemTimeName.SOCOLA, 4127, 30, false);
                        target.addItemTime(item);
                        service.changeBodyMob(target, (byte) 1);
                        _player.info.mp -= manaUse;
                        service.setSkillPaint_1(target, _player, (byte) skill.id);
                        _player.setCritFirstHit(true);
                        if (noitai != null) {
                            if (noitai.id == 27) {
                                _player.setPercentDamageBonus(noitai.param);
                            }
                        }
                    }
                    return;
                }

                case SkillName.CHIEU_KAMEJOKO:
                case SkillName.CHIEU_MASENKO:
                case SkillName.CHIEU_ANTOMIC:
                    if (_player.achievements != null) {
                        _player.achievements.get(4).addCount(1);// Nội công cao cường
                    }
                    break;
                default:
                    break;
            }
            long damageFull = _player.info.damageFull;
            long dame = damageFull + Utils.percentOf(damageFull, (percentDame - 100));
            dame = Utils.nextLong(dame - (dame / 10), dame);
            dame += Utils.percentOf(dame, _player.info.options[19]);
            if (_player.isBuaManhMe()) {
                dame *= 2;
            }
            if ((skill.template.id == SkillName.CHIEU_DAM_GALICK && _player.isSetKakarot()) || (skill.template.id == SkillName.CHIEU_KAMEJOKO && _player.isSetSongoku())) {
                dame *= 3;
            }
            if (_player.isDisciple()) {
                Disciple disciple = (Disciple) _player;
                if (disciple.master.isBuaDeTu() && disciple.typeDisciple != 2) {
                    dame *= 2;
                }
            }

            if (noitai != null) {
                if ((noitai.id == 1 && skill.template.id == SkillName.CHIEU_DAM_GALICK) || (noitai.id == 2 && skill.template.id == SkillName.CHIEU_ANTOMIC) || (noitai.id == 3 && _player.isMonkey())
                        || (noitai.id == 11 && skill.template.id == SkillName.CHIEU_DAM_DRAGON) || (noitai.id == 12 && skill.template.id == SkillName.CHIEU_KAMEJOKO)
                        || (noitai.id == 21 && skill.template.id == SkillName.CHIEU_DAM_DEMON) || (noitai.id == 22 && skill.template.id == SkillName.CHIEU_MASENKO) || (noitai.id == 26 && skill.template.id == SkillName.LIEN_HOAN)) {
                    dame += Utils.percentOf(dame, noitai.param);
                }
                if (noitai.id == 31) {
                    long pHP = _player.info.hp * 100 / _player.info.hpFull;
                    if (pHP < noitai.param) {
                        isCrit = true;
                    }
                }
            }
            if (_player.isSkillSpecial()) {
                isCrit = false;
            }

            if (isCrit) {
                dame *= 2;
                dame += Utils.percentOf(dame, _player.info.options[5]);
            }

            if (skill.template.id == SkillName.CHIEU_DAM_DRAGON || skill.template.id == SkillName.CHIEU_KAMEJOKO || skill.template.id == SkillName.CHIEU_DAM_DEMON || skill.template.id == SkillName.CHIEU_MASENKO || skill.template.id == SkillName.CHIEU_DAM_GALICK || skill.template.id == SkillName.CHIEU_ANTOMIC || skill.template.id == SkillName.LIEN_HOAN || skill.template.id == SkillName.KAIOKEN) {
                int percentDamageBonus = _player.getPercentDamageBonus();
                if (percentDamageBonus > 0) {
                    dame += Utils.percentOf(dame, percentDamageBonus);
                    _player.setPercentDamageBonus(0);
                }
            }
            if (skill.template.id == 11) {
                dame = Utils.percentOf(_player.info.mp, percentDame);
//                if (_player.isSetPicolo()) {
//                    dame += dame / 2;
//                }
                isCrit = false;
                isMiss = false;
                _player.info.mp = 1;
            } else if (skill.template.id == SkillName.QUA_CAU_KENH_KHI) {
                long hp = getTotalHP();
                dame = (hp / 10) + (_player.info.damageFull * 10);
                if (_player.isSetKirin()) {
                    dame *= 2;
                }
            } else if (skill.template.id == SkillName.LIEN_HOAN) {
                if (_player.isSetOcTieu()) {
                    dame += Utils.percentOf(dame, 100);
                }
            }
            if (_player.info.options[111] > Utils.nextInt(100)) {
                isMiss = true;
            }
//            if (map.isNguHanhSon()) {
//                dame = 80000;
//            }
            if (!_player.isSkillSpecial()) {
                if (target.templateId == MobName.MOC_NHAN) {
                    dame = 10;
                } else {
                    if (isMiss) {
                        dame = -1;
                    }
                }
                if (distance1 > distance2 + 50) {
                    dame = 0;
                }
            }
            if (dame == 0) {
                isCrit = false;
            }
            if (dame >= target.hp) {
                if (target.hp == target.maxHp && !_player.isSkillSpecial()) {
                    dame = target.hp - 1;
                } else {
                    dame = target.hp;
                }
            }
            if (target.templateId == 70) {
                if (_player.mapPhuHo == 126) {
                    dame = Math.max(2, target.hp / 50);
                } else {
                    dame = Math.max(1, target.hp / 100);
                }
            }
            if (_player.useSkill(target)) {
                if (!_player.isSkillSpecial()) {
                    long now = System.currentTimeMillis();
                    skill.lastTimeUseThisSkill = now;
                }
                _player.info.mp -= manaUse;
                if (!(_player.isBuaDeoDai() || _player.isBoss())) {
                    _player.info.updateStamina(-1);
                } else {
                    _player.service.setStamina();
                }
                target.lock.lock();
                try {
                    if (_player.isDead() || target.isDead()) {
                        return;
                    }
                    service.setSkillPaint_1(target, _player, (byte) skill.id);
                    if (skill.template.id == SkillName.DICH_CHUYEN_TUC_THOI) {
                        service.setPosition(_player, (byte) 1);
                    }
                    if (dame > 0) {
                        if (!_player.isSkillSpecial()) {
                            if (!_player.isBuaOaiHung() && !_player.isAutoPlay()) {
                                if (target.levelBoss != 0) {
                                    long hp10Percent = target.maxHp / 10;
                                    if (dame > hp10Percent) {
                                        dame = hp10Percent;
                                    }
                                }
                            }
                        }
                        target.hp -= dame;
//                        if (target instanceof com.ngocrong.mob._BigBoss.Hirudegarn) {
//                            ((com.ngocrong.mob._BigBoss.Hirudegarn) target).addDamage(_player, dame);
//                        }
                        if (skill.template.id == SkillName.QUA_CAU_KENH_KHI) {
                            lockMob.readLock().lock();
                            try {
                                for (Mob mob : mobs) {
                                    if (!mob.isDead()) {
                                        int distance = Utils.getDistance(mob.x, mob.y, target.x, target.y);
                                        if (distance < distance2) {
                                            mob.hp -= dame;
//                                            if (mob instanceof com.ngocrong.mob._BigBoss.Hirudegarn) {
//                                                ((com.ngocrong.mob._BigBoss.Hirudegarn) mob).addDamage(_player, dame);
//                                            }
                                            service.attackNpc(dame, isCrit, mob, (byte) -1);
                                            if (mob.hp <= 0) {
                                                _player.kill(mob);
                                                mob.startDie(dame, isCrit, _player);
                                            }
                                        }
                                    }
                                }
                            } finally {
                                lockMob.readLock().unlock();
                            }
                        }
                        _player.info.recovery(Info.HP, Utils.percentOf(dame, _player.info.options[95] + _player.info.options[104]));
                        _player.info.recovery(Info.MP, Utils.percentOf(dame, _player.info.options[96]));
                    }
                    if (!isMobMe) {
                        long exp = dame / 10;
                        if (exp <= 0) {
                            exp = 1;
                        }
                        //   exp = callEXP(_player, exp);

                        // tuong lai = 0.7
                        if (map.isFuture()) {
//                            exp -= Utils.percentOf(exp, 30);
                        } else if (map.isNappa()) {
                            // nappa = 0.8
                            exp -= Utils.percentOf(exp, 20);
                        } else if (map.isCold()) {
                            //cold= 1.5
                            exp += Utils.percentOf(exp, 50);
                        } else if (map.mapID >= 168 && map.mapID <= 174) {
                            //maptet = 0.01
//                            exp /= 100;                     
                        } else if (map.isBarrack() || map.mapID == 181 || map.mapID == 183 || map.mapID == 184) {
                            exp = 0;
                        } else if (exp > 10_000_000) {
                            // tnsm > 10tr
                            exp = Utils.nextInt(9_000_000, 10_000_000);
                        }

                        if (noitai != null) {
                            if (noitai.id == 30) {
                                exp += exp * noitai.param / 100;
                            }
                        }
                        int percent = 0;
                        if (_player.flag > 0) {
                            percent += 5;
                            if (_player.flag == 8) {
                                percent += 5;
                            }
                        }
                        percent += _player.info.options[101] + _player.info.options[155] + _player.info.options[88] + _player.info.options[83];
                        int n = _player.checkEffectOfSatellite(343);
                        percent += (n * 20);
                        exp += exp * percent / 100;
                        if (exp <= 0) {
                            exp = 1;
                        }
                        _player.addExp(Info.POWER_AND_POTENTIAL, exp, true, true);
                    }
                    if (target.hp <= 0) {
                        if (!isMobMe) {
                            _player.kill(target);
                            target.startDie(dame, isCrit, _player);
                        }
                    } else {
                        if (target.templateId != MobName.MOC_NHAN) {
                            target.addChar(_player);
                        }
                        if (!isMobMe) {
                            service.attackNpc(dame, isCrit, target, (byte) -1);
                        } else {
                            service.mobMeUpdate(_player, target, dame, (byte) skill.id, (byte) 5);
                        }
                    }
                } finally {
                    target.lock.unlock();
                }
            }
            _player.setSkillSpecial(false);
            if (_player.mobMe != null) {
                _player.mobMe.attack(_player, target);
            }
        }
        if (target.status == 4 && target.levelBoss != 0 && target.meCantAttack()) {
            target.attack(_player);
        }
    }

    public static long callEXP(Player player, long oldEXP) {
        double num = 1;
        long result = oldEXP;
        long pw = player.info.power;
        if (pw < 10_000_000_000L) {
            num = 25;
        } else if (pw < 20_000_000_000L) {
            num = 2;
        } else if (pw < 30_000_000_000L) {
            num = 1;
        } else if (pw < 40_000_000_000L) {
            num = 0.5;
        } else if (pw < 70_000_000_000L) {
            num = 0.25;
        } else if (pw < 80_000_000_000L) {
            num = 0.015625;
        } else if (pw < 100_000_000_000L) {
            num = 0.007625;
        } else if (pw < 105_000_000_000L) {
            num = 0.003525;
        } else if (pw < 110_000_000_000L) {
            num = 0.00125;
        } else if (pw < 115_000_000_000L) {
            num = 0.000590625;
        } else {
            num = 0.00000190625;
        }
        if (player instanceof Disciple) {
            Disciple pet = (Disciple) player;
            if (pet.typeDisciple == 2 && pw >= 40_000_000_000L) {
                num /= 3;
            }
        }
        int bonusEXP = 3;
        return (long) ((double) result * num * bonusEXP);
    }

    public void respawn(ArrayList<Mob> mobs) {
        boolean isBarrack = map.isBarrack();
        boolean isBossLive = false;
        try {
            if (map.mapID == MapName.TUONG_THANH_3) {
                Player boss = getBoss(GeneralWhite.class);
                if (boss != null && !boss.isDead()) {
                    isBossLive = true;
                }
            }
        } catch (Exception e) {
            
            logger.error("check err", e);
        }
        for (Mob mob : mobs) {
            if (!isBarrack || (mob.templateId == MobName.BULON && isBossLive)) {
                mob.respawn();
                removeWaitForRespawn(mob);
                if (service != null) {
                    service.respawn(mob);
                } else {
                    logger.error("service is null");
                }
            }
        }
    }

    public Boss getBoss(Class aClass) {
        List<Player> list = getListChar(TYPE_BOSS);
        for (Player _c : list) {
            if (_c.getClass().getName().equals(aClass.getName()) && aClass.getPackage() == aClass.getPackage()) {
                return (Boss) _c;
            }
        }
        return null;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public List<ItemMap> getItems() {
        return this.items;
    }

    public List<Player> getBossInZone() {
        return getListChar(TYPE_BOSS);
    }

    @Override
    public void run() {
        long delay = 100;
        while (this.running) {
            try {
                long l1 = System.currentTimeMillis();
                update();
                long l2 = System.currentTimeMillis();
                long l3 = l2 - l1;
                if (l3 > delay) {
                    continue;
                }
                Thread.sleep(delay - l3);
            } catch (Exception e) {
                
                logger.debug("while loop err", e);
            }
        }
    }

}
