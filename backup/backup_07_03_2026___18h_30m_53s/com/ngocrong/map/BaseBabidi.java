package com.ngocrong.map;

import com.ngocrong.bot.boss.mabu.BuiBui;
import com.ngocrong.bot.boss.mabu.Drabura;
import com.ngocrong.bot.boss.mabu.Yacon;
import com.ngocrong.map.tzone.CommandRoom;
import com.ngocrong.map.tzone.SpaceshipRoom;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import com.ngocrong.consts.MapName;
import com.ngocrong.lib.RandomCollection;
import com.ngocrong.map.tzone.GravityRoom;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class BaseBabidi extends IMap<SpaceshipRoom> {

    private static Logger logger = Logger.getLogger(BaseBabidi.class);
    public static int[] MAPS = {MapName.CONG_PHI_THUYEN, MapName.PHONG_CHO, MapName.CUA_AI_1, MapName.CUA_AI_3, MapName.PHONG_CHI_HUY};

    private long lastUpdateHypnosis;

    public BaseBabidi() {
        super(3600);
        lastUpdateHypnosis = System.currentTimeMillis();
        int zoneNumber = 5;
        for (int m : MAPS) {
            TMap map = MapManager.getInstance().getMap(m);
            for (int i = 0; i < zoneNumber; i++) {
                SpaceshipRoom z = null;
                if (m == MapName.CUA_AI_1) {
                    z = new GravityRoom(map, i);
                } else if (m == MapName.PHONG_CHI_HUY) {
                    z = new CommandRoom(map, i);
                } else {
                    z = new SpaceshipRoom(map, i);
                }
                if (m == MapName.CONG_PHI_THUYEN || m == MapName.PHONG_CHI_HUY) {
                    Drabura dr = new Drabura();
                    dr.setLocation(z);
                }
                if (m == MapName.PHONG_CHO || m == MapName.CUA_AI_1) {
                    BuiBui b = new BuiBui();
                    b.setLocation(z);
                }
                if (m == MapName.CUA_AI_3) {
                    Yacon y = new Yacon();
                    y.setLocation(z);
                }
                zones.add(z);
                map.addZone(z);
            }
        }
    }

    public void nextFloor(Player _c) {
        int floor = _c.getCurrentNumberFloorInBaseBabidi();

        floor++;
        if (floor >= MAPS.length) {
            floor = 0;
        }
        int next = MAPS[floor];
        TMap map = MapManager.getInstance().getMap(next);
        int zoneId = map.getZoneID();
        _c.zone.leave(_c);
        _c.setY((short) 100);
        map.enterZone(_c, zoneId);
    }

    @Override
    public void close() {
        MapManager.getInstance().baseBabidi = null;
        for (SpaceshipRoom z : zones) {
            List<Player> list = z.getListChar(Zone.TYPE_HUMAN);
            for (Player _c : list) {
                try {
                    _c.service.serverMessage("Trận chiến đã kết thúc");
                    _c.goHome();
                } catch (Exception e) {
                    

                }
            }
            z.map.removeZone(z);
        }

    }

    @Override
    public void update() {
        long now = System.currentTimeMillis();
        if (now - lastUpdateHypnosis >= 30000) {
            lastUpdateHypnosis = now;
            for (SpaceshipRoom z : zones) {
                try {
                    RandomCollection<Integer> rdc = new RandomCollection<>();
                    rdc.add(1 + Math.abs(z.teamKaiosin), 0);
                    rdc.add(1 + Math.abs(z.teamMabu), 1);
                    int r = rdc.next();
                    List<Player> list = z.getListChar(Zone.TYPE_HUMAN);
                    Player c = null;
                    ArrayList<Player> l = new ArrayList<>();
                    for (Player _c : list) {
                        try {
                            if ((r == 0 && _c.flag == 9) || (r == 1 && _c.flag == 10)) {
                                l.add(_c);
                            }
                        } catch (Exception e) {
                            
                            e.printStackTrace();
                        }
                    }
                    if (l.size() > 0) {
                        int rd = Utils.nextInt(l.size());
                        c = l.get(rd);
                        if (c != null) {
                            byte flag = 0;
                            if (r == 0) {
                                flag = 10;
                                c.service.serverMessage("Bạn bị Babiđây thôi miên");
                                z.service.npcChat((short) 46, String.format("Úm bala xì bùa %s", c.name));
                            } else {
                                flag = 9;
                                c.service.serverMessage("Bạn được Ôsin giải bùa mê");
                                z.service.npcChat((short) 44, String.format("Úm bala xì bùa, hóa giải cho %s", c.name));
                            }
                            c.setFlag(flag);
                        }
                    }
                } catch (Exception e) {
                    
                    e.printStackTrace();
                }
            }
        }
        super.update();
    }
}
