/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.NQMP.DHVTSieuHang;

/**
 *
 */
import _HunrProvision.boss.Boss;
import com.ngocrong.user.Player;
import org.apache.log4j.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

public class CloneSieuHang extends Boss {

    private static final Logger logger = Logger.getLogger(CloneSieuHang.class);
    public int cloneRanking;
    public int challengerRanking;

    public CloneSieuHang() {
        super();
        this.distanceToAddToList = 10000;
        //limit = -1 thi la target bat chap khoang cach. Con neu co limit thi trong khoang do moi tan cong. Distance la khoang cach de boss cho vao danh sach co the tan cong
        this.limit = -1;
        this.name = "";
        this.waitingTimeToLeave = 0;
        this.willLeaveAtDeath = false;
        setTypePK((byte) 0);
    }

    @Override
    public void initSkill() {
        skills = new ArrayList<>();
    }

    @Override
    public void throwItem(Object obj) {
    }

    @Override
    public void killed(Object obj) {
        super.killed(obj);
        if (obj == null) {
            return;
        }
        Player pl = (Player) obj;
    }

    public boolean meCanMove() {
        return super.meCanMove() && typePk == 5;
    }

    @Override
    public void startDie() {
        super.startDie();
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
    }

    @Override
    public void sendNotificationWhenDead(String name) {
    }

    @Override
    public void setDefaultLeg() {
    }

    @Override
    public void setDefaultBody() {
    }

    @Override
    public void setDefaultHead() {
    }
}
