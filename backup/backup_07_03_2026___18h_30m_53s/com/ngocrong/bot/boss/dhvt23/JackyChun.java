package com.ngocrong.bot.boss.dhvt23;

import _HunrProvision.boss.Boss;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class JackyChun extends BossDHVT {

    public JackyChun(Player plAtt) {
        super();
        canAttack = false;
        this.plAtt = plAtt;
        this.limit = -1;
        this.name = "JackyChun";
        setInfo(450000000, 20000, 50000, 3, 5);
        setLocation(plAtt.zone);
        Utils.setTimeout(() -> {
            canAttack = true;
        }, 14500);
        super.setDame(10, plAtt);
        percentDef = 30;
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
        this.setLeg((short) 358);
    }

    @Override
    public void setDefaultBody() {
        this.setBody((short) 357);
    }

    @Override
    public void setDefaultHead() {
        this.setHead((short) 356);
    }

}
