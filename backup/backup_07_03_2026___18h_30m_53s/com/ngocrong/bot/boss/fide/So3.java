package com.ngocrong.bot.boss.fide;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.map.GinyuForce;
import com.ngocrong.server.SessionManager;
import com.ngocrong.skill.SkillName;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;
import com.ngocrong.mob.Mob;
import java.util.ArrayList;

public class So3 extends Boss {

    private static final Logger logger = Logger.getLogger(So3.class);

    private final GinyuForce team;

    public So3(GinyuForce team) {
        super();
        this.team = team;
        this.distanceToAddToList = 1000;
        this.limit = 1000;
        this.name = "Số 3";
        setInfo(15000000, 1000000, 10000, 100, 5);
        this.willLeaveAtDeath = false;
        if (team.getType() == 0) {
            setInfo(50000000, 1000000, 10000, 100, 5);
            this.percentDame = 35;
             canReactDame = false;
        }
    }

    @Override
    public long injure(Player plAtt, Mob mob, long dameInput) {
        if (team.getType() == 0) {
            return Math.min(500000, dameInput);
        }
        return dameInput;

    }

    @Override
    public void killed(Object obj) {
        super.killed(obj);
        if (obj == null) {
            return;
        }
        Player killer = (Player) obj;
//        killer.pointBoss += 1;
//        killer.isChangePoint = true;
        if (killer.taskMain != null && killer.taskMain.id == 20 && killer.taskMain.index == 2
                && killer.zone.map.mapID > 70) {
            killer.updateTaskCount(1);
        }
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_DRAGON, (byte) 4).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_DEMON, (byte) 4).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_GALICK, (byte) 4).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_KAMEJOKO, (byte) 4).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_MASENKO, (byte) 4).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_ANTOMIC, (byte) 4).clone());
        } catch (Exception ex) {
            
            logger.error("init skill err");
        }
    }

    public boolean meCanMove() {
        return super.meCanMove() && typePk == 5;
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        Player c = (Player) obj;
        //dropGroupA(c);
        if (team.getType() == 0) {
            for (int i = 0; i < 5; i++) {
                Item item = new Item(ItemName.THOI_VANG);
                item.setDefaultOptions();
                item.quantity = 1;
                dropItem(item, null);
            }
        }
    }

    @Override
    public void startDie() {
        try {
            super.startDie();
        } finally {
            zone.leave(this);
            if (team.getType() == 0) {
                Utils.setTimeout(() -> {
                    this.wakeUpFromDead();
                    team.spawnSolo(this);
                    this.setTypePK((byte) 5);
                }, 30 * 60000L);
            } else {
                team.next(this);
            }
        }
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
        SessionManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s", this.name, map));
        logger.debug(String.format("BOSS %s vừa xuất hiện tại %s khu vực %d", this.name, map, zone.zoneID));
    }

    @Override
    public void sendNotificationWhenDead(String name) {
        // if (team != null && team.type == 0) {
        // PlayerManager.chatVip(String.format("%s: Đã đánh bại và nhận được cải trang
        // thành Số 3", name));
        // } else {
        SessionManager.chatVip(String.format("%s: Đã tiêu diệt được %s mọi người đều ngưỡng mộ.", name, this.name));
        // }
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 176);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 175);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 174);
    }

}
