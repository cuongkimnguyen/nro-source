/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.bot.boss.BossDisciple;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.mob.Mob;
import com.ngocrong.server.DropRateService;
import com.ngocrong.server.SessionManager;
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
public class SuperMabu extends Boss {

    private static final Logger logger = Logger.getLogger(SuperMabu.class);

    public SuperMabu() {
        super();
        this.percentDame = 3;
        setInfo(1_000_000_000, 1000000000, 100000, 100, 20);
        // this.limitDame = this.info.originalHP / 300;
        this.name = "Super Mabu";
        setTypePK((byte) 5);
        this.limit = -1;
        this.waitingTimeToLeave = 0;
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();

            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_DEMON, (byte) 5).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_DRAGON, (byte) 5).clone());
            skills.add(Skills.getSkill((byte) SkillName.CHIEU_DAM_GALICK, (byte) 5).clone());

            skills.add(Skills.getSkill((byte) SkillName.CHIEU_KAMEJOKO, (byte) 5).clone());

            skills.add(Skills.getSkill((byte) SkillName.KHIEN_NANG_LUONG, (byte) 7).clone());
        } catch (Exception ex) {
            
            logger.error("init skill err");
        }

    }

    @Override
    public void killed(Object obj) {
        Player player = (Player) obj;
        if (player == null) {
            return;
        }

        // Capture the zone reference before the async operation
        final Zone currentZone = this.zone;
        if (currentZone == null) {
            return; // Safety check
        }

        Item quatrung = new Item(568);
        quatrung.quantity = 1;
        dropItem(quatrung, player);
        quatrung = new Item(568);
        quatrung.quantity = 1;
        dropItem(quatrung, player);

        Utils.setTimeout(() -> {
            // Use the captured reference instead of this.zone
            int[] arrayX = new int[]{24, currentZone.map.width / 2, currentZone.map.width - 80};
            for (int i = 0; i < 3; i++) {
                var quatrung2 = new Item(568);
                quatrung2.quantity = 1;
                ItemMap itemMap = new ItemMap(currentZone.autoIncrease++);
                itemMap.item = quatrung2;
                itemMap.playerID = -1;
                itemMap.x = (short) arrayX[i];
                itemMap.y = currentZone.map.collisionLand(itemMap.x, getY());
                currentZone.addItemMap(itemMap);
                currentZone.service.addItemMap(itemMap);
            }
        }, 10000);
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
       super.sendNotificationWhenAppear(map);
    }

    @Override
    public void throwItem(Object obj) {

    }

    @Override
    public long injure(Player plAtt, Mob mob, long dameInput) {
        if (plAtt != null && plAtt.myDisciple == null) {
            plAtt.service.sendThongBao("Bạn cần phải có đệ tử trước tiên");
            return 0;
        }
//        if (plAtt.myDisciple.typeDisciple != 1 && plAtt.myDisciple.typeDisciple != 2) {
//            plAtt.service.sendThongBao("Bạn cần phải có đệ tử Mabu trước tiên");
//            return 0;
//        }

        return Math.min(dameInput, 20_000_000);
    }

    @Override
    public void sendNotificationWhenDead(String name) {
        super.sendNotificationWhenDead(name);
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
        super.startDie();
        Utils.setTimeout(() -> {
            SuperMabu bl = new SuperMabu();
            bl.setLocation(52, -1);
        }, 15 * 60000);

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
