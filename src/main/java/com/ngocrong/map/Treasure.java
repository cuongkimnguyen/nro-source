package com.ngocrong.map;

import com.ngocrong.map.tzone.*;
import com.ngocrong.clan.Clan;
import com.ngocrong.clan.ClanMember;
import com.ngocrong.consts.MapName;
import com.ngocrong.mob.Mob;
import com.ngocrong.model.MessageTime;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import lombok.Data;
import org.apache.log4j.Logger;

import java.util.List;

@Data
public class Treasure extends IMap<ZTreasure> {

    private static Logger logger = Logger.getLogger(Treasure.class);
    public static final int[] MAPS = {MapName.DONG_HAI_TAC, MapName.CANG_HAI_TAC, MapName.HANG_BACH_TUOC, MapName.DONG_KHO_BAU};

    private short level;
    private Clan clan;
    private boolean isWinner;

    public Treasure(short level, Clan clan) {
        super(1800);
        this.clan = clan;
        this.level = level;
        for (int mapID : MAPS) {
            TMap map = MapManager.getInstance().getMap(mapID);
            ZTreasure z = null;
            if (mapID == MapName.DONG_HAI_TAC) {
                z = new PirateCave(this, map, map.autoIncrease++);
            }
            if (mapID == MapName.CANG_HAI_TAC) {
                z = new PiratePort(this, map, map.autoIncrease++);
            }
            if (mapID == MapName.HANG_BACH_TUOC) {
                z = new OctopusCave(this, map, map.autoIncrease++);
            }
            if (mapID == MapName.DONG_KHO_BAU) {
                z = new TreasureCave(this, map, map.autoIncrease++);
            }
            map.addZone(z);
            zones.add(z);
        }
    }

    public void enter(Player _c) {
        enterMap(MapName.DONG_HAI_TAC, _c);
    }

    public void enterMap(int map, Player _c) {
        Zone z = getZone(map);
        if (z != null) {
            z.enter(_c);
        }
    }

    public void updateMessageTimeForMember() {
        for (Zone zone : zones) {
            List<Player> list = zone.getListChar(Zone.TYPE_HUMAN);
            for (Player _c : list) {
                try {
                    _c.setTimeForMessageTime(MessageTime.HANG_KHO_BAU, (short) countDown);
                } catch (Exception e) {
                    

                }
            }

        }
    }

    public void addBonusPoint() {
        if (clan != null) {
            int time = countdownTimes - countDown;
            int m = time / 60;
            if (m == 0) {
                m = 1;
            }
            int point = (this.level / m) * 5;
            if (point == 0) {
                point = 1;
            }
            for (Zone z : zones) {
                try {
                    List<Player> list = z.getListChar(Zone.TYPE_HUMAN);
                    for (Player _c : list) {
                        ClanMember mem = clan.getMember(_c.id);
                        if (mem != null) {
                            clan.clanPoint += point;
                            mem.clanPoint += point;
                            mem.currClanPoint += point;
                            _c.service.sendThongBao(String.format("Bạn nhận được %s Capsule bang", Utils.currencyFormat(point)));
                        }
                    }
                } catch (Exception e) {
                    
                    e.printStackTrace();
                }
            }
        }
    }

    public void update() {
        if (countDown < 60 && countDown > 0 && countDown % 10 == 0) {
            sendServerMessage(String.format("Cái hang này sắp sập rồi, chúng ta phải rời khỏi đây ngay %d giây nữa", countDown));
        }
        if (!isWinner) {
            for (Zone z : zones) {
                if (z.map.mapID == MapName.DONG_KHO_BAU) {
                    int num = 0;
                    List<Mob> mobs = z.getListMob();
                    for (Mob mob : mobs) {
                        if (!mob.isDead()) {
                            num++;
                        }
                    }
                    if (num == 0) {
                        List<Player> players = z.getListChar(Zone.TYPE_BOSS);
                        for (Player _c : players) {
                            num++;
                        }
                    }
                    if (num == 0) {
                        addBonusPoint();
                        isWinner = true;
                        countDown = 60;
                        updateMessageTimeForMember();
                    }
                    break;
                }
            }
        }
        super.update();
    }

    @Override
    public void close() {
        clan.treasure = null;
        for (Zone zone : zones) {
            List<Player> list = zone.getListChar(Zone.TYPE_HUMAN);
            for (Player _c : list) {
                try {
                    _c.teleport(MapName.DAO_KAME);
                } catch (Exception e) {
                    

                }
            }
            zone.map.removeZone(zone);
        }
    }

}
