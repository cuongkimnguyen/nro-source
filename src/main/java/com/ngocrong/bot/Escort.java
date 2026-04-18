//package com.ngocrong.bot;
//
//import com.ngocrong.map.TMap;
//import com.ngocrong.network.Service;
//import com.ngocrong.user.Player;
//import com.ngocrong.user.Info;
//import com.ngocrong.util.Utils;
//
//import java.util.ArrayList;
//
//public class Escort extends Player {
//
//    public Player escort;
//    public TMap map;
//
//    public Escort(Player _c) {
//        this.id = -(((Utils.nextInt(100) * 1000) + Utils.nextInt(100) * 100)) + Utils.nextInt(100);
//        this.escort = _c;
//        info = new Info(this);
//        info.setPowerLimited();
//        info.setStamina();
//        info.setInfo();
//        info.recovery(Info.ALL, 100, false);
//        service = new Service(this);
//        effects = new ArrayList();
//        itemTimes = new ArrayList();
//        idMount = -1;
//        setDefaultPart();
//    }
//
//    public void setInfo(long hp, long mp, long dame, int def, int crit) {
//        info.originalHP = hp;
//        info.originalMP = mp;
//        info.originalDamage = dame;
//        info.originalDefense = def;
//        info.originalCritical = crit;
//        info.setInfo();
//        info.recovery(Info.ALL, 100, false);
//    }
//
//    public void move() {
//        short x = (short) (escort.getX() + Utils.nextInt(-50, 50));
//        short y = escort.getY();
//        moveTo(x, y);
//    }
//
//    public void moveTo(short x, short y) {
//        setX(x);
//        setY(y);
//        if (zone != null) {
//            zone.service.move(this);
//        }
//    }
//
//    @Override
//    public boolean isBoss() {
//        return false;
//    }
//
//    @Override
//    public boolean isHuman() {
//        return false;
//    }
//
//    @Override
//    public boolean isPet() {
//        return false;
//    }
//
//    @Override
//    public boolean isMiniPet() {
//        return false;
//    }
//
//    @Override
//    public boolean isEscort() {
//        return true;
//    }
//
//    @Override
//    public void updateEveryFiveSeconds() {
//        move();
//    }
//}
