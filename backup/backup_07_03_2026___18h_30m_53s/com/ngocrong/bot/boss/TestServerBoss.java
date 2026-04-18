/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.bot.boss;

import _HunrProvision.boss.Boss;
import com.ngocrong.mob.Mob;
import com.ngocrong.skill.Skill;
import com.ngocrong.skill.SkillName;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class TestServerBoss extends Boss {

    private static final Logger logger = Logger.getLogger(TestServerBoss.class);

    public TestServerBoss() {
        super();
        setInfo(10_000_000_000L, 100000000, 1, 100, 20);
        // this.limitDame = this.info.originalHP / 300;
        this.name = "Test Server Boss " + Utils.nextInt(100);
        setTypePK((byte) 5);
        this.limit = -1;
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();

            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_DEMON, (byte) 5).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_DRAGON, (byte) 5).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_GALICK, (byte) 5).clone());
            
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_KAMEJOKO, (byte) 5).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_MASENKO, (byte) 5).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_ANTOMIC, (byte) 5).clone());
            
           skills.add(Skills.getSkill((byte) SkillName.DE_TRUNG, (byte) 7).clone());

            skills.add(Skills.getSkill((byte) SkillName.KHIEN_NANG_LUONG, (byte) 7).clone());
        } catch (Exception ex) {
            
            logger.error("init skill err");
        }

    }

    @Override
    public void killed(Object obj) {

    }

    @Override
    public void sendNotificationWhenAppear(String map) {

    }

    @Override
    public void throwItem(Object obj) {

    }

    @Override
    public long injure(Player plAtt, Mob mob, long dameInput) {
        return 1;
    }

    @Override
    public void sendNotificationWhenDead(String name) {

    }

    @Override
    public void setDefaultHead() {
        setHead((short) 421);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 422);
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 423);
    }

    @Override
    public void startDie() {

    }

    @Override
    public Object targetDetect() {
        List<Player> enemiesCanAttack = this.zone.players;
        if (enemiesCanAttack.size() > 0) {
            Player target = randomChar(enemiesCanAttack);
            if (target != null) {
                return target;
            }
        }
        return null;
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

    @Override
    public void attack(Object obj) {
        long now = System.currentTimeMillis();
        if (now - lastTimeSkillShoot < 1000) {
            return;
        }
        Player target = (Player) obj;
        if (!target.isBoss()) {
            return;
        }
        Skill skill = selectSkillAttack();
        if (skill != null) {
            int d = Utils.getDistance(0, 0, skill.dx, skill.dy);
            if (skill.template.id == SkillName.CHIEU_KAMEJOKO || skill.template.id == SkillName.CHIEU_MASENKO
                    || skill.template.id == SkillName.CHIEU_ANTOMIC) {
                lastTimeSkillShoot = now;
            }
            this.select = skill;
            moveTo((short) (target.getX() + Utils.nextInt(-d, d)), target.getY());

            zone.attackPlayer(this, target);

        }
    }
}
