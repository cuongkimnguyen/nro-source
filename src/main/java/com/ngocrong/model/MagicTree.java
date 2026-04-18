package com.ngocrong.model;

import com.ngocrong.consts.Cmd;
import com.ngocrong.network.Message;
import com.ngocrong.server.Server;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import com.ngocrong.item.Item;
import com.ngocrong.lib.KeyValue;
import com.google.gson.annotations.SerializedName;
import com.ngocrong.network.FastDataOutputStream;
import org.apache.log4j.Logger;

import java.io.IOException;

public class MagicTree {

    private static final Logger logger = Logger.getLogger(MagicTree.class);

    public static final long[] TIME_UPGRADE = new long[]{0L, 600000L, 6000000L, 58920000L, 597600000L, 1202400000L, 2592000000L, 4752000000L, 5961600000L, 8640000000L};
    public static final int[] GOLD_UPGRADE = new int[]{0, 5000, 10000, 100000, 1000000, 10000000, 20000000, 50000000, 100000000, 300000000};
    public static final int[] PEAN_ID = new int[]{0, 13, 60, 61, 62, 63, 64, 65, 352, 523, 595};
    public static final int[][] POSITION = {{378, 336}, {200, 336}, {300, 336}};
    public static final String[] TREE_NAME = {"Đậu thần cấp 1", "Đậu thần cấp 2", "Đậu thần cấp 3", "Đậu thần cấp 4", "Đậu thần cấp 5", "Đậu thần cấp 6", "Đậu thần cấp 7", "Đậu thần cấp 8", "Đậu thần cấp 9", "Đậu thần cấp 10"};
    public static final int[][] ICON = {{84, 85, 86, 87, 88, 89, 90, 90, 90, 90}, {371, 372, 373, 374, 375, 376, 377, 377, 377, 377}, {378, 379, 380, 381, 382, 383, 384, 384, 384, 384}};

    public transient int id;
    public transient int x;
    public transient int y;
    public transient int currPeas;
    public transient int remainPeas;
    public transient int maxPeas;
    public transient String strInfo;
    public transient String name;
    public transient int seconds;
    public transient int planet;

    @SerializedName("level")
    public int level;
    @SerializedName("upgrade")
    public boolean isUpgrade;
    @SerializedName("upgrade_time")
    public long upgradeTime;
    @SerializedName("last_harvest")
    public long lastHarvestTime;

    public void init() {
        int[] pos = POSITION[this.planet];
        int[] icon = ICON[this.planet];
        this.x = pos[0];
        this.y = pos[1];
        int index = this.level - 1;
        this.id = icon[index];
        this.name = TREE_NAME[index];
        this.maxPeas = 5 + index * 2;
        update();
    }

    public void update() {
        long now = System.currentTimeMillis();
        if (isUpgrade) {
            this.seconds = (int) ((this.upgradeTime - now) / 1000);
            if (this.seconds <= 0) {
                this.isUpgrade = false;
                this.upgradeTime = 0;
                this.level++;
                init();
            }
        } else {
            int fruitingTime = 30 * this.level;
            int waited = (int) ((now - this.lastHarvestTime) / 1000);
            this.currPeas = waited / fruitingTime;
            if (this.currPeas >= this.maxPeas) {
                this.currPeas = this.maxPeas;
                this.seconds = 0;
            } else {
                this.seconds = (this.level * 30) - (waited - (this.currPeas * fruitingTime));
            }
        }
    }

    public void openMenu(Player _player) {
        try {
            _player.menus.clear();
            if (_player.taskMain != null && _player.taskMain.id == 0 && _player.taskMain.index < 4) {
                return;
            }
            if (this.isUpgrade) {
                long now = System.currentTimeMillis();
                int gem = (int) ((this.upgradeTime - now) / 1000 / 60 / 10);
                gem = gem > 0 ? gem : 1;
                _player.menus.add(new KeyValue(3504, "Nâng cấp nhanh " + Utils.formatNumber(gem) + " ngọc"));
                _player.menus.add(new KeyValue(3505, "Hủy nâng cấp hồi " + Utils.formatNumber(GOLD_UPGRADE[this.level] / 2) + " vàng"));
            } else {
                _player.menus.add(new KeyValue(3500, "Thu hoạch"));
                if (this.level < 10) {
                    String t = Utils.getTime((int) (TIME_UPGRADE[this.level] / 1000));
                    _player.menus.add(new KeyValue(3501, "Nâng cấp\n" + t + "\n" + Utils.formatNumber(GOLD_UPGRADE[this.level]) + " vàng"));
                }
                if (this.currPeas < this.maxPeas) {
                    int gem = ((this.level - 1) * 5 + 1);
                    gem = gem > 0 ? gem : 1;
                    _player.menus.add(new KeyValue(3502, "Kết hạt nhanh " + gem + " ngọc"));
                }
            }
            Message ms = new Message(Cmd.MAGIC_TREE);
            FastDataOutputStream ds = ms.writer();
            ds.writeByte(1);
            for (KeyValue<Integer, String> keyValue : _player.menus) {
                ds.writeUTF(keyValue.value);
            }
            ds.flush();
            _player.service.sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            
            logger.error("failed!", ex);
        }
    }

