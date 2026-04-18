//package com.ngocrong.map.tzone;
//
//import com.ngocrong.consts.CMDPk;
//import com.ngocrong.consts.MapName;
//import com.ngocrong.map.MapManager;
//import com.ngocrong.map.MartialArtsFestival;
//import com.ngocrong.map.TMap;
//import com.ngocrong.user.Player;
//import com.ngocrong.user.Info;
//import lombok.Data;
//
//@Data
//public class Arena extends Zone {
//
//    public static final short[][] POSITION = {{265, 312}, {508, 312}};
//
//    private Player p1, p2;
//    private MartialArtsFestival martialArtsFestival;
//    private int countDown;
//    private long last;
//    private boolean started;
////    private TrongTai trongTai;
//    private int countDownToStart;
//    private boolean isFinish;
//    private String winnerName;
//
//    public Arena(TMap map, int zoneId, MartialArtsFestival martialArtsFestival) {
//        super(map, zoneId);
//        this.martialArtsFestival = martialArtsFestival;
//        this.countDown = 60;
//        this.countDownToStart = 15;
//        this.trongTai = new TrongTai();
//        this.trongTai.setX((short) 388);
//        this.trongTai.setY((short) 312);
//        enter(this.trongTai);
//    }
//
//    public void close() {
//        running = false;
//        TMap map = MapManager.getInstance().getMap(MapName.DAI_HOI_VO_THUAT);
//        if (p1.isDead()) {
//            p1.returnTownFromDead();
//        } else {
//            int zoneID = map.getZoneID();
//            leave(p1);
//            map.enterZone(p1, zoneID);
//        }
//        if (p2.isDead()) {
//            p2.returnTownFromDead();
//        } else {
//            int zoneID = map.getZoneID();
//            leave(p2);
//            map.enterZone(p2, zoneID);
//        }
//        System.out.println("arena close");
//    }
//
//    public void checkResult() {
//        if (!isFinish) {
//            if (p1.isDead() && !p2.isDead()) {
//                p2.service.serverMessage("Đối thủ đã kiệt sức, bạn đã thắng");
//                winner(p2);
//            }
//            if (!p1.isDead() && p2.isDead()) {
//                p1.service.serverMessage("Đối thủ đã kiệt sức, bạn đã thắng");
//                winner(p1);
//            }
//            if (p1.zone != this) {
//                p2.service.serverMessage("Đối thủ đã bỏ chạy, bạn đã thắng");
//                winner(p2);
//            }
//            if (p2.zone != this) {
//                p1.service.serverMessage("Đối thủ đã bỏ chạy, bạn đã thắng");
//                winner(p1);
//            }
//        }
//    }
//
//    public void winner(Player p) {
//        int fee = martialArtsFestival.getFee();
//        int feeType = martialArtsFestival.getFeeType();
//        if (feeType == 0) {
//            p.addGold(fee);
//            p.service.serverMessage(String.format("Bạn vừa nhận thưởng %d vàng", fee));
//        } else {
//            p.addDiamondLock(fee);
//            p.service.serverMessage(String.format("Bạn vừa nhận thưởng %d ngọc", fee));
//        }
//        martialArtsFestival.getCurrentRound().addParticipant(p.id);
//        this.winnerName = p.name;
//        this.isFinish = true;
//        this.countDown = 5;
//        enter(trongTai);
//    }
//
//    public void fight() {
//        started = true;
//        leave(trongTai);
//        p1.info.recovery(Info.ALL, 100, true);
//        p2.info.recovery(Info.ALL, 100, true);
//        p1.setX(POSITION[0][0]);
//        p1.setY(POSITION[0][1]);
//        service.setPosition(p1, (byte) 0);
//        p2.setX(POSITION[1][0]);
//        p2.setY(POSITION[1][1]);
//        service.setPosition(p2, (byte) 0);
//        p1.setCommandPK(CMDPk.DAI_HOI_VO_THUAT);
//        p2.setCommandPK(CMDPk.DAI_HOI_VO_THUAT);
//        p1.testCharId = p2.id;
//        p2.testCharId = p1.id;
//        p1.setTypePK((byte) 3);
//        p2.setTypePK((byte) 3);
//
//    }
//
//    @Override
//    public void update() {
//        super.update();
//        if (trongTai != null && p1 != null && p2 != null) {
//            long now = System.currentTimeMillis();
//            if (now - last >= 1000) {
//                last = now;
//                if (countDownToStart > 0) {
//                    countDownToStart--;
//                    if (countDownToStart == 14) {
//                        service.chat(trongTai, String.format("Trận đấu giữa %s và %s sắp diễn ra", p1.name, p2.name));
//                    } else if (countDownToStart == 12) {
//                        service.chat(trongTai, "Xin quý vị khán giả cho 1 tràng pháo tay cổ vũ cho 2 đấu thủ nào");
//                    } else if (countDownToStart == 10) {
//                        service.chat(trongTai, "Mọi người hãy ổn định chỗ ngồi, trận đấu sẽ bắt đầu sau 3 giây nữa");
//                    } else if (countDownToStart == 8) {
//                        service.chat(trongTai, "3");
//                    } else if (countDownToStart == 6) {
//                        service.chat(trongTai, "2");
//                    } else if (countDownToStart == 4) {
//                        service.chat(trongTai, "1");
//                    } else if (countDownToStart == 2) {
//                        service.chat(trongTai, "Trận đấu bắt đầu");
//                    } else if (countDownToStart == 0) {
//                        fight();
//                    }
//                }
//                if (started) {
//                    if (countDown > 0) {
//                        countDown--;
//                        if (countDown == 0) {
//                            if (!isFinish) {
//                                p1.service.serverMessage("Bạn đã chiến thắng");
//                                winner(p1);
//                            } else {
//                                if (countDown == 4) {
//                                    service.chat(trongTai, "Trận đấu đã kết thúc");
//                                }
//                                if (countDown == 2) {
//                                    service.chat(trongTai, String.format("Đối thủ %s đã dành chiến thắng", winnerName));
//                                }
//                                close();
//                            }
//                            return;
//                        }
//                        checkResult();
//                    }
//                    System.out.println("arena countDown: " + countDown);
//                }
//
//            }
//        }
//    }
//}
