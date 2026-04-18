package com.ngocrong.bot.boss.karin;

import _HunrProvision.boss.Boss;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class TaoPaiPai extends Boss {

    private static Logger logger = Logger.getLogger(TaoPaiPai.class);

    public TaoPaiPai() {
        super();
        this.limit = -1;
        this.isShow = false;
        this.name = "Tàu Pảy Pảy";
        this.sayTheLastWordBeforeDie = "Ngươi hãy chờ đấy";
        Utils.setTimeout(() -> {
            setTypePK((byte) 5);
        }, 5000);
        point = 0;
    }

    public void initSkill() {
        try {
            skills = new ArrayList();
            Skill skill = Skills.getSkill((byte) 0, (byte) 7).clone();
            skills.add(skill);
        } catch (CloneNotSupportedException ex) {
            
            logger.error("init skill err", ex);
        }
    }

    public void killed(Object obj) {
        Player killer = (Player) obj;
        if (killer.taskMain.id == 10 && killer.taskMain.index == 1) {
            killer.taskNext();
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
        setLeg((short) 94);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 93);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 92);
    }
}
