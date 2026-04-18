package com.ngocrong.bot.boss;

import _HunrProvision.boss.Boss;
import com.ngocrong.consts.ItemName;
import com.ngocrong.consts.ItemTimeName;
import com.ngocrong.consts.MapName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.map.tzone.Zone;
import com.ngocrong.server.SessionManager;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class ThoDaiCa extends Boss {

    private static final Logger logger = Logger.getLogger(ThoDaiCa.class);
    private static final long TAUNT_INTERVAL = 10_000L;
    private static final long RESPAWN_DELAY = 10 * 60_000L;
    private static final int HP_DRAIN_RADIUS = 200;
    private static final int HP_DRAIN_PERCENT = 5;
    private static final int[] LEVEL2_ITEMS = {
        ItemName.CUONG_NO_2,
        ItemName.BO_HUYET_2,
        ItemName.BO_KHI_2
    };
    private static final int[] DESTROY_ITEMS = {
        ItemName.AO_HUY_DIET_TD,
        ItemName.AO_HUY_DIET_NM,
        ItemName.AO_HUY_DIET_XD,
        ItemName.QUAN_HUY_DIET_TD,
        ItemName.QUAN_HUY_DIET_NM,
        ItemName.QUAN_HUY_DIET_XD,
        ItemName.GIAY_HUY_DIET_TD,
        ItemName.GIAY_HUY_DIET_NM,
        ItemName.GIAY_HUY_DIET_XD,
        ItemName.GANG_HUY_DIET_TD,
        ItemName.GANG_HUY_DIET_NM,
        ItemName.GANG_HUY_DIET_XD,
        ItemName.NHAN_HUY_DIET
    };

    public enum Region {
        EARTH(new int[]{
            MapName.LANG_ARU,
            MapName.DOI_HOA_CUC,
            MapName.THUNG_LUNG_TRE,
            MapName.RUNG_NAM,
            MapName.RUNG_XUONG,
            MapName.DAO_KAME,
            MapName.DONG_KARIN,
            MapName.LANG_MORI,
            MapName.DOI_NAM_TIM,
            MapName.THI_TRAN_MOORI,
            MapName.THUNG_LUNG_NAMEC,
            MapName.THUNG_LUNG_MAIMA,
            MapName.VUC_MAIMA,
            MapName.DAO_GURU,
            MapName.LANG_KAKAROT,
            MapName.DOI_HOANG,
            MapName.LANG_PLANT,
            MapName.RUNG_NGUYEN_SINH,
            MapName.RUNG_THONG_XAYDA,
            MapName.THANH_PHO_VEGETA,
            MapName.VACH_NUI_DEN
        }),
        FUTURE(new int[]{
            MapName.THANH_PHO_PHIA_DONG,
            MapName.THANH_PHO_PHIA_NAM,
            MapName.DAO_BALE,
            MapName.CAO_NGUYEN,
            MapName.THANH_PHO_PHIA_BAC,
            MapName.NGON_NUI_PHIA_BAC,
            MapName.THUNG_LUNG_PHIA_BAC,
            MapName.THI_TRAN_GINDER,
            MapName.NHA_BUNMA,
            MapName.VO_DAI_XEN_BO_HUNG
        }),
        COLD(new int[]{
            MapName.CANH_DONG_TUYET,
            MapName.RUNG_TUYET,
            MapName.NUI_TUYET,
            MapName.DONG_SONG_BANG,
            MapName.RUNG_BANG,
            MapName.HANG_BANG
        }),
        DEMON(new int[]{
            MapName.VUNG_DAT_MA
        }),
        BEACH(new int[]{
            MapName.BAI_BIEN_NGAY_HE
        });

        private final int[] mapIds;

        Region(int[] mapIds) {
            this.mapIds = mapIds;
        }

        public int randomMap() {
            return mapIds[Utils.nextInt(mapIds.length)];
        }
    }

    private final Region region;
    private long lastTaunt;

    public ThoDaiCa(Region region) {
        super();
        this.region = region;
        this.distanceToAddToList = 250;
        this.limit = -1;
        this.name = "Thỏ Đại Ca";
        setInfo(2000L, 1000, 1000, 10, 5);
        setTypePK((byte) 5);
        setDefaultPart();
        this.lastTaunt = System.currentTimeMillis();
    }

    @Override
    public void initSkill() {
        this.skills = new ArrayList<>();
    }

    @Override
    public long injure(Player plAtt, com.ngocrong.mob.Mob mob, long dameInput) {
        long cap = 1;
        if (plAtt != null && plAtt.exitsItemTime(ItemTimeName.CA_ROT_THAN)) {
            cap = Utils.nextInt(2, 10);
        }
        return Math.min(dameInput, cap);
    }

    @Override
    public void throwItem(Object obj) {
        if (!(obj instanceof Player) || zone == null) {
            return;
        }
        Player player = (Player) obj;
        int roll = Utils.nextInt(100);
        if (roll < 20) {
            Item item = new Item(LEVEL2_ITEMS[Utils.nextInt(LEVEL2_ITEMS.length)]);
            item.setDefaultOptions();
            item.quantity = 1;
            dropItem(item, player);
        } else if (roll < 30) {
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
            dropItem(item, player);
        } else if (roll < 40) {
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
            int random = Utils.nextInt(1, 10);
            for (int i = 0; i < random; i++) {
                Item carrot = new Item(ItemName.CU_CA_ROT);
                carrot.setDefaultOptions();
                carrot.quantity = 1;
                dropItem(carrot, null);
            }
        }
        super.throwItem(obj);
    }

    @Override
    public void startDie() {
        Zone currentZone = this.zone;
        if (currentZone != null) {
            currentZone.service.chat(this, "Oh nooooo…!");
        }
        super.startDie();
        Utils.setTimeout(() -> {
            try {
                ThoDaiCa boss = new ThoDaiCa(region);
                boss.setLocation(region.randomMap(), -1);
            } catch (Exception e) {
                logger.error("Failed to respawn Thỏ Đại Ca", e);
            }
        }, RESPAWN_DELAY);
    }

    @Override
    public void updateEveryOneSeconds() {
        try {
            if (zone != null) {
                long now = System.currentTimeMillis();
                if (now - lastTaunt >= TAUNT_INTERVAL) {
                    zone.service.chat(this, "CHƠI GIẤU XÁC HEM?!");
                    lastTaunt = now;
                }
                for (Player player : zone.getPlayers()) {
                    if (player == null || player == this || player.isDead() || player.exitsItemTime(ItemTimeName.CA_ROT_THAN)) {
                        continue;
                    }
                    int disX = Math.abs(getX() - player.getX());
                    int disY = Math.abs(getY() - player.getY());
                    if (disX <= HP_DRAIN_RADIUS && disY <= HP_DRAIN_RADIUS) {
                        long subHp = Math.max(1, player.info.hpFull * HP_DRAIN_PERCENT / 100);
                        player.info.hp -= subHp;
                        zone.service.attackPlayer(player, subHp, false, (byte) -1);
                        if (player.info.hp <= 0) {
                            player.killed(null);
                            player.startDie();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error while updating Thỏ Đại Ca", e);
        }
        super.updateEveryOneSeconds();
    }

    @Override
    public void setDefaultHead() {
        setHead((short) 403);
    }

    @Override
    public void setDefaultBody() {
        setBody((short) 404);
    }

    @Override
    public void setDefaultLeg() {
        setLeg((short) 405);
    }

    @Override
    public void sendNotificationWhenAppear(String map) {
        SessionManager.chatVip(String.format("BOSS %s vừa xuất hiện tại %s", this.name, map));
        logger.debug(String.format("BOSS %s xuất hiện tại %s khu %d", this.name, map, zone != null ? zone.zoneID : -1));
    }

    @Override
    public void sendNotificationWhenDead(String name) {
        SessionManager.chatVip(String.format("%s đã tiêu diệt %s", name, this.name));
    }
}
