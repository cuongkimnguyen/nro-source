package com.ngocrong.map;

import com.ngocrong.map.tzone.Zone;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class IMap<E> {

    public boolean running;
    private int id;
    protected int countDown, countdownTimes;
    protected ArrayList<E> zones;

    public IMap(int countDown) {
        this.countdownTimes = countDown;
        this.countDown = countDown;
        long now = System.currentTimeMillis() / 1000;
        this.id = (int) (now + Utils.nextInt((int) now));
        this.zones = new ArrayList<>();
        this.running = true;
    }

    public Zone getZone(int mapID) {
        for (E e : zones) {
            Zone z = (Zone) e;
            if (z.map.mapID == mapID) {
                return z;
            }
        }
        return null;
    }

    public void sendServerMessage(String text) {
        for (E e : zones) {
            Zone zone = (Zone) e;
            List<Player> list = zone.getListChar(Zone.TYPE_HUMAN);
            for (Player _c : list) {
                try {
                    _c.service.sendThongBao(text);
                } catch (Exception ex) {
                    

                }
            }

        }
    }

    public void update() {
        countDown--;
        if (countDown <= 0) {
            running = false;
            close();
        }
    }

    public abstract void close();
}
