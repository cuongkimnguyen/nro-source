package com.ngocrong.bot.boss.karin;

import _HunrProvision.boss.Boss;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Yajiro extends Boss {

    private static Logger logger = Logger.getLogger(Yajiro.class);

    private boolean isTrainning;

    public Yajiro() {
        super();
        this.willLeaveAtDeath = false;
        this.limit = 400;
        this.name = "Yajiro";
        this.isShow = false;
        this.sayTheLastWordBeforeDie = "Ngươi khá lắm";
        setInfo(1000, 20000, 50, 5, 5);
        point = 0;
    }

    public void setTrainning(boolean isTrainning) {
        this.isTrainning = isTrainning;
    }

    public void move() {
        if (typePk == 0) {
            return;
        }
        if (!meCanMove()) {
            return;
        }
        setX((short) Utils.nextInt(225, 490));
        setY((short) 408);
        zone.service.move(this);
    }

    @Override
    public void setLocation(Zone zone) {
        super.setLocation(zone);
        chat("Ta sẽ đánh hết sức, ngươi cẩn thận nhé");
    }

    @Override
    public void kill(Object obj) {
        chat("Ngươi hãy tập luyện thêm");
        Utils.setTimeout(() -> {
            setX((short) 298);
            setY((short) 408);
            revival(100);
        }, 3000);
    }

    @Override
    public void killed(Object obj) {
        Player killer = (Player) obj;
        clearPk();
        killer.clearPk();
        if (!isTrainning) {
            if (killer.taskMain.id == 10 && killer.taskMain.index == 0) {
                killer.taskNext();
            }
            killer.setTypeTranning((byte) 2);
        }
    }

    @Override
    public void startDie() {
        Zone z = this.zone;
        super.startDie();
        Utils.setTimeout(() -> {
            setX((short) 298);
            setY((short) 408);
            revival(100);
        }, 3000);
    }

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
        setHead((short) 77);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 78);
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 79);
    }
}
