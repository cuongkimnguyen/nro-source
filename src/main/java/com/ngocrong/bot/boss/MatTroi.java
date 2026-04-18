package com.ngocrong.bot.boss;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.ItemTimeName;
import com.ngocrong.consts.MapName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.mob.Mob;
import com.ngocrong.server.SessionManager;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class MatTroi extends Boss {

    private static final Logger logger = Logger.getLogger(MatTroi.class);

    public MatTroi() {
        super();
        this.name = "Mặt Trời";
        this.limit = -1;
        setInfo(30000L, 1000, 1000, 10, 5);
        this.setTypePK((byte) 5);
    }

    @Override
    public void initSkill() {
        this.skills = new ArrayList<>();
    }

    @Override
    public long injure(Player plAtt, Mob mob, long dameInput) {
        long limit = 1;
        if (plAtt != null && plAtt.exitsItemTime(ItemTimeName.BINH_NUOC_PHEP)) {
            limit = 3;
        }
        return Math.min(dameInput, limit);
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        Player c = (Player) obj;
        int percent = Utils.nextInt(100);
        if (percent < 50) {
            int[] arr = {ItemName.CUONG_NO_2, ItemName.BO_HUYET_2, ItemName.BO_KHI_2};
            Item item = new Item(arr[Utils.nextInt(arr.length)]);
            item.setDefaultOptions();
            item.quantity = 1;
            dropItem(item, c);
        } else if (percent < 70) {
            int chance = Utils.nextInt(100);
            int id;
            if (chance < 90) {
                int[] arr = {ItemName.AO_HUY_DIET_TD, ItemName.AO_HUY_DIET_NM, ItemName.AO_HUY_DIET_XD,
                    ItemName.QUAN_HUY_DIET_TD, ItemName.QUAN_HUY_DIET_NM, ItemName.QUAN_HUY_DIET_XD,
                    ItemName.GIAY_HUY_DIET_TD, ItemName.GIAY_HUY_DIET_NM, ItemName.GIAY_HUY_DIET_XD};
                id = arr[Utils.nextInt(arr.length)];
            } else if (chance < 95) {
                int[] arr = {ItemName.GANG_HUY_DIET_TD, ItemName.GANG_HUY_DIET_NM, ItemName.GANG_HUY_DIET_XD};
                id = arr[Utils.nextInt(arr.length)];
            } else {
                id = ItemName.NHAN_HUY_DIET;
            }
            Item item = new Item(id);
            item.setDefaultOptions();
            item.quantity = 1;
            dropItem(item, c);
        } else {
            for (int i = 0; i < 20; i++) {
                Item gold = new Item(ItemName.THOI_VANG);
                gold.setDefaultOptions();
                gold.quantity = 1;
                ItemMap im = new ItemMap(zone.autoIncrease++);
                im.item = gold;
                im.playerID = -1;
                im.x = (short) Utils.nextInt(0, zone.map.width);
                im.y = zone.map.collisionLand(im.x, getY());
                zone.addItemMap(im);
                zone.service.addItemMap(im);
            }
        }
    }

    @Override
    public void startDie() {
        int map = MapName.BAI_BIEN_NGAY_HE;
        super.startDie();
        Utils.setTimeout(() -> {
            MatTroi boss = new MatTroi();
            boss.setLocation(map, -1);
        }, 5 * 60000L);
    }

    @Override
    public void updateEveryOneSeconds() {
        try {
            if (this.zone != null) {
                for (Player player : this.zone.getPlayers()) {
                    if (player != null && !player.equals(this) && !player.exitsItemTime(ItemTimeName.BINH_NUOC_PHEP)) {
                        int disX = Math.abs(this.getX() - player.getX());
                        int disY = Math.abs(this.getY() - player.getY());
                        if (disX < 200 && disY < 200) {
                            long subHp = player.info.hpFull / 20;
                            player.info.hp -= subHp;
                            zone.service.attackPlayer(player, subHp, false, (byte) -1);
                            if (player.info.hp <= 0) {
                                player.killed(null);
                                player.startDie();
                            }                          
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.updateEveryOneSeconds();
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 1342);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 1343);
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 1344);
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
        SessionManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s", this.name, map));
        logger.debug(String.format("BOSS %s vừa xuất hiện tại %s khu vực %d", this.name, map, zone.zoneID));
    }

    @Override
    public void sendNotificationWhenDead(String name) {
        SessionManager.chatVip(String.format("%s: Đã tiêu diệt được %s mọi người đều ngưỡng mộ.", name, this.name));
    }
}
