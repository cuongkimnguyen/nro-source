package com.ngocrong.map.expansion.blackdragon;

import com.ngocrong.clan.Clan;
import com.ngocrong.clan.ClanMember;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.MapName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.task.Task;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Data;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;

@Data
public class ZBlackDragonBall extends Zone {

    private static Logger logger = Logger.getLogger(ZBlackDragonBall.class);

    private int star;
    public ItemMap itemBlackDragonBall;
    private long[] lasts = new long[10];
    private Player playerHolding;
    private Object obj = new Object();

    public ZBlackDragonBall(TMap map, int zoneId, int star) {
        super(map, zoneId);
        this.star = star + 1;
        initBlackDragonBall(star);
    }

    public void initBlackDragonBall(int star) {
        int[] items = {ItemName.NGOC_RONG_1_SAO_DEN, ItemName.NGOC_RONG_2_SAO_DEN, ItemName.NGOC_RONG_3_SAO_DEN, ItemName.NGOC_RONG_4_SAO_DEN, ItemName.NGOC_RONG_5_SAO_DEN, ItemName.NGOC_RONG_6_SAO_DEN, ItemName.NGOC_RONG_7_SAO_DEN};
        ItemMap item = new ItemMap(this.autoIncrease++);
        item.isPickedUp = false;
        item.throwTime = System.currentTimeMillis();
        item.item = new Item(items[star]);
        item.playerID = -1;
        item.item.setDefaultOptions();
        item.y = 0;
        item.x = 0;
        item.countDown = 30 * 60;
   //     item.countDown = 10;
        itemBlackDragonBall = item;
    }

    public void setFlagForAllChar() {
        int clanLoot = -1;
        List<Player> list = getListChar(TYPE_HUMAN);
        Multimap<Integer, Player> multimap = ArrayListMultimap.create();
        for (Player p : list) {
            try {
                int clan = -1;
                if (p.clan != null) {
                    clan = p.clanID;
                }
                if (p.getItemLoot() != null) {
                    clanLoot = clan;
                }
                multimap.put(clan, p);
            } catch (Exception e) {
                
            }
        }
        int i = 1;
        for (int key : multimap.keys()) {
            Collection<Player> collection = multimap.get(key);
            for (Player p : collection) {
                if (key == -1) {
                    if (p.getItemLoot() != null) {
                        p.setFlag((byte) 8);
                    } else {
                        p.setFlag((byte) Utils.nextInt(1, 7));
                    }
                } else {
                    if (key == clanLoot) {
                        p.setFlag((byte) 8);
                    } else {
                        p.setFlag((byte) i);
                    }
                }
            }
            i++;
            if (i >= 8) {
                i = 1;
            }
        }

    }

    @Override
    public void enter(Player p) {
        super.enter(p);
        if (p.isHuman()) {
            synchronized (obj) {
                List<Player> list = getListChar(TYPE_HUMAN);
                int index = Utils.nextInt(MBlackDragonBall.FLAG_ARRAY.length);
                byte flag = (byte) MBlackDragonBall.FLAG_ARRAY[index];
                if (list.size() <= 1) {
                    flag = 1;
                    setPositionItem();
                } else {
                    Player _c = list.get(0);
                    if (p.clan != null && _c.clan == p.clan) {
                        flag = _c.flag;
                    } else if (itemBlackDragonBall.isPickedUp) {
                        if (p.clan != null) {
                            for (Player c2 : list) {
                                if (c2.clan == p.clan) {
                                    flag = c2.flag;
                                    break;
                                }
                            }
                        }
                    } else {
                        flag = (byte) Utils.nextInt(1, 7);
                    }
                }
                p.setFlag(flag);
            }
        }
    }

    @Override
    public void leave(Player p) {
        if (p != null && p.getItemLoot() != null) {
            p.dropItemSpe();
            itemBlackDragonBall.isPickedUp = false;
            service.addItemMap(itemBlackDragonBall);
            itemBlackDragonBall.countDown = 0;
            setPlayerHolding(null);
        }

        super.leave(p);

    }

