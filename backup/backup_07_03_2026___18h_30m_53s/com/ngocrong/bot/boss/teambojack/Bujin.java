package com.ngocrong.bot.boss.teambojack;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.event.Event;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemOption;
import com.ngocrong.map.GalaxySoldier;
import com.ngocrong.model.RandomItem;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.SkillName;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Bujin extends TeamBojack {

    private static Logger logger = Logger.getLogger(Bujin.class);

    private GalaxySoldier team;

    public Bujin(GalaxySoldier team) {
        super();
        this.team = team;
        this.distanceToAddToList = 1000;
        this.limit = 1000;
        this.name = "Bujin";
        setInfo(50000000, 1000000, 10000, 100, 5);
        this.willLeaveAtDeath = false;
    }

//    @Override
//    public void initSkill() {
//        try {
//            skills = new ArrayList<>();
//            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_DRAGON, (byte) 7).clone());
//            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_DEMON, (byte) 7).clone());
//            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_GALICK, (byte) 7).clone());
//            skills.add(Skills.getSkill((byte) SkillName.CHIEU_KAMEJOKO, (byte) 7).clone());
//            skills.add(Skills.getSkill((byte) SkillName.CHIEU_MASENKO, (byte) 7).clone());
//            skills.add(Skills.getSkill((byte) SkillName.CHIEU_ANTOMIC, (byte) 7).clone());
//            skills.add(Skills.getSkill((byte) SkillName.KHIEN_NANG_LUONG, (byte) 7).clone());
//            skills.add(Skills.getSkill((byte) SkillName.TROI, (byte) 7).clone());
//        } catch (Exception ex) {
//            
//            logger.error("init skill err");
//        }
//    }

//    @Override
//    public void startDie() {
//        try {
//            super.startDie();
//        } finally {
//            team.next((byte) 2);
//            zone.leave(this);
//        }
//    }

    @Override
    public void sendNotificationWhenAppear(String map) {
        SessionManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s", this.name, map));
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 343);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 342);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 341);
    }

}
