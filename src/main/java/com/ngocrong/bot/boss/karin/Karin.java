package com.ngocrong.bot.boss.karin;

import _HunrProvision.boss.Boss;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Karin extends Boss {

    private static Logger logger = Logger.getLogger(Karin.class);

    private boolean isTrainning;

    public Karin(boolean isTrainning) {
        super();
        this.isShow = false;
        this.isTrainning = isTrainning;
        this.limit = 400;
        this.name = "Karin";
        this.sayTheLastWordBeforeDie = "Ngươi khá lắm";
        setInfo(500, 20000, 20, 3, 5);
        Utils.setTimeout(() -> {
            setTypePK((byte) 5);
        }, 3000);
        point = 0;
    }

    public void move() {
        if (!meCanMove()) {
            return;
        }
        setX((short) Utils.nextInt(225, 490));
        setY((short) 408);
        zone.service.move(this);
    }

    @Override
    public void setLocation(Zone zone) {
        zone.service.npcHide((byte) 18, true);
        super.setLocation(zone);
        chat("Ta sẽ đánh hết sức, ngươi cẩn thận nhé");
    }

    @Override
    public void kill(Object obj) {
        chat("Ngươi hãy tập luyện thêm");
        Utils.setTimeout(() -> {
            zone.service.npcHide((byte) 18, false);
            zone.leave(this);
        }, waitingTimeToLeave);
    }

    @Override
    public void killed(Object obj) {
        Player killer = (Player) obj;
        if (!isTrainning) {
            if (killer.taskMain.id == 10 && killer.taskMain.index == 0) {
                killer.taskNext();
            }
            killer.setTypeTranning((byte) 1);
        }
    }

    @Override
    public void startDie() {
        Zone z = zone;
        Utils.setTimeout(() -> {
            z.service.npcHide((byte) 18, false);
        }, waitingTimeToLeave);
        super.startDie();

    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList();
            Skill skill;
            skill = Skills.getSkill((byte) 0, (byte) 1).clone();
            skills.add(skill);
            skill = Skills.getSkill((byte) 1, (byte) 1).clone();
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
    public void setDefaultHead() {
        setHead((short) 89);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 90);
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 91);
    }
}
