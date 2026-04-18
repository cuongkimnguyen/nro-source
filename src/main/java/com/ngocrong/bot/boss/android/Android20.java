package com.ngocrong.bot.boss.android;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.event.Event;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemOption;
import com.ngocrong.map.TeamAndroid19;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.SkillName;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Android20 extends Boss {

    private final static Logger logger = Logger.getLogger(Android20.class);

    private final TeamAndroid19 team;

    public Android20(TeamAndroid19 team) {
        super();
        this.team = team;
        this.distanceToAddToList = 1000;
        this.limit = 1000;
        this.name = "Android 20";
        setInfo(30000000, 1000000, 10000, 100, 5);
        this.willLeaveAtDeath = false;
        point = 3;
        canDispose = true;
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_DRAGON, (byte) 5).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_DEMON, (byte) 5).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_GALICK, (byte) 5).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_KAMEJOKO, (byte) 5).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_MASENKO, (byte) 5).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_ANTOMIC, (byte) 5).clone());
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
//         killer.pointBoss += 2;
//        killer.isChangePoint = true;
        if (killer.taskMain != null && killer.taskMain.id == 22 && killer.taskMain.index == 3) {
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
        try {
            super.startDie();
        } finally {
            team.next(this);
            zone.leave(this);
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
        setLeg((short) 257);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 256);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 255);
    }
}
