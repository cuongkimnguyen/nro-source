package com.ngocrong.lucky;

import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.NpcName;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.server.SessionManager;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;

public class GoldLucky extends Lucky {

    public GoldLucky(int id, String name) {
        super(id, name);
    }

    @Override
    public void join(Player _c, byte type) {
        if (!isCountdownRemainingTime) {
            return;
        }
        if (timeRemaining < 10) {
            _c.service.sendThongBao("Đã hết thời gian tham gia");
            return;
        }
        if (type == 0) {
            if (totalNormal >= 2000000000) {
                _c.service.sendThongBao("Đã đạt số lượng vàng tối đa");
                return;
            }
            if (_c.getGold() < 1000000) {
                return;
            }
        } else {
            if (totalVip >= 2000000000) {
                _c.service.sendThongBao("Đã đạt số lượng vàng tối đa");
                return;
            }
            if (_c.getGold() < 10000000) {
                return;
            }
        }
        Gamer pl = findPlayer(_c.id, type);
        if (pl == null) {
            pl = new Gamer();
            pl.id = _c.id;
            pl.name = _c.name;
            pl.quantity = 0;
            pl.numberOfBets = 0;
            addPlayer(pl, type);
        }
        if (pl.numberOfBets < 10) {
            if (type == 0) {
                _c.addGold(-1000000);
                totalNormal += 1000000;
                pl.quantity += 1000000;
            } else {
                _c.addGold(-10000000);
                totalVip += 10000000;
                pl.quantity += 10000000;
            }
            pl.numberOfBets++;
            show(_c);
        } else {
            _c.service.sendThongBao("Mỗi vòng chỉ có thể đặt tối đa 10 lần");
        }
    }

    @Override
    public void show(Player _player) {
        int[] percentNormal = {0, 0};
        int[] percentVIP = {0, 0};
        Gamer pl = findPlayer(_player.id, LUCKY_NORMAL);
        if (pl != null) {
            percentNormal = Utils.formatPercent(pl.quantity, totalNormal);
        }
        pl = findPlayer(_player.id, LUCKY_VIP);
        if (pl != null) {
            percentVIP = Utils.formatPercent(pl.quantity, totalVip);
        }
        int totalRewardNormal = totalNormal - totalNormal / 100;
        int totalRewardVIP = totalVip - totalVip / 100;
        _player.menus.add(new KeyValue(780, "Cập nhật"));
        _player.menus.add(new KeyValue(784, "Thường\n1 triệu\nvàng"));
        _player.menus.add(new KeyValue(785, "VIP\n10 triệu\nvàng"));
        _player.menus.add(new KeyValue(CMDMenu.CANCEL, "Đóng"));
        StringBuilder sb = new StringBuilder();
        if (percentNormal[1] != 0) {
            sb.append(String.format("Tổng giải thưởng: %s vàng, cơ hội trúng hiện tại của bạn là: %d.%d%%\n", Utils.currencyFormat(totalRewardNormal), percentNormal[0], percentNormal[1]));
        } else {
            sb.append(String.format("Tổng giải thưởng: %s vàng, cơ hội trúng hiện tại của bạn là: %d%%\n", Utils.currencyFormat(totalRewardNormal), percentNormal[0]));
        }
        if (percentVIP[1] != 0) {
            sb.append(String.format("Tổng giải VIP: %s vàng, cơ hội trúng hiện tại của bạn là: %d.%d%%\n", Utils.currencyFormat(totalRewardVIP), percentVIP[0], percentVIP[1]));
        } else {
            sb.append(String.format("Tổng giải VIP: %s vàng, cơ hội trúng hiện tại của bạn là: %d%%\n", Utils.currencyFormat(totalRewardVIP), percentVIP[0]));
        }
        sb.append(String.format("Thời gian còn lại: %d giây", timeRemaining));
        _player.service.openUIConfirm(NpcName.LY_TIEU_NUONG, sb.toString(), (short) 3049, _player.menus);
    }

    @Override
    public void reward(Gamer pl, int quantity) {
        Player _c = SessionManager.findChar(pl.id);
        if (_c != null) {
            _c.addGold(quantity);
        }
    }

}
