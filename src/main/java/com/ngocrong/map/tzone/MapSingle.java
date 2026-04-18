package com.ngocrong.map.tzone;

import com.ngocrong.user.Player;
import com.ngocrong.map.TMap;

public class MapSingle extends Zone {

    public MapSingle(TMap map, int zoneId) {
        super(map, zoneId);
    }

    public void leave(Player _c) {
        super.leave(_c);
        if (_c.isHuman()) {
            this.running = false;
            map.removeZone(this);
        }
    }

}
