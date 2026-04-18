package com.ngocrong.NQMP.Cumber;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.lib.RandomCollection;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.mob.Mob;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Cumber extends Boss {

    private static final Logger logger = Logger.getLogger(Cumber.class);

    private final boolean isSuper;

    public static final int MAP = 19;

    public static RandomCollection<Integer> ITEMS = new RandomCollection<>();

    public static RandomCollection<Integer> ITEM_SUPER = new RandomCollection<>();

    static {
        ITEMS.add(33, ItemName.GIAY_THAN_XAYDA);
        ITEMS.add(33, ItemName.GIAY_THAN_LINH);
        ITEMS.add(33, ItemName.GIAY_THAN_NAMEC);
    }

    public Cumber(boolean isSuper) {
        super();
        this.isSuper = isSuper;
        this.limit = -1;
        if (isSuper) {
            this.name = "Super Cumber";
            setInfo(10000000000L, Long.MAX_VALUE, 1500000, 1000, 10);
        } else {
            this.name = "Cumber";
            setInfo(5000000000L, Long.MAX_VALUE, 1000000, 1000, 10);
        }
        setDefaultPart();
        setTypePK((byte) 5);
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            skills.add(Skills.getSkill((byte) 0, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 1, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 8, (byte) 7).clone());

        } catch (Exception ex) {
            
            logger.error("init skill err");
        }
    }

    @Override
    public void setDefaultLeg() {
        if (isSuper) {
            setLeg((short) 2146);
        } else {
            setLeg((short) 2143);
        }
    }

    @Override
    public void setDefaultBody() {
        if (isSuper) {
            setBody((short) 2145);
        } else {
            setBody((short) 2142);
        }
    }

    @Override
    public void setDefaultHead() {
        if (isSuper) {
            setHead((short) 2144);
        } else {
            setHead((short) 2141);
        }
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
        SessionManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s", this.name, map));
        logger.debug(String.format("BOSS %s vừa xuất hiện tại %s khu vực %d", this.name, map, zone.zoneID));
    }

    @Override
    public void sendNotificationWhenDead(String name) {
        SessionManager.chatVip(String.format("%s: Đã tiêu diệt được %s mọi người đều ngưỡng mộ.", name, this.name));
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        int[] random = {
            ItemName.AO_THAN_LINH, ItemName.AO_THAN_XAYDA, ItemName.AO_THAN_NAMEC,
            ItemName.QUAN_THAN_LINH, ItemName.QUAN_THAN_NAMEC,
            ItemName.GIAY_THAN_LINH, ItemName.GIAY_THAN_XAYDA, ItemName.GIAY_THAN_NAMEC
        };
        int[] Gang = {
            ItemName.GANG_THAN_LINH, ItemName.GANG_THAN_XAYDA, ItemName.GANG_THAN_NAMEC, ItemName.GANG_THAN_XAYDA, ItemName.GANG_THAN_NAMEC
        };
        Player c = (Player) obj;
        int percent = Utils.nextInt(100);
        Item item;
        int manhthiensuId = ItemName.MANH_AO_THIEN_SU_TD + Utils.nextInt(0, 14);
        if (percent < 80) {
            item = new Item(manhthiensuId);
        } else if (percent < 90) {
            item = new Item(random[Utils.nextInt(random.length)]);
        } else if (percent < 95) {
            item = new Item(ItemName.NHAN_THAN_LINH);
        } else {
            item = new Item(Gang[Utils.nextInt(Gang.length)]);
        }

        item.setDefaultOptions();
        item.quantity = 1;
        ItemMap itemMap = new ItemMap(zone.autoIncrease++);
        itemMap.item = item;
        itemMap.playerID = Math.abs(c.id);
        itemMap.x = getX();
        itemMap.y = zone.map.collisionLand(getX(), getY());
        zone.addItemMap(itemMap);
        zone.service.addItemMap(itemMap);

    }

    @Override
    public void startDie() {
        Zone z = zone;
        this.explode(Short.MAX_VALUE);
        super.startDie();
        if (!isSuper) {
            Utils.setTimeout(() -> {
                Cumber bl = new Cumber(true);
                bl.setLocation(z);
            }, 4000);
        } else {
            Utils.setTimeout(() -> {
                Cumber bl = new Cumber(false);
                bl.setLocation(19, -1);
            }, 30 * 60000);
        }
    }

    @Override
    public void explode(int distance) {
        try {
            if (zone != null) {
                long hpFull = info.hpFull;
                List<Player> list = zone.getListChar(Zone.TYPE_ALL);
                List<Player> filter = list.stream().filter(f -> {
                    if (f != null && f != this && isMeCanAttackOtherPlayer(f) && Utils.getDistance(this.getX(), this.getY(), f.getX(), f.getY()) < distance) {
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
                try {
                    for (Player _player : filter) {
                        _player.lock.lock();
                        try {
                            if (!_player.isDead()) {
                                long dame = hpFull;
                                dame += (dame * this.info.optionTuSat / 100);
                                if (_player.isBoss()) {
                                    if (isMonkey()) {
                                        dame /= 3;
                                    } else {
                                        dame /= 2;
                                    }
                                }
                                if (_player.limitDame > 0 && dame > _player.limitDame) {
                                    dame = _player.limitDame;
                                }
                                if (_player.isProtected()) {
                                    if (dame >= _player.info.hpFull) {
                                        _player.setTimeForItemtime(0, 0);
                                        _player.service.sendThongBao("Khiên năng lượng đã vỡ");
                                    }
                                    dame = 1;
                                }
                                _player.info.hp -= dame;
                                zone.service.attackPlayer(_player, dame, false, (byte) -1);
                                if (_player.info.hp <= 0) {
                                    kill(_player);
                                    _player.killed(this);
                                    _player.startDie();
                                }
                            }
                        } finally {
                            _player.lock.unlock();
                        }

                    }
                    List<Mob> list2 = zone.getListMob();
                    for (Mob mob : list2) {
                        mob.lock.lock();
                        try {
                            if (mob.isDead()) {
                                continue;
                            }
                            int d = Utils.getDistance(this.getX(), this.getY(), mob.x, mob.y);
                            if (d < distance) {
                                if (mob.templateId != 70) {
                                    mob.hp -= hpFull;
                                    zone.service.attackNpc(hpFull, false, mob, (byte) -1);
                                    if (mob.hp <= 0) {
                                        kill(mob);
                                        mob.startDie(hpFull, false, this);
                                    }
                                }
                            }
                        } finally {
                            mob.lock.unlock();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            setCharge(false);
            seconds = 0;
        }
    }

    @Override
    public void updateEveryFiveSeconds() {
        super.updateEveryFiveSeconds();
    }
}
