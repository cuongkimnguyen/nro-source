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
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.SkillName;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class SuperBojack extends TeamBojack {

    private static final Logger logger = Logger.getLogger(SuperBojack.class);

    private GalaxySoldier team;
    private byte type;

    public SuperBojack(GalaxySoldier team, byte type) {
        super();
        this.type = type;
        this.team = team;
        this.distanceToAddToList = 1000;
        this.limit = 1000;
        this.name = "Super Bojack";
        setInfo(100000000, 1000000, 10000, 100, 5);
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
//        } catch (Exception ex) {
//            
//            logger.error("init skill err");
//        }
//    }
    @Override
    public void sendNotificationWhenAppear(String map) {
        SessionManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s", this.name, map));
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 328);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 327);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 326);
    }

//    @Override
//    public void startDie() {
//        try {
//            super.startDie();
//        } finally {
//            if (type == 0) {
//                team.next((byte) 1);
//            }
//            if (type == 1) {
//                team.next((byte) 3);
//            }
//            zone.leave(this);
//        }
//    }

    @Override
    public void attack(Object obj) {
        long now = System.currentTimeMillis();
        if (now - lastTimeSkillShoot < 1000) {
            return;
        }
        Player target = (Player) obj;
        Skill skill = selectSkillAttack();
        if (skill != null) {
            int d = Utils.getDistance(0, 0, skill.dx, skill.dy);
            if (skill.template.id == SkillName.CHIEU_KAMEJOKO || skill.template.id == SkillName.CHIEU_MASENKO || skill.template.id == SkillName.CHIEU_ANTOMIC) {
                lastTimeSkillShoot = now;
            }
            this.select = skill;
            moveTo((short) (target.getX() + Utils.nextInt(-d, d)), target.getY());
            zone.attackPlayer(this, target);
        }
    }
    
    @Override
    public void updateEveryHalfSeconds() {
        super.updateEveryHalfSeconds();
        if (!isDead()) {
            if (isAttack() && meCanAttack()) {
                if (!isRecoveryEnergy() && !isCharge()) {
                    Object target = targetDetect();
                    if (target != null) {
                        attack(target);
                    }
                    useSkillNotFocus();
                }
            }
        }
    }
}
