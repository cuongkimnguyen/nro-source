package com.ngocrong.bot.boss.fide;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.event.Event;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemOption;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class MapDauDinh extends Boss {

    private static final Logger logger = Logger.getLogger(MapDauDinh.class);

    public MapDauDinh() {
        super();
        this.distanceToAddToList = 500;
        this.limit = 500;
        this.name = "Mập Đầu Đinh";
        setInfo(10000000, 1000000, 20000, 1000, 50);
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
    public void killed(Object obj) {
        super.killed(obj);
        if (obj == null) {
            return;
        }
        Player killer = (Player) obj;
//        killer.pointBoss += 1;
//        killer.isChangePoint = true;
        if (killer.taskMain != null && killer.taskMain.id == 19 && killer.taskMain.index == 1) {
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
        int[] mapIDs = new int[]{64, 65, 63, 66, 67};
        super.startDie();
        Utils.setTimeout(() -> {
            MapDauDinh mapDauDinh = new MapDauDinh();
            mapDauDinh.setLocation(mapIDs[Utils.nextInt(mapIDs.length)], -1);
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
        setLeg((short) 167);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 166);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 165);
    }
}
