/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.NQMP.Whis;

import _HunrProvision.boss.Boss;
import com.ngocrong.skill.Skills;
import com.ngocrong.user.Player;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class WhisInSingleMap extends Boss {

    public int count;
    private static final Logger logger = Logger.getLogger(WhisInSingleMap.class);
    Player playerAttack;

    static {
    }

    public WhisInSingleMap(Player player) {
        super();
        this.limit = -1;
        this.name = "Whis";
        setInfo(2500000000L, 1000000, 500000, 10000, 10);
        setDefaultPart();
        setTypePK((byte) 5);
        this.isShow = false;
        playerAttack = player;
        playerAttack.isAttackWhis = true;
        playerAttack.info.setInfo();
        playerAttack.service.loadPoint();
        playerAttack.zone.service.playerLoadBody(playerAttack);
        playerAttack.healFull();
    }

    @Override
    public void initSkill() {
        try {
            skills = new ArrayList<>();
            skills.add(Skills.getSkill((byte) 1, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 5, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 3, (byte) 7).clone());
            skills.add(Skills.getSkill((byte) 4, (byte) 7).clone());

        } catch (Exception ex) {
            logger.error("init skill err");
        }
    }

    @Override
    public void killed(Object obj) {
        if (obj != null) {
            super.killed(obj);
            Player killer = (Player) obj;
            killer.currentLevelBossWhis++;
            killer.updateLevelWhis();
            playerAttack.isAttackWhis = false;
            playerAttack.info.setInfo();
            playerAttack.service.loadPoint();
            playerAttack.zone.service.playerLoadBody(playerAttack);
        }

    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 507);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 506);
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 505);
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
    }

    @Override
    public void sendNotificationWhenDead(String name) {
    }

    @Override
    public void throwItem(Object obj) {
    }
}
