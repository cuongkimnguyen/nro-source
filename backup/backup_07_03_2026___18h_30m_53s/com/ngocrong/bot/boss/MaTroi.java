package com.ngocrong.bot.boss;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.ItemTimeName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemOption;
import com.ngocrong.mob.Mob;
import com.ngocrong.server.SessionManager;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.time.LocalTime;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class MaTroi extends Boss {

    private static final Logger logger = Logger.getLogger(MaTroi.class);

    private int secondChat;

    public MaTroi() {
        super();
        this.name = "Ma trơi";
        this.limit = -1;
        setInfo(2000L, 1000, 1000, 10, 5);
        this.setTypePK((byte) 5);
        this.sayTheLastWordBeforeDie = "Ta không chết... ta chỉ đang đợi...";
    }

    @Override
    public void initSkill() {
        this.skills = new ArrayList<>();
    }

    @Override
    public long injure(Player plAtt, Mob mob, long dameInput) {
        if (plAtt != null && plAtt.exitsItemTime(ItemTimeName.CU_TOI)) {
            return Math.min(dameInput, Utils.nextInt(3, 10));
        }
        return 1;
    }

    @Override
    public void throwItem(Object obj) {
        if (obj == null) {
            return;
        }
        Player c = (Player) obj;
        int percent = Utils.nextInt(100);
        if (percent < 60) {
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
            item.addItemOption(new ItemOption(30,0));
            item.quantity = 1;
            dropItem(item, c);
        } else if (percent < 90) {
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
        } else {
            Item item = new Item(ItemName.NGOC_THAN_LONG);
            item.setDefaultOptions();
            item.quantity = 1;
            int bonusHP = 5 + Utils.nextInt(6);
            int bonusKI = 5 + Utils.nextInt(6);
            int bonusSD = 5 + Utils.nextInt(6);
            item.options.add(new ItemOption(77, bonusHP));
            item.options.add(new ItemOption(103, bonusKI));
            item.options.add(new ItemOption(50, bonusSD));
            dropItem(item, c);
        }
    }

    @Override
    public void startDie() {
        int map = zone.map.mapID;
        super.startDie();
        LocalTime now = LocalTime.now();
        if (now.getHour() >= 16) {
            Utils.setTimeout(() -> {
                MaTroi boss = new MaTroi();
                boss.setLocation(map, 0);
            }, 10 * 60_000L);
        }
    }

    @Override
    public void updateEveryOneSeconds() {
        try {
            if (this.zone != null) {
                for (Player player : this.zone.getPlayers()) {
                    if (player != null && !player.equals(this) && !player.exitsItemTime(ItemTimeName.CU_TOI) && !player.isDead()) {
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
        secondChat++;
        if (secondChat >= 10) {
            chat("Đêm nay... mày sẽ thấy điều cuối cùng trong đời.");
            secondChat = 0;
        }
        super.updateEveryOneSeconds();
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 651);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 652);
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 653);
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

