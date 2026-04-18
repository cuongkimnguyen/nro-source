package com.ngocrong.bot.boss.teamhaitac;

import _HunrProvision.boss.Boss;
import com.ngocrong.map.HaiTacManager;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.SkillName;
import com.ngocrong.skill.Skills;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Usopp extends TeamHaiTac {

    private static final Logger logger = Logger.getLogger(Usopp.class);

    private final HaiTacManager team;

    public Usopp(HaiTacManager team) {
        super();
        this.team = team;
        this.distanceToAddToList = 1000;
        this.limit = 1000;
        this.name = "Usopp";
        setInfo(15000000, 900000, 10000, 100, 5);
        this.willLeaveAtDeath = false;
    }

    @Override
    public void startDie() {
        try {
            super.startDie();
        } finally {
            team.checkBossDeath();
            zone.leave(this);
        }
    }

    @Override
    public void sendNotificationWhenDead(String name) {
        SessionManager.chatVip(String.format("%s đã đánh bại %s và nhận được phần thưởng!", name, this.name));
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 599);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 598);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 597);
    }
}