    public void harvest(Player _player) {
        update();
        int currPeas = this.currPeas;
        if (currPeas == 0) {
            return;
        }
        int number = 0;
        for (Item item : _player.itemBag) {
            if (item != null && item.template.type == Item.TYPE_DAUTHAN) {
                number += item.quantity;
            }
        }
        int max = Server.getMaxQuantityItem();
        int received = max - number;
        if (received > currPeas) {
            received = currPeas;
        }
        if (received > 0) {
            Item item = new Item(PEAN_ID[this.level]);
            item.setDefaultOptions();
            item.quantity = received;
            if (_player.addItem(item)) {
                currPeas -= received;
            }
        }
        if (currPeas > 0) {
            number = 0;
            max = 20;
            for (Item item : _player.itemBox) {
                if (item != null && item.template.type == Item.TYPE_DAUTHAN) {
                    number += item.quantity;
                }
            }
            received = max - number;
            if (received > currPeas) {
                received = currPeas;
            }
            if (received > 0) {
                Item item = new Item(PEAN_ID[this.level]);
                item.setDefaultOptions();
                item.quantity = received;
                if (_player.addItemToBox(item)) {
                    currPeas -= received;
                }
            }
        }
        if (currPeas > 0) {
            _player.service.sendThongBao(String.format("Rương đồ đã chứa đầy %d viên đậu, không thể thu hoạch thêm.", 20));
        }
        if (_player.taskMain != null && _player.taskMain.id == 0 && _player.taskMain.index == 4) {
            _player.taskNext();
        }
        this.lastHarvestTime = System.currentTimeMillis() - (this.level * 30000L * currPeas);
        update();
        if (currPeas < maxPeas) {
            _player.service.magicTree((byte) 2, this);
        }
    }

    public void fastest(Player _player) {
        int gem = ((this.level - 1) * 5 + 1);
        gem = gem > 0 ? gem : 1;
        if (gem > _player.getTotalGem()) {
            _player.service.sendThongBao("Bạn không đủ ngọc để thực hiện");
            return;
        }
        _player.subDiamond(gem);
        this.lastHarvestTime = System.currentTimeMillis() - (this.level * 30000L * this.maxPeas);
        update();
        _player.service.magicTree((byte) 0, this);
    }

    public void upgrade(Player _player) {
        int gold = GOLD_UPGRADE[this.level];
        if (gold > _player.gold) {
            int thieu = (int) (gold - _player.gold);
            _player.service.sendThongBao("Bạn còn thiếu " + Utils.formatNumber(thieu) + " vàng.");
            return;
        }
        _player.addGold(-gold);
        this.isUpgrade = true;
        this.upgradeTime = System.currentTimeMillis() + TIME_UPGRADE[this.level];
        update();
        _player.service.magicTree((byte) 0, this);
    }

    public void quickUpgrade(Player _player) {
        long now = System.currentTimeMillis();
        int gem = (int) ((this.upgradeTime - now) / 1000 / 60 / 10);
        gem = gem > 0 ? gem : 1;
        if (_player.getTotalGem() < gem) {
            _player.service.sendThongBao("Bạn không đủ ngọc để thực hiện");
            return;
        }
        _player.subDiamond(gem);
        this.upgradeTime = System.currentTimeMillis();
        update();
        _player.service.magicTree((byte) 0, this);
    }

    public void cancelUpgrade(Player _player) {
        if (this.isUpgrade) {
            this.isUpgrade = false;
            this.upgradeTime = 0;
            update();
            int gold = GOLD_UPGRADE[this.level] / 2;
            _player.addGold(gold);
            _player.service.magicTree((byte) 0, this);
        }
    }
}
