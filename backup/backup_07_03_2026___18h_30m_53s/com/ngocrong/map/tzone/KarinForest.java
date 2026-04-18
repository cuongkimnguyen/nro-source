package com.ngocrong.map.tzone;

import com.ngocrong.bot.boss.karin.TaoPaiPai;
import com.ngocrong.user.Player;
import com.ngocrong.map.TMap;
import com.ngocrong.task.Task;

public class KarinForest extends MapSingle {

    public KarinForest(TMap map, int zoneId, Player _c) {
        super(map, zoneId);
        Task task = _c.taskMain;
        if ((task.id == 9 && (task.index == 2 || task.index == 3)) || (task.id == 10 && (task.index == 0 || task.index == 1))) {
            if (task.id == 10 && task.index == 1) {
                TaoPaiPai taoPaiPai = new TaoPaiPai();
                taoPaiPai.setInfo(1000, 100000, _c.info.hpFull / 50, 0, 5);
                taoPaiPai.setLocation(this);
            } else {
                appearTaoPaiPai();
            }
        }
    }

    public void appearTaoPaiPai() {
        TaoPaiPai taoPaiPai = new TaoPaiPai();
        taoPaiPai.setInfo(50000, 100000, 100, 5, 10);
        taoPaiPai.setLocation(this);
    }

}
