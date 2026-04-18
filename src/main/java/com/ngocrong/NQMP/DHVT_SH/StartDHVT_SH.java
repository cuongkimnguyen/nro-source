/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ngocrong.NQMP.DHVT_SH;

import _HunrProvision.ConfigStudio;
import _event.newyear_2026.EventNewYear2026;
import com.ngocrong.NQMP.DHVT_SH.DHVT_SH_Service;
import com.ngocrong.NQMP.DHVT_SH.SuperRank;
import com.ngocrong.consts.MapName;
import com.ngocrong.data.PlayerData;
import com.ngocrong.map.MapManager;
import com.ngocrong.map.TMap;
import com.ngocrong.map.tzone.ArenaSieuHang;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.user.Player;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Administrator
 */
public class StartDHVT_SH {

    public static HashMap<Integer, Long> listAttack = new HashMap<>();
    public static int zoneCount = 0;

    public static int removeOldAttacks() {
        long currentTime = System.currentTimeMillis();
        int removedCount = 0;

        // Sử dụng Iterator để an toàn khi xóa phần tử trong quá trình duyệt
        Iterator<Map.Entry<Integer, Long>> iterator = listAttack.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, Long> entry = iterator.next();
            Long storedTime = entry.getValue();

            // Kiểm tra nếu khoảng thời gian đã vượt quá 180000ms
            if (currentTime - storedTime >= 180000) {
                iterator.remove();
                removedCount++;
            }
        }

        return removedCount;
    }

    public static void Attack(Player player, int cloneID) {
        if (player == null || player.zone.map.mapID != MapName.DAI_HOI_VO_THUAT_2) {
            return;
        }
        var service = player.service;
        removeOldAttacks();
        if (player.superrank == null) {
            SuperRank.loadSuperRank(player);
            if (player.superrank == null) {
                DHVT_SH_Service.gI().checkTop(player);
                SuperRank.loadSuperRank(player);
            }
        }
        if (player.superrank == null) {
            service.sendThongBao("Có lỗi xảy ra, vui lòng thử lại");
            return;
        }
        if (player.viewTop == -1) {
            service.dialogMessage("Không thể thách đấu trong top 100");
            return;
        }
        if (cloneID == player.id) {
            service.serverMessage("Bạn không thể thách đấu chính mình");
            return;
        }
        if (player.getSession().user.getActivated() == 0) {
            service.sendThongBao("Bạn cần kích hoạt thành viên để sử dụng tính năng này");
            return;
        }
        var clone = Top_SieuHang.getTopbyPid(cloneID);
        if (clone == null) {
            service.sendThongBao("Không tìm thấy nhân vật");
            return;
        }
        if (clone.rank > player.superrank.rank) {
            service.sendThongBao("Không thể thách đấu người có thứ hạng thấp hơn mình");
            return;
        }
        int abs = Math.abs(clone.rank - player.superrank.rank);
        if (clone.rank < 100 && clone.rank > 20 && abs > 10) {
            service.sendThongBao("Không thể thách đấu chênh lệch 10 hạng");
            return;
        }
        if (clone.rank <= 20 && clone.rank > 10 && abs > 2) {
            service.sendThongBao("Không thể thách đấu chênh lệch 2 hạng");
            return;
        }
        if (clone.rank <= 10 && abs > 1) {
            service.sendThongBao("Không thể thách đấu chênh lệch 1 hạng");
            return;
        }

        if (listAttack.containsKey(cloneID)) {
            service.sendThongBao("Người bạn thách đấu hiện tại đang trong 1 trận chiến khác");
            return;
        }
        if (listAttack.containsKey(player.id)) {
            service.sendThongBao("Bạn đang được người khác thách đấu , hãy chờ đợi");
            return;
        }
        PlayerData data = null;
        Optional<PlayerData> dataPlayer = GameRepository.getInstance().player.findById(cloneID);
        if (dataPlayer.isPresent()) {
            data = dataPlayer.get();
        } else {
            service.serverMessage("Có lỗi xảy ra");
            return;
        }
        if (_HunrProvision.ConfigStudio.EVENT_NEWYEAR_2026) {
            if (!_event.newyear_2026.EventNewYear2026.checkTicket(player)) {
                service.sendThongBao("Bạn không đủ vé đấu (cần 3 vé miễn phí hoặc 5 thỏi vàng)");
                return;
            }
            // Check nếu đã nhận điểm và chưa đủ 30 phút thì không cho đánh
            if (!_event.newyear_2026.EventNewYear2026.onFight(player)) {
                return; // onFight đã gửi thông báo lỗi
            }
        } else {
            if (player.superrank.ticket <= 0) {
                if (player.gold < 200_000_000) {
                    service.sendThongBao("Bạn cần 200tr vàng để thách đấu");
                    return;
                }
                player.subGold(200_000_000);
            }
        }

        try {
            int clonePlayerId = cloneID;
            TMap map = MapManager.getInstance().getMap(MapName.DAU_TRUONG);
            ArenaSieuHang arenaSieuHang = new ArenaSieuHang(map, zoneCount++);
            player.setX((short) 334);
            player.setY((short) 274);
            player.zone.leave(player);
            arenaSieuHang.setData(data);
            arenaSieuHang.setCurrFightingPlayer(player);
            arenaSieuHang.enter(player);
            arenaSieuHang.setClonePlayerId(clonePlayerId);
            arenaSieuHang.setBossPointDhvt(clone.rank > 0 ? clone.rank : 0);
            player.zone.service.setPosition(player, (byte) 0);
            listAttack.put(cloneID, System.currentTimeMillis());
            listAttack.put(player.id, System.currentTimeMillis());
        } catch (Exception ignored) {
            System.err.println("Error at 24");
            ignored.printStackTrace();
        }
    }
}
