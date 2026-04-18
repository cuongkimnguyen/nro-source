/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.NQMP.Tet2025;

import com.ngocrong.consts.ItemName;
import com.ngocrong.item.Item;
import com.ngocrong.item.ItemMap;
import com.ngocrong.item.ItemOption;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;

/**
 *
 * @author Administrator
 */
public class EventTet2025 {

    public static final int HoaDao = 2192;
    public static final int HoaMai = 2193;
    public static final int Lixi = 717;
    public static final int Lixi2 = 2143;
    public static final int HopQua2025 = 2194;
    public static final int HopQua2025Vip = 2195;

    public static void mobReward(Player player) {
        if (player == null || player.zone == null || true) {
            return;
        }
        if (Utils.nextInt(100) <= 20) {
            boolean flag = player.zone.map.mapID == 1 || player.zone.map.mapID == 2 || player.zone.map.mapID == 8 || player.zone.map.mapID == 9 || player.zone.map.mapID == 15 || player.zone.map.mapID == 16;
            int tempId = flag ? HoaDao : player.zone.map.isNappa() ? HoaMai : -1;
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

    public static void DoiHopQua(Player player) {
        if (player == null && player.zone == null) {
            return;
        }
        if (player.getCountEmptyBag() == 0) {
            player.service.dialogMessage("Hành trang đã đầy");
            return;
        }
        Item hoadao = player.getItemInBag(HoaDao);
        Item hoamai = player.getItemInBag(HoaMai);
        Item lixi = player.getItemInBag(Lixi);
        if (hoadao == null || hoamai == null || hoadao.quantity < 99 || hoamai.quantity < 99 || lixi == null || lixi.quantity < 1) {
            player.service.sendThongBao("Bạn không đủ vật phẩm sự kiện");
            player.service.dialogMessage("Cần có x99 hoa đào và x99 hoa mai và 1 Lì xì");
            return;
        }
        if (player.gold < 500_000_000) {
            player.service.sendThongBao("Cần có 500tr vàng để đổi");
            return;
        }
        player.removeItem(hoadao.indexUI, 99);
        player.removeItem(hoamai.indexUI, 99);
        player.removeItem(lixi.indexUI, 1);
        player.subGold(500_000_000);
        Item item = new Item(HopQua2025);
        item.setDefaultOptions();
        player.addItem(item);
        player.service.sendThongBao("Bạn nhận được " + item.template.name);
    }

    public static boolean useItem(Player player, Item item) {

        if (player == null || item == null) {
            return false;
        }

        if (item.template.id != 2194 && item.template.id != 2195 && item.template.id != 2196 && item.template.id != 2143) {
            return false;
        }
        int[] option = new int[]{221, 197, 196};
        int optionId = option[Utils.nextInt(option.length)];
        int random = Utils.nextInt(1000);
        if (player.getCountEmptyBag() == 0) {
            player.service.dialogMessage("Hành trang đã đầy");
            return false;
        }
        if (item.template.id == 2143) {
            long random2 = Utils.nextInt(1, 20);
            long gold = random2 * 50_000_000L;
            player.addGold(gold);
            player.service.sendThongBao("Bạn nhận được " + Utils.currencyFormat(gold) + " vàng");
            return true;
        } else if (item.template.id == 2194) {
            Item item2 = null;
            if (random <= 600) {
                item2 = new Item(Utils.nextInt(17, 20));
                item2.quantity = 1;
            } else if (random <= 700) {
                item2 = new Item(898);
                item2.options.add(new ItemOption(77, Utils.getParambyRandom(10, 30)));
                item2.options.add(new ItemOption(50, Utils.getParambyRandom(10, 30)));
                item2.options.add(new ItemOption(103, Utils.getParambyRandom(10, 30)));
                item2.options.add(new ItemOption(optionId, Utils.getParambyRandom(2, 3)));
                item2.options.add(new ItemOption(93, Utils.getParambyRandom(1, 3)));
            } else if (random <= 800) {
                item2 = new Item(2136);
                item2.options.add(new ItemOption(77, Utils.getParambyRandom(10, 25)));
                item2.options.add(new ItemOption(50, Utils.getParambyRandom(10, 25)));
                item2.options.add(new ItemOption(103, Utils.getParambyRandom(10, 25)));
                item2.options.add(new ItemOption(93, Utils.getParambyRandom(1, 3)));

            } else if (random <= 999) {
                item2 = new Item(2190);
                item2.options.add(new ItemOption(77, Utils.getParambyRandom(10, 25)));
                item2.options.add(new ItemOption(50, Utils.getParambyRandom(10, 25)));
                item2.options.add(new ItemOption(103, Utils.getParambyRandom(10, 25)));
                item2.options.add(new ItemOption(93, Utils.getParambyRandom(1, 3)));

            } else {
                int[] awj = new int[]{
                    555, 556, 557, 558, 559, 563, 567
                };
                item2 = new Item(awj[Utils.nextInt(awj.length)]);
                item2.setDefaultOptions();
            }
            player.addItem(item2);
            //   player.pointHopQua2025++;
            player.isChangePoint = true;
            player.service.sendThongBao("Bạn nhận được " + item2.template.name);
            return true;
        }
        if (item.template.id == 2195) {
            Item item2 = null;
            if (random <= 600) {
                item2 = new Item(Utils.nextInt(16, 20));
                item2.quantity = 1;
            } else if (random <= 700) {
                item2 = new Item(898);
                item2.options.add(new ItemOption(77, Utils.getParambyRandom(20, 30)));
                item2.options.add(new ItemOption(50, Utils.getParambyRandom(20, 30)));
                item2.options.add(new ItemOption(103, Utils.getParambyRandom(20, 30)));
                item2.options.add(new ItemOption(optionId, Utils.getParambyRandom(2, 5)));
                if (Utils.nextInt(10) <= 8) {
                    item2.options.add(new ItemOption(93, Utils.getParambyRandom(1, 7)));
                }
            } else if (random <= 800) {
                item2 = new Item(2136);
                item2.options.add(new ItemOption(77, Utils.getParambyRandom(20, 40)));
                item2.options.add(new ItemOption(50, Utils.getParambyRandom(20, 40)));
                item2.options.add(new ItemOption(103, Utils.getParambyRandom(20, 40)));
                if (Utils.nextInt(10) <= 8) {
                    item2.options.add(new ItemOption(93, Utils.getParambyRandom(1, 7)));
                }

            } else if (random <= 999) {
                item2 = new Item(2190);
                item2.options.add(new ItemOption(77, Utils.getParambyRandom(20, 40)));
                item2.options.add(new ItemOption(50, Utils.getParambyRandom(20, 40)));
                item2.options.add(new ItemOption(103, Utils.getParambyRandom(20, 40)));
                if (Utils.nextInt(10) <= 8) {
                    item2.options.add(new ItemOption(93, Utils.getParambyRandom(1, 7)));
                }

            } else {
                int[] awj = new int[]{
                    555, 556, 557, 558, 559, 563, 567
                };
                item2 = new Item(awj[Utils.nextInt(awj.length)]);
                item2.setDefaultOptions();
            }
            player.addItem(item2);
            //   player.pointHopQua2025 += 3;
            player.service.sendThongBao("Bạn nhận được " + item2.template.name);
            player.isChangePoint = true;
            return true;
        }
        if (item.template.id == 2196) {
            Item item2 = new Item(898);
            item2.options.add(new ItemOption(77, Utils.getParambyRandom(30, 40)));
            item2.options.add(new ItemOption(50, Utils.getParambyRandom(30, 40)));
            item2.options.add(new ItemOption(103, Utils.getParambyRandom(30, 40)));
            item2.options.add(new ItemOption(optionId, Utils.getParambyRandom(10, 25)));
            if (Utils.nextInt(10) <= 8) {
                item2.options.add(new ItemOption(93, Utils.getParambyRandom(1, 5)));
            }
            player.addItem(item2);
            player.service.sendThongBao("Bạn nhận được " + item2.template.name);

            return true;
        }
        return false;
    }

}
