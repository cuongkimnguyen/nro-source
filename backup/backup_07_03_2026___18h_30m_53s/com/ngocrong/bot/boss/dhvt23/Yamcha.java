package com.ngocrong.bot.boss.dhvt23;

import _HunrProvision.boss.Boss;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class Yamcha extends BossDHVT {

    public Yamcha(Player plAtt) {
        super();
        canAttack = false;
        this.plAtt = plAtt;
        this.limit = -1;
        this.name = "Yamcha";
        setInfo(400000000, 20000, 15000, 3, 5);
        setLocation(plAtt.zone);
        Utils.setTimeout(() -> {
            canAttack = true;
        }, 14500);
        super.setDame(10, plAtt);
    }
    private static Logger logger = Logger.getLogger(SoiHecQuyn.class);
    boolean next = false;

    @Override
    public void startDie() {
        next = true;
        super.startDie();
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList();
            Skill skill;
            skill = Skills.getSkill((byte) 0, (byte) 1).clone();
            skills.add(skill);
        } catch (CloneNotSupportedException ex) {
            
            logger.error("init skill err", ex);
        }
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
    }

    @Override
    public void sendNotificationWhenDead(String name) {
    }

    @Override
    public void setDefaultLeg() {
        this.setLeg((short) 376);
    }

    @Override
    public void setDefaultBody() {
        this.setBody((short) 375);
    }

    @Override
    public void setDefaultHead() {
        this.setHead((short) 374);
    }

}
