/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _HunrProvision;

import com.ngocrong.bot.boss.fide.Fide;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.NpcName;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.map.GinyuForce;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.map.TeamAndroid13;
import com.ngocrong.map.TeamAndroid19;
import com.ngocrong.user.Player;
import java.util.ArrayList;

/**
 *
 * @author Administrator
 */
public class AdminHandle {

    public static final int CMD_Menu_Admin = 1171;
    public static AdminHandle instance = new AdminHandle();

    public static AdminHandle gI() {
        return instance;
    }
    public String[] listBoss = new String[]{
        "TĐST", "Fide Đại Ca", "Android 19 20", "Android 13 14 15", "Pic Poc KK", "Xên bọ hung"
    };

    public void showMenu(Player _c) {
        _c.menus.clear();
        _c.menus.add(new KeyValue(CMD_Menu_Admin, "Gọi Boss", 1));
        _c.menus.add(new KeyValue(CMD_Menu_Admin, "Xóa Boss", 2));
        _c.menus.add(new KeyValue(CMD_Menu_Admin, "Xóa Boss Khu hiện tại", 5));
        _c.service.openUIConfirm(NpcName.CON_MEO, "Bạn muốn làm gì", _c.getPetAvatar(), _c.menus);
    }

    public void perform(int idAction, Player player, Object... p) {
        switch (idAction) {
            case 1: {
                showBoss(player, 3);
                break;
            }
            case 2: {
                showBoss(player, 4);
                break;
            }
            case 3: {
                var index = ((Integer) p[0]);
                callBoss(index);
                break;
            }
            case 4: {
                var index = ((Integer) p[0]);
                deleteBoss(index);
                break;
            }
            case 5: {
                var index = ((Integer) p[0]);
                deleteBoss(index);
                break;
            }
        }
    }

    public void showBoss(Player _c, int type) {
        _c.menus.clear();
        for (int i = 0; i < listBoss.length; i++) {
            _c.menus.add(new KeyValue(CMD_Menu_Admin, listBoss[i], type, i));
        }
        _c.service.openUIConfirm(NpcName.CON_MEO, "Chọn Boss muốn thực hiện", _c.getPetAvatar(), _c.menus);
    }

    public void callBoss(int index) {
        switch (index) {
            case 0: {
                GinyuForce ginyu = new GinyuForce((byte) 1);
                ginyu.born();
                break;
            }
            case 1: {
                Fide fide = new Fide((byte) 0);
                fide.setLocation(80, -1);
                break;
            }
            case 2: {
                TeamAndroid19 teamAndroid19 = new TeamAndroid19();
                teamAndroid19.born();
                break;
            }
            case 3: {
                TeamAndroid13 teamAndroid13 = new TeamAndroid13();
                teamAndroid13.born();
                break;
            }
            case 4: {
                com.ngocrong.map.TeamAndroid16 teamAndroid16 = new com.ngocrong.map.TeamAndroid16();
                teamAndroid16.born();
                break;
            }
            case 5: {
                com.ngocrong.bot.boss.Cell.XenBoHung xenBoHung = new com.ngocrong.bot.boss.Cell.XenBoHung((byte) 0);
                xenBoHung.setLocation(100, -1);
                break;
            }
        }
    }

    private void clearBoss(TMap map) {
        synchronized (map.zones) {
            var array = new ArrayList<>(map.zones);
            for (var zone : array) {
                var players = new java.util.ArrayList<>(zone.players);
                for (Player boss : players) {
                    if (boss != null && boss.isBoss()) {
                        zone.leave(boss);
                    }
                }
            }
        }
    }

    private void clearBoss(TMap map, String... names) {
        synchronized (map.zones) {
            for (var zone : map.zones) {
                var players = new java.util.ArrayList<>(zone.players);
                for (Player boss : players) {
                    if (boss != null) {
                        for (String n : names) {
                            if (boss.name.equals(n)) {
                                zone.leave(boss);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void deleteBoss(int index) {
        switch (index) {
            case 0: {
                clearBoss(MapManager.getInstance().getMap(79), "Số 1", "Số 2", "Số 3", "Số 4", "Tiểu đội trưởng");
                clearBoss(MapManager.getInstance().getMap(82), "Số 1", "Số 2", "Số 3", "Số 4", "Tiểu đội trưởng");
                break;
            }
            case 1: {
                clearBoss(MapManager.getInstance().getMap(80), "Fide Đại Ca 1", "Fide Đại Ca 2", "Fide Đại Ca 3");
                break;
            }
            case 2: {
                clearBoss(MapManager.getInstance().getMap(93), "Android 19", "Android 20");
                break;
            }
            case 3: {
                TMap map = MapManager.getInstance().getMap(104);
                TeamAndroid13.clearAllboss(map);
                break;
            }
            case 4: {
                clearBoss(MapManager.getInstance().getMap(97), "Poc", "Pic", "King Kong");
                break;
            }
            case 5: {
                clearBoss(MapManager.getInstance().getMap(100), "Xên Bọ Hung", "Xên Bọ Hung 2", "Xên Hoàn Thiện");
//                clearBoss(MapManager.getInstance().getMap(103), "Xên Bọ Hung", "Xên Bọ Hung 2", "Xên Hoàn Thiện");
                break;
            }
        }
    }
}
