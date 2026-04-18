package com.ngocrong.map.tzone;

import com.ngocrong.model.PowerInfo;
import com.ngocrong.map.BaseBabidi;
import com.ngocrong.map.TMap;
import com.ngocrong.user.Player;

import java.util.List;

public class SpaceshipRoom extends Zone {

    public int teamMabu;
    public int teamKaiosin;

    public SpaceshipRoom(TMap map, int zoneId) {
        super(map, zoneId);
    }

    @Override
    public void enter(Player p) {
        super.enter(p);
        if (p.isHuman()) {
            setTeam();
        }
        if (p.isHuman()) {
            PowerInfo accumulatedPoint = p.getAccumulatedPoint();
            if (accumulatedPoint != null) {
                int floor = p.getCurrentNumberFloorInBaseBabidi();
                if (accumulatedPoint.isMaxPoint() && floor + 1 < BaseBabidi.MAPS.length) {
                    p.showMenuDownToNextFloor();
                }
            }
        }
    }

    public void setTeam() {
        List<Player> list = getListChar(Zone.TYPE_HUMAN);
        int teamMabu = 0;
        int teamKaiosin = 0;
        for (Player c : list) {
            if (c != null) {
                if (c.flag == 9) {
                    teamKaiosin++;
                }
                if (c.flag == 10) {
                    teamMabu++;
                }
            }
        }
        this.teamKaiosin = teamKaiosin;
        this.teamMabu = teamMabu;
    }

    @Override
    public void leave(Player p) {
        super.leave(p);
        if (p.isHuman()) {
            setTeam();
        }
    }

}