    @Override
    public void update() {
        super.update();
        long now = System.currentTimeMillis();
        if (now - lasts[0] >= 10000) {
            lasts[0] = now;
            if (!itemBlackDragonBall.isPickedUp) {
                setPositionItem();
            }
        }

        if (now - lasts[1] >= 1000) {
            lasts[1] = now;
            if (itemBlackDragonBall.countDown > 0) {
                itemBlackDragonBall.countDown--;
            }
            if (itemBlackDragonBall.isPickedUp) {
                if (itemBlackDragonBall.countDown <= 0) {
                    closeBlackDragonBall();
                    return;
                }
            }
        }
        if (now - lasts[2] >= 10000) {
            lasts[2] = now;
            if (itemBlackDragonBall.isPickedUp && itemBlackDragonBall.countDown > 10 && playerHolding != null) {
                playerHolding.service.sendThongBao(String.format("Cố giữ ngọc thêm %d giây nữa sẽ thắng", itemBlackDragonBall.countDown));
            }
        }
        if (now - lasts[3] >= 2000) {
            lasts[3] = now;
            if (itemBlackDragonBall.isPickedUp) {
                if (itemBlackDragonBall.countDown <= 10 && playerHolding != null) {
                    playerHolding.service.sendThongBao(String.format("Cố giữ ngọc thêm %d giây nữa sẽ thắng", itemBlackDragonBall.countDown));
                }
            }
        }
    }

    public void serverMessage(String text) {
        List<Player> list = getListChar(TYPE_HUMAN);
        for (Player _c : list) {
            try {
                _c.service.sendThongBao(text);
            } catch (Exception e) {
                

            }
        }
    }

    public void closeBlackDragonBall() {
        running = false;
        this.isDoneNRD = true;
//        map.removeZone(this);
        if (playerHolding != null) {
            playerHolding.service.sendThongBao(String.format("Chúc mừng bạn đã dành được Ngọc rồng %d sao đen cho bang", star));
            if (playerHolding.clan != null) {
                Clan clan = playerHolding.clan;
                clan.clanPoint += 1;
                ClanMember mem = clan.getMember(playerHolding.id);
                if (mem != null) {
                    mem.clanPoint += 1000;
                    mem.currClanPoint += 1000;
                }
                clan.addClanRewardForMember(star);
            }
        }
        serverMessage("Trò chơi tìm ngọc đã kết thúc. Hẹn gặp bạn vào 20h tối mai");
        int[] M = {MapName.TRAM_TAU_VU_TRU, MapName.TRAM_TAU_VU_TRU_2, MapName.TRAM_TAU_VU_TRU_3};
        Utils.setTimeout(() -> {
            List<Player> list = getListChar(TYPE_HUMAN);
            for (Player _c : list) {
                try {
                    int mapID = M[_c.gender];
                    TMap map = MapManager.getInstance().getMap(mapID);
                    _c.setX(_c.calculateX(map));
                    _c.setY(map.collisionLand(_c.getX(), (short) 24));
                    leave(_c);
                    int zoneID = map.getZoneID();
                    map.enterZone(_c, zoneID);
                } catch (Exception e) {
                    
                    e.printStackTrace();
                }
            }
        }, 2000);
    }

    @Override
    public List<ItemMap> getListItemMap(Task... tasks) {
        List<ItemMap> items = super.getListItemMap();
        if (!itemBlackDragonBall.isPickedUp) {
            items.add(itemBlackDragonBall);
        }
        return items;
    }

    public void setPositionItem() {
        List<Player> list = getListChar(TYPE_HUMAN);
        if (!list.isEmpty()) {
            Player p = list.get(0);
            itemBlackDragonBall.x = p.getX();
            itemBlackDragonBall.y = map.collisionLand(p.getX(), (short) 24);
            service.removeItemMap(itemBlackDragonBall);
            service.addItemMap(itemBlackDragonBall);
        }
    }

    @Override
    public ItemMap findItemMapByID(int id) {
        if (itemBlackDragonBall != null && itemBlackDragonBall.id == id) {
            return itemBlackDragonBall;
        }
        lockItemMap.readLock().lock();
        try {
            for (ItemMap item : items) {
                if (item.id == id) {
                    return item;
                }
            }
            return null;
        } finally {
            lockItemMap.readLock().unlock();
        }
    }
}
