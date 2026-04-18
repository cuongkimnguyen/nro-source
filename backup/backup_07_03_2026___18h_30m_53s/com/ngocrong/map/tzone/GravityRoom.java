package com.ngocrong.map.tzone;

import com.ngocrong.user.Player;
import com.ngocrong.map.TMap;

public class GravityRoom extends SpaceshipRoom {

    public GravityRoom(TMap map, int zoneId) {
        super(map, zoneId);
    }

    @Override
    public void enter(Player p) {
        super.enter(p);
        if (p.isHuman()) {
            p.info.setInfo();
            p.service.serverMessage("Đây là không gian cao trọng lục, hãy cẩn thận");
        }
    }

    @Override
    public void leave(Player p) {
        super.leave(p);
        if (p.isHuman()) {
            p.info.setInfo();
        }
    }

}
