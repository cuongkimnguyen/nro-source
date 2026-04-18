package com.ngocrong.bot.boss.fide;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Objects;

public class Fide extends Boss {

    private static final Logger logger = Logger.getLogger(Fide.class);

    private final byte level;

    public Fide(byte level) {
        super();
        this.level = level;
        this.distanceToAddToList = 500;
        this.limit = -1;
        if (this.level == 0) {
            this.name = "Fide Đại Ca 1";
            setInfo(40000000, 1000000, 15000, 1000, 50);
        } else if (this.level == 1) {
            this.name = "Fide Đại Ca 2";
            setInfo(45000000, 1000000, 20000, 1000, 50);
        } else {
            this.name = "Fide Đại Ca 3";
            setInfo(50000000, 1000000, 20000, 1000, 50);
        }
        this.waitingTimeToLeave = 0;
        setTypePK((byte) 5);
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            skills.add(Objects.requireNonNull(Skills.getSkill((byte) 0, (byte) 7)).clone());
            skills.add(Objects.requireNonNull(Skills.getSkill((byte) 1, (byte) 7)).clone());
            skills.add(Objects.requireNonNull(Skills.getSkill((byte) 2, (byte) 7)).clone());
            skills.add(Objects.requireNonNull(Skills.getSkill((byte) 3, (byte) 7)).clone());
            skills.add(Objects.requireNonNull(Skills.getSkill((byte) 4, (byte) 7)).clone());
            skills.add(Objects.requireNonNull(Skills.getSkill((byte) 5, (byte) 7)).clone());
        } catch (CloneNotSupportedException ex) {
            
            logger.error("init skill");
        }
    }

    @Override
    public void killed(Object obj) {
        super.killed(obj);
        if (obj == null) {
            return;
        }
        Player killer = (Player) obj;
        if (level == 0 && killer.taskMain != null && killer.taskMain.id == 21 && killer.taskMain.index == 1) {
            killer.updateTaskCount(1);
        }
        if (level == 1 && killer.taskMain != null && killer.taskMain.id == 21 && killer.taskMain.index == 2) {
            killer.updateTaskCount(1);
        }
        if (level == 2 && killer.taskMain != null && killer.taskMain.id == 21 && killer.taskMain.index == 3) {
            killer.updateTaskCount(1);
        }
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
//        dropGroupA((Player) obj);
    }

    @Override
    public void startDie() {
        Zone z = zone;
        super.startDie();
        if (level == 0) {
            Fide fide = new Fide((byte) 1);
            fide.setLocation(z);
        } else if (level == 1) {
            Fide fide = new Fide((byte) 2);
            fide.setLocation(z);
        } else {
            Utils.setTimeout(() -> {
                Fide fide = new Fide((byte) 0);
                fide.setLocation(80, -1);
            }, 10 * 60000);
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
    public void setDefaultLeg() {
        if (this.level == 0) {
            setLeg((short) 185);
        } else if (this.level == 1) {
            setLeg((short) 188);
        } else {
            setLeg((short) 191);
        }
    }

    @Override
    public void setDefaultBody() {
        if (this.level == 0) {
            setBody((short) 184);
        } else if (this.level == 1) {
            setBody((short) 187);
        } else {
            setBody((short) 190);
        }
    }

    @Override
    public void setDefaultHead() {
        if (this.level == 0) {
            setHead((short) 183);
        } else if (this.level == 1) {
            setHead((short) 186);
        } else {
            setHead((short) 189);
        }
    }
}
