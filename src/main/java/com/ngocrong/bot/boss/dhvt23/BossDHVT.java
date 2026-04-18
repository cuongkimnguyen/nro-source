/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.bot.boss.dhvt23;

import _HunrProvision.boss.Boss;
import com.ngocrong.mob.Mob;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;

/**
 *
 * @author Administrator
 */
public class BossDHVT extends Boss {

    public BossDHVT() {
        super();
        info.percentMiss = 20;
        this.isShow = false;
    }
    public int percentDef = 0;
    public Player plAtt = null;
    public boolean canAttack;

    @Override
    public void updateEveryHalfSeconds() {
        super.updateEveryHalfSeconds();
        if (!isDead()) {
            if (isAttack() && meCanAttack()) {
                if (!isRecoveryEnergy() && !isCharge()) {
                    if (plAtt != null && plAtt.info != null) {
                        if (plAtt.info.hp == 0) {
                            plAtt = null;
                        }
                        if (plAtt != null && canAttack && this.zone != null && plAtt.zone != null) {
                            attack(plAtt);
                        }
                    }
                    useSkillNotFocus();
                }
            }
        }
    }

    @Override
    public long injure(Player plAtt, Mob mob, long dameInput) {
        if (dameInput <= 0) {
            return -1;
        }
        dameInput -= Utils.percentOf(dameInput, percentDef);
        return dameInput;
    }

    public long getDameAttack(Player target) {
        long dame = this.info.originalDamage;
        long dame2 = (target.info.hpFull * percentDame) / 100;
        if (target.isProtected()) {
            return -1;
        }
        return Math.max(dame, dame2);
    }

    public void setDame(int percentDame, Player player) {
        this.percentDame = (byte) percentDame;
        this.waitingTimeToLeave = 0;
    }

    @Override
    public void killed(Object obj) {
        super.killed(obj);
        if (obj == null) {
            return;
        }
        Player killer = (Player) obj;
        killer.roundDHVT23++;
    }

    @Override
    public void initSkill() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void sendNotificationWhenDead(String name) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setDefaultLeg() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setDefaultBody() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setDefaultHead() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    public boolean isDie;

    @Override
    public void startDie() {
        isDie = true;
        super.startDie();
    }
}
