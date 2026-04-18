package com.ngocrong.bot.boss.dhvt23;

import _HunrProvision.boss.Boss;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class PonPut extends BossDHVT {

    public PonPut(Player plAtt) {
        super();
        canAttack = false;
        this.plAtt = plAtt;
        this.limit = -1;
        this.name = "PonPut";
        setInfo(250000000, 20000, 30000, 3, 5);
        setLocation(plAtt.zone);
        Utils.setTimeout(() -> {
            canAttack = true;
        }, 14500);
        super.setDame(5, plAtt);
    }
    private static Logger logger = Logger.getLogger(SoiHecQuyn.class);

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
        this.setLeg((short) 367);
    }

    @Override
    public void setDefaultBody() {
        this.setBody((short) 366);
    }

    @Override
    public void setDefaultHead() {
        this.setHead((short) 365);
    }
}
