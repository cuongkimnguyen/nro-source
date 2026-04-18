package com.ngocrong.map.tzone;

import com.ngocrong.bot.boss.mabu.Mabu;
import com.ngocrong.consts.CMDMenu;
import com.ngocrong.consts.NpcName;
import com.ngocrong.lib.KeyValue;
import com.ngocrong.map.TMap;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;

import java.util.List;

public class CommandRoom extends SpaceshipRoom {

    private Mabu mabu;
    private byte percentMabu;
    private long lastUpdateMabu;
    private long last;

    private int delayEnd;

    public CommandRoom(TMap map, int zoneId) {
        super(map, zoneId);
        mabu = new Mabu();
    }

    public void update() {
        super.update();
        long now = System.currentTimeMillis();

        if (delayEnd >= 0) {
            if (now - last >= 1000) {
                last = now;
                List<Player> list = getListChar(TYPE_HUMAN);
                for (Player _c : list) {
                    try {
                        if (delayEnd <= 0) {
                            _c.goHome();
                        } else {
                            _c.service.serverMessage(String.format("Về sau %d giây nữa", delayEnd));
                        }
                    } catch (Exception e) {
                        

                    }
                }
                delayEnd--;
            }
        } else if (percentMabu < 100) {
            if (now - lastUpdateMabu >= 500) {
                lastUpdateMabu = now;
                if (getNumPlayer() > 0) {
                    percentMabu++;
                    service.sendPercentMabu(percentMabu);
                } else {
                    percentMabu--;
                }
                if (percentMabu > 100) {
                    percentMabu = 100;
                }
                if (percentMabu < 0) {
                    percentMabu = 0;
                }
                if (percentMabu == 100) {
                    Utils.setTimeout(() -> {
                        percentMabu = 101;
                        //service.sendPercentMabu(percentMabu);
                        if (mabu.isDead()) {
                            mabu.wakeUpFromDead();
                        }
                        mabu.setTypePK((byte) 5);
                        mabu.setLocation(this);
                    }, 3000);

                }
            }

        }
    }

    public void end() {
        String text = "Trận chiến đã kết thúc, chúng ta phải rời khỏi đây ngay";
        List<Player> list = getListChar(TYPE_HUMAN);
        for (Player _c : list) {
            try {
                _c.menus.clear();
                _c.menus.add(new KeyValue(CMDMenu.CANCEL, "OK"));
                _c.service.openUIConfirm(NpcName.OSIN, text, (short) 4390, _c.menus);
            } catch (Exception e) {
                

            }
        }
        delayEnd = 30;
        percentMabu = 0;
    }

}
