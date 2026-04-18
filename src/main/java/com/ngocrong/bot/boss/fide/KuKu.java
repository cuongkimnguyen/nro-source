package com.ngocrong.bot.boss.fide;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class KuKu extends Boss {

    private static final Logger logger = Logger.getLogger(KuKu.class);

    public KuKu() {
        super();
        this.distanceToAddToList = 500;
        this.limit = 500;
        this.name = "KuKu";
        setInfo(5000000, 1000000, 20000, 1000, 50);
        this.waitingTimeToLeave = 0;
        setTypePK((byte) 5);
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
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }

//        dropGroupA((Player) obj);
    }

    @Override
    public void killed(Object obj) {
        super.killed(obj);
        if (obj == null) {
            return;
        }
        Player killer = (Player) obj;
//        killer.pointBoss +=1;
//        killer.isChangePoint = true;
        if (killer.taskMain != null && killer.taskMain.id == 19 && killer.taskMain.index == 0) {           
            killer.updateTaskCount(1);
        }
    }

    @Override
    public void startDie() {
        int[] mapIDs = new int[]{68, 69, 70, 71, 72};
        super.startDie();
        Utils.setTimeout(() -> {
            KuKu kuku = new KuKu();
            kuku.setLocation(mapIDs[Utils.nextInt(mapIDs.length)], -1);
        }, 30000);
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
        setLeg((short) 161);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 160);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 159);
    }
}
