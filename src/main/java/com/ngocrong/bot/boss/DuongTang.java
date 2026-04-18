//package com.ngocrong.bot.boss;
//
//import com.ngocrong.bot.Escort;
//import com.ngocrong.map.MapManager;
//import com.ngocrong.user.Player;
//import com.ngocrong.util.Utils;
//
//public class DuongTang extends Escort {
//
//    public static int[] MAPS = {105, 106, 107, 108, 109, 110, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 79, 80, 81, 82, 83};
//
//    public DuongTang(Player _c) {
//        super(_c);
//        this.name = "Đường tăng";
//        setInfo(_c.info.hpFull, _c.info.mpFull, _c.info.damageFull, _c.info.defenseFull, _c.info.criticalFull);
//        int rd = Utils.nextInt(MAPS.length);
//        int mapID = MAPS[rd];
//        map = MapManager.getInstance().getMap(mapID);
//    }
//
//    @Override
//    public void setDefaultLeg() {
//        setLeg((short) 469);
//    }
//
//    @Override
//    public void setDefaultBody() {
//        setBody((short) 468);
//    }
//
//    @Override
//    public void setDefaultHead() {
//        setHead((short) 467);
//    }
//
//    @Override
//    public void startDie() {
//        escort.endEscort(0);
//        zone.leave(this);
//    }
//
//}
