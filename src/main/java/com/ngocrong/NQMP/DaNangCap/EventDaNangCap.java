/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.NQMP.DaNangCap;

import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;

/**
 *
 * @author Administrator
 */
public class EventDaNangCap {

    public static void MobReward(Player player) {
        if (player == null || player.zone == null) {
            return;
        }
        if (Utils.isTrue(1, 8)) {
            int tempId = player.zone.map.isCold() ? 2206 : player.zone.map.isNappa() ? 2207 : 2208;
            if (tempId != -1) {
                Item item = new Item(tempId);
                item.setDefaultOptions();
                item.quantity = 1;
                ItemMap itemMap = new ItemMap(player.zone.autoIncrease++);
                itemMap.item = item;
                itemMap.playerID = Math.abs(player.id);
                itemMap.x = player.getX();
                itemMap.y = player.zone.map.collisionLand(player.getX(), player.getY());
                player.zone.addItemMap(itemMap);
                player.zone.service.addItemMap(itemMap);
            }
        }
    }

    public static void Exchange(Player player) {
        if (player == null || player.zone == null) {
            return;
        }
        if (player.getCountEmptyBag() == 0) {
            player.service.dialogMessage("Hành trang đã đầy");
            return;
        }
        Item xanhlam = player.getItemInBag(2206);
        Item xanhluc = player.getItemInBag(2207);
        Item _do = player.getItemInBag(2208);
        if (xanhlam == null || xanhluc == null || xanhlam.quantity < 99 || xanhluc.quantity < 99 || _do == null || _do.quantity < 99) {
            player.service.sendThongBao("Bạn không đủ vật phẩm sự kiện");
            player.service.dialogMessage("Cần có x99 Bình nước xanh lam,xanh lục,đỏ");
            return;
        }
        if (player.gold < 500_000_000) {
            player.service.sendThongBao("Cần có 500tr vàng để đổi");
            return;
        }
        player.removeItem(xanhlam.indexUI, 99);
        player.removeItem(xanhluc.indexUI, 99);
        player.removeItem(_do.indexUI, 99);
        player.subGold(500_000_000);
        int percent = Utils.nextInt(100);
        if (percent < 50) {
            long random2 = Utils.getParambyRandom(1, 25);
            long gold = random2 * 50_000_000L;
            player.addGold(gold);
            player.service.sendThongBao("Bạn nhận được " + Utils.currencyFormat(gold) + " vàng");
        } else if (percent < 85) {
            Item item = new Item(2199);
            player.addItem(item);
            player.service.sendThongBao("Bạn nhận được " + item.template.name);
        } else {
            Item item = new Item(2200);
            player.addItem(item);
            player.service.sendThongBao("Bạn nhận được " + item.template.name);
        }

    }
}
