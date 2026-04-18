package _HunrProvision.services;

import com.ngocrong.consts.MapName;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.map.tzone.Arena23;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Administrator
 */
public class DaiHoiVoThuat_23Service {

    public static final CopyOnWriteArrayList<_HunrProvision.daihoivothuat.DaiHoiVoThuat_23> DHVT23 = new CopyOnWriteArrayList<>();
    static long lastUpdate;

    public static void update() {

    }

    public static void addDHVT(_HunrProvision.daihoivothuat.DaiHoiVoThuat_23 dhvt) {
        Player player = dhvt.player;
        try {

            long gold = DaiHoiVoThuat_23Service.getGold(player.timesOfDHVT23);
            if (player.gold < gold) {
                player.service.sendThongBao("Bạn không đủ vàng để tham gia");
                return;
            }
            dhvt.joinMap();
            player.timesOfDHVT23++;
            player.gold -= gold;
            player.service.sendMoney();
            TMap map = MapManager.getInstance().getMap(MapName.DAU_TRUONG);
            Arena23 arena23 = new Arena23(map, player.zone.zoneID);
            if (player == null) {
                return;
            }
            player.setX((short) 334);
            player.setY((short) 274);
            player.zone.leave(player);
            arena23.setCurrFightingPlayer(player);
            arena23.enter(player);
            player.zone.service.setPosition(player, (byte) 0);
        } catch (Exception e) {
            
            e.printStackTrace();
            player.service.sendThongBao("Có lỗi xảy ra hãy thử lại");
        }
    }

    public static void removeDHVT(_HunrProvision.daihoivothuat.DaiHoiVoThuat_23 dhvt) {
        DHVT23.remove(dhvt);
    }

    public static long getGold(int times) {
        return 500_000_000;
    }

}
