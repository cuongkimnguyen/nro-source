package com.ngocrong.map;

import _HunrProvision.boss.Boss;
import com.ngocrong.bot.boss.barrack.*;
import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemOption;
import com.ngocrong.map.tzone.ZBarrack;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.mob.Mob;
import com.ngocrong.model.MessageTime;
import com.ngocrong.model.RandomItem;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import lombok.Data;
import org.apache.log4j.Logger;

import java.util.List;

@Data
public class Barrack extends IMap<ZBarrack> {

    private static Logger logger = Logger.getLogger(Barrack.class);
    public static final int[] MAPS = {53, 58, 59, 60, 61, 62, 55, 56, 54, 57};

    private String openMemberName;
    private long openedAt;
    public boolean isWinner;
    public boolean rewarded;

    public Barrack(String name) {
        super(1800);
        this.openMemberName = name;
        this.openedAt = System.currentTimeMillis();
        for (int mapID : MAPS) {
            TMap map = MapManager.getInstance().getMap(mapID);
            ZBarrack z = new ZBarrack(this, map, map.autoIncrease++);
            map.addZone(z);
            zones.add(z);
            if (mapID == 59 || mapID == 62 || mapID == 55 || mapID == 54 || mapID == 57) {
                //set boss
                Boss boss = null;
                if (mapID == 59) {
                    // trung úy trắng
                    boss = new GeneralWhite();
                }
                if (mapID == 62) {
                    // trung úy xanh lơ
                    boss = new GeneralBlue();
                }
                if (mapID == 55) {
                    // trung úy thép
                    boss = new MajorMetallitron();
                }
                if (mapID == 54) {
                    // Ninja Áo tím
                    boss = new NinjaMurasaki(-1000000000, false);
                }
                if (mapID == 57) {
                    // Robot vệ sĩ
                    Boss boss1 = new Robot(-10000000, "Robot vệ sĩ 1");
                    boss1.setLocation(z);
                    Boss boss2 = new Robot(-10000001, "Robot vệ sĩ 2");
                    boss2.setLocation(z);
                    Boss boss3 = new Robot(-10000002, "Robot vệ sĩ 3");
                    boss3.setLocation(z);
                    Boss boss4 = new Robot(-10000003, "Robot vệ sĩ 4");
                    boss4.setLocation(z);
                }
                if (boss != null) {
                    boss.setLocation(z);
                }
            }
        }
    }

    public void enterMap(int map, Player _c) {
        for (ZBarrack zone : zones) {
            if (zone.map.mapID == map) {
                zone.setBarrack(_c.clan);
                zone.enter(_c);
                return;
            }
        }
    }

    public void win() {
        if (!rewarded) {
            rewarded = true;
            countDown = 300;
            updateMessageTimeForMember();
            sendServerMessage("Trại Độc Nhãn đã bị tiêu diệt, bạn có 5 phút để tìm kiếm viên ngọc 4 sao trước khi phi thuyền đến đón");
            int size = zones.size();          
            for (Zone z : zones) {
                Item item = new Item(ItemName.NGOC_RONG_7_SAO);
                item.setDefaultOptions();
                item.quantity = 1;
                ItemMap itemMap = new ItemMap(z.autoIncrease++);
                itemMap.item = item;
                itemMap.playerID = -1;
                itemMap.isBarrack = true;
                itemMap.x = (short) Utils.nextInt(50, z.map.width - 50);
                itemMap.y = z.map.collisionLand(itemMap.x, (short) 120);
                z.addItemMap(itemMap);
                z.service.addItemMap(itemMap);
            }
        }
    }

    public void enter(Player _c) {
        _c.setX((short) 35);
        _c.setY((short) 432);
        enterMap(53, _c);
        if (_c.taskMain.id == 17) {
            _c.updateTask(18);
        }
    }

    public void close() {
        for (Zone zone : zones) {
            List<Player> list = zone.getListChar(Zone.TYPE_HUMAN);
            for (Player _c : list) {
                try {
                    _c.service.sendThongBao("Đã hết thời gian, bạn sẽ được đưa về nhà");
                    _c.goHome();
                } catch (Exception e) {
                    

                }
            }
            zone.map.removeZone(zone);
        }
    }

    public void updateMessageTimeForMember() {
        for (Zone zone : zones) {
            List<Player> list = zone.getListChar(Zone.TYPE_HUMAN);
            for (Player _c : list) {
                try {
                    _c.setTimeForMessageTime(MessageTime.DOANH_TRAI, (short) countDown);
                } catch (Exception e) {
                    

                }
            }

        }
    }

    public void update() {
        if (!isWinner) {
            for (Zone z : zones) {
                if (z.map.mapID == MAPS[MAPS.length - 1]) {
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
                        isWinner = true;
                        sendServerMessage("Mau đi tìm Độc Nhãn");
                    }
                    break;
                }
            }
        }
        super.update();
    }

}
