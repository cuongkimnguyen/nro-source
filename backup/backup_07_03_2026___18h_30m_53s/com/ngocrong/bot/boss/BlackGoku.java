package com.ngocrong.bot.boss;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.MapName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.lib.RandomCollection;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.server.Config;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class BlackGoku extends Boss {

    private static final Logger logger = Logger.getLogger(BlackGoku.class);

    private final boolean isSuper;

    public static final int[] MAPS = new int[]{92, 93, 94, 96, 97, 98, 99, 100, 102, 103};
    public static int count;

    static {
    }

    public BlackGoku(boolean isSuper) {
        super();
        this.isSuper = isSuper;
        this.limit = -1;
        if (isSuper) {
            this.name = "Super Black Goku " + Utils.nextInt(100);
            setInfo(2_000_000_000L, 1000000, 500000, 1000, 10);
            count++;
        } else {
            this.name = "Black Goku " + Utils.nextInt(100);
            setInfo(2_000_000_000L, 1000000, 1000000, 1000, 10);
        }
        if (Config.serverID() == 2) {
            if (isSuper) {
                setInfo(1_000_000_000L, 1000000, 500000, 1000, 10);
            } else {
                setInfo(1_000_000_000L, 1000000, 1000000, 1000, 10);
            }
        }
        setDefaultPart();
        setTypePK((byte) 5);
        point = 5;
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            skills.add(Skills.getSkill((byte) 1, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 5, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 3, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 4, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 19, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 8, (byte) 7).clone());
        } catch (Exception ex) {
            
            logger.error("init skill err");
        }
    }

    @Override
    public void killed(Object obj) {
        super.killed(obj);
        if (obj == null) {
            return;
        }

        Player killer = (Player) obj;
//        killer.pointBoss += 3;
        killer.isChangePoint = true;
        if (!isSuper && killer.taskMain != null && killer.taskMain.id == 29 && killer.taskMain.index == 0) {
            killer.updateTaskCount(1);
        }
        if (isSuper && killer.taskMain != null && killer.taskMain.id == 29 && killer.taskMain.index == 1) {
            killer.updateTaskCount(1);
        }
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 552);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 551);
    }

    @Override
    public void setDefaultHead() {
        if (isSuper) {
            setHead((short) 553);
        } else {
            setHead((short) 550);
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
        if (Utils.isTrue(1, 10)) {
            Item item = new Item(992);
            item.setDefaultOptions();
            item.quantity = 1;
            dropItem(item, (Player) obj);
        }
        dropGroupC((Player) obj);

    }

    @Override
    public void startDie() {
        super.startDie();
        if (!isSuper) {
            BlackGoku bl = new BlackGoku(true);
            //bl.setLocation(z);
            bl.setLocation(MAPS[Utils.nextInt(MAPS.length)], -1);
        } else {
            Utils.setTimeout(() -> {
                BlackGoku bl = new BlackGoku(false);
                bl.setLocation(MAPS[Utils.nextInt(MAPS.length)], -1);
            }, 10 * 60000);
        }
    }

    @Override
    public void updateEveryFiveSeconds() {
        super.updateEveryFiveSeconds();
    }
}
