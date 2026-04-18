package com.ngocrong.map;

import com.ngocrong.data.ConsignmentItemData;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.shop.Consignment;
import com.ngocrong.shop.ConsignmentItem;
import com.ngocrong.shop.ConsignmentItemStatus;
import com.ngocrong.user.Player;
import com.ngocrong.util.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MartialCongress {

    private static final Logger logger = Logger.getLogger(MartialCongress.class);
    private static MartialCongress instance;
    public final Map<Integer, Integer> currentLevelDHVT23;
    public final Map<Integer, Integer> countStarDHVT23;
    public final List<Integer> playersNhanRuongGoDHVT23;
    public final List<Integer> playersOpenRuongGo;
    public static long[] FEE = {100000000L, 200000000L, 400000000L, 800000000L};
    public static long MIN_FEE = 100000000L;
    public static long MAX_FEE = 800000000L;

    public MartialCongress() {
        this.playersOpenRuongGo = new ArrayList<>();
        this.currentLevelDHVT23 = new HashMap<>();
        this.countStarDHVT23 = new HashMap<>();
        this.playersNhanRuongGoDHVT23 = new ArrayList<>();
    }

    public int getCurrentLevelDHVT23(Integer playerId) {
        Object o = this.currentLevelDHVT23.get(playerId);
        if (o == null) {
            return 0;
        }
        return Integer.parseInt(o.toString());
    }

    public int getCountStar(Integer playerId) {
        Object o = this.countStarDHVT23.get(playerId);
        if (o == null) {
            return 0;
        }
        return Integer.parseInt(o.toString());
    }

    public void savePlayerNhanRuongGoDHVT23(Integer playerId) {
        if (this.playersNhanRuongGoDHVT23.contains(playerId)) {
            return;
        }
        this.playersNhanRuongGoDHVT23.add(playerId);
    }

    public boolean isNhanRuongGoDHVT23(Integer playerId) {
        return this.playersNhanRuongGoDHVT23.contains(playerId);
    }

    public void savePlayerOpenRuongGoDHVT23(Integer playerId) {
        if (this.playersOpenRuongGo.contains(playerId)) {
            return;
        }
        this.playersOpenRuongGo.add(playerId);
    }

    public boolean isOpenRuongGo(Integer playerId) {
        return this.playersOpenRuongGo.contains(playerId);
    }

}
