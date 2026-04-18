//package com.ngocrong.map;
//
//import com.ngocrong.map.tzone.Arena;
//import com.ngocrong.consts.MapName;
//import com.ngocrong.user.Player;
//import com.ngocrong.util.Utils;
//import lombok.Data;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Data
//public class Round {
//
//    private ArrayList<Integer> participants;
//    private int round;
//    private List<Arena> zones;
//    private MartialArtsFestival martialArtsFestival;
//    private TMap map;
//
//    public Round(int round, MartialArtsFestival martialArtsFestival) {
//        this.round = round;
//        this.martialArtsFestival = martialArtsFestival;
//        this.participants = new ArrayList<>();
//        this.map = MapManager.getInstance().getMap(MapName.DAI_HOI_VO_THUAT);
//    }
//
//    public void addParticipant(int id) {
//        synchronized (participants) {
//            participants.add(id);
//        }
//    }
//
//    public List<Player> getList() {
//        ArrayList<Player> list = new ArrayList<>();
//        synchronized (participants) {
//            for (int id : participants) {
//                Player _c = map.findCharInMap(id);
//                if (_c != null) {
//                    list.add(_c);
//                }
//            }
//        }
//        return list;
//    }
//
//    public void startFighting() {
//        System.out.println("start fighting");
//        List<Player> list = getList();
//        if (list.size() <= 1) {
//            if (list.size() == 1) {
//                Player _c = list.get(0);
//                int feeType = martialArtsFestival.getFeeType();
//                int fee = martialArtsFestival.getFee();
//                if (feeType == 0) {
//                    _c.addGold(fee);
//                    _c.service.serverMessage(String.format("Bạn vừa nhận thưởng %d vàng", fee));
//                } else {
//                    _c.addDiamondLock(fee);
//                    _c.service.serverMessage(String.format("Bạn vừa nhận thưởng %d ngọc", fee));
//                }
//                _c.service.serverMessage("Bạn đã thắng giải đấu này");
//            }
//            martialArtsFestival.close();
//            return;
//        }
//        this.zones = new ArrayList<>();
//        TMap map = MapManager.getInstance().getMap(MapName.DAU_TRUONG);
//        while (list.size() >= 2) {
//            Arena arena = new Arena(map, map.autoIncrease++, martialArtsFestival);
//            int index = Utils.nextInt(list.size());
//            Player p1 = list.get(index);
//            list.remove(index);
//            index = Utils.nextInt(list.size());
//            Player p2 = list.get(index);
//            list.remove(index);
//            p1.setX(Arena.POSITION[0][0]);
//            p1.setY(Arena.POSITION[0][1]);
//            p2.setX(Arena.POSITION[1][0]);
//            p2.setY(Arena.POSITION[1][1]);
//            p1.zone.leave(p1);
//            arena.setP1(p1);
//            arena.setP2(p2);
//            arena.enter(p1);
//            p2.zone.leave(p2);
//            arena.enter(p2);
//            zones.add(arena);
//            //map.addZone(arena);
//        }
//        if (list.size() == 1) {
//            Player p = list.get(0);
//            int feeType = martialArtsFestival.getFeeType();
//            int fee = martialArtsFestival.getFee();
//            if (feeType == 0) {
//                p.addGold(fee);
//                p.service.serverMessage(String.format("Bạn vừa nhận thưởng %d vàng", fee));
//            } else {
//                p.addDiamondLock(fee);
//                p.service.serverMessage(String.format("Bạn vừa nhận thưởng %d ngọc", fee));
//            }
//            p.service.serverMessage("Bạn đã chiến thắng vòng này, do đối thủ đã bỏ cuộc");
//            martialArtsFestival.getCurrentRound().addParticipant(p.id);
//        }
//    }
//}
