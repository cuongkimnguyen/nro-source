/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.NQMP.Tet2025;

import com.ngocrong.item.Item;
import com.ngocrong.item.ItemOption;
import com.ngocrong.lib.RandomCollection;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;

/**
 *
 * @author Administrator
 */
public class VongQuayTet {

    static RandomCollection<Integer> random = new RandomCollection();
    public static VongQuayTet instance = new VongQuayTet();

    public VongQuayTet() {
        random.add(100, 17);
        random.add(100, 18);
        random.add(100, 19);
        random.add(100, 20);
        random.add(100, 220);
        random.add(100, 221);
        random.add(100, 222);
        random.add(100, 223);
        random.add(100, 224);
        random.add(100, 381);
        random.add(100, 382);
        random.add(100, 383);
        random.add(100, 384);
        random.add(100, 717);
        random.add(100, 2143);
        random.add(100, 2197);
        random.add(10, 2196);
        random.add(10, 2132);
        random.add(10, 904);
        random.add(30, 897);
        random.add(1, 1996);

        random.add(30, 1021);
        random.add(30, 1022);
        random.add(30, 1023);
        random.add(100, 2101);
    }

    public void start(Player player, int num) {
        if (player == null) {
            return;
        }
        if (player.getCountEmptyBag() < num) {
            player.service.sendThongBao("Hành trang không đủ ô trống");
            return;
        }
        Item thoivang = player.getItemInBag(457);
        if (thoivang == null || thoivang.quantity < num) {
            player.service.sendThongBao("Không tìm thấy đủ thỏi vàng");
            return;
        }
        player.subThoiVang(num);
//        player.pointThoiVang += num;
//        player.isChangePoint = true;
        for (int i = 0; i < num; i++) {
            Item item = new Item(random.next());
            item.setDefaultOptions();
            if (item.template.id == 904) {
                item.options.clear();
                item.options.add(new ItemOption(77, Utils.getParambyRandom(20, 30)));
                item.options.add(new ItemOption(50, Utils.getParambyRandom(20, 30)));
                item.options.add(new ItemOption(103, Utils.getParambyRandom(20, 30)));
            }
            if (item.template.id == 2132 || item.template.id == 897) {
                item.options.clear();
                item.options.add(new ItemOption(77, Utils.getParambyRandom(20, 40)));
                item.options.add(new ItemOption(50, Utils.getParambyRandom(20, 40)));
                item.options.add(new ItemOption(103, Utils.getParambyRandom(20, 40)));
            }
            player.addItem(item);
            player.service.sendThongBao("Bạn nhận được " + item.template.name);
        }
    }

}
