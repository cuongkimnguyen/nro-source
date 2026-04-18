package com.ngocrong.bot.boss.Cell;

import _HunrProvision.boss.Boss;
import com.ngocrong.bot.boss.fide.Fide;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.model.RandomItem;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class XenBoHung extends Boss {

    private static final Logger logger = Logger.getLogger(Fide.class);

    private final byte level;

    public XenBoHung(byte level) {
        super();
        this.level = level;
        this.distanceToAddToList = 500;
        this.limit = -1;
        if (this.level == 0) {
            this.name = "Xên Bọ Hung";
            setInfo(40000000, 1000000, 15000, 1000, 50);
        } else if (this.level == 1) {
            this.name = "Xên Bọ Hung 2";
            setInfo(45000000, 1000000, 20000, 1000, 50);
        } else {
            this.name = "Xên Hoàn Thiện";
            setInfo(50000000, 1000000, 20000, 1000, 50);
        }
        this.waitingTimeToLeave = 0;
        setTypePK((byte) 5);
        point = 4;
        canDispose = true;
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            skills.add(Skills.getSkill((byte) 0, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 1, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 2, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 3, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 4, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 5, (byte) 7).clone());
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
        if (level == 0 && killer.taskMain != null && killer.taskMain.id == 25 && killer.taskMain.index == 1) {
            killer.updateTaskCount(1);
        }
        if (level == 1 && killer.taskMain != null && killer.taskMain.id == 25 && killer.taskMain.index == 2) {
            killer.updateTaskCount(1);
        }
        if (level == 2 && killer.taskMain != null && killer.taskMain.id == 25 && killer.taskMain.index == 3) {
            killer.updateTaskCount(1);
        }
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        var player = (Player) obj;
        if (level <= 1) {
            dropGroupB((Player) obj);
        } else {
            int percent = Utils.nextInt(100);
            Item item = null;
            if (percent <= 30) {
                item = new Item(ItemName.NGOC_RONG_3_SAO);
                item.quantity = 1;
            } else if (percent <= 80) {
                item = new Item(ItemName.VANG_190);
                item.quantity = 10_000_000;
            } else {
                item = new Item(RandomItem.DO_CUOI.next());
                item.setDefaultOptions();
                item.addRandomOption(1, 5);
                item.quantity = 1;
            }
            dropItem(item, player);
        }
    }

    @Override
    public void startDie() {
        Zone z = zone;
        super.startDie();
        if (level == 0) {
            XenBoHung xenBoHung = new XenBoHung((byte) 1);
            xenBoHung.setLocation(z);
        } else if (level == 1) {
            XenBoHung xenBoHung = new XenBoHung((byte) 2);
            xenBoHung.setLocation(z);
        } else {
            Utils.setTimeout(() -> {
                XenBoHung xenBoHung = new XenBoHung((byte) 0);
                xenBoHung.setLocation(100, -1);
            }, 15 * 60000);
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
            setLeg((short) 230);
        } else if (this.level == 1) {
            setLeg((short) 233);
        } else {
            setLeg((short) 236);
        }
    }

    @Override
    public void setDefaultBody() {
        if (this.level == 0) {
            setBody((short) 229);
        } else if (this.level == 1) {
            setBody((short) 232);
        } else {
            setBody((short) 235);
        }
    }

    @Override
    public void setDefaultHead() {
        if (this.level == 0) {
            setHead((short) 228);
        } else if (this.level == 1) {
            setHead((short) 231);
        } else {
            setHead((short) 234);
        }
    }
}
