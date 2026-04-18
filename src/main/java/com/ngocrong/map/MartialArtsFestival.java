//package com.ngocrong.map;
//
//import com.ngocrong.user.Player;
//import com.ngocrong.util.Utils;
//import com.ngocrong.consts.MapName;
//import lombok.Data;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Data
//public class MartialArtsFestival implements Runnable {
//
//    private Round currentRound;
//    private String name;
//    private long maxRequiredPower;
//    private int round;
//    private int registrationTimeoutCountdown;
//    private int countdownTimeStarts;
//    private boolean running;
//    private boolean started, fighting;
//    private int fee;
//    private byte feeType;
//    private ArrayList<Integer> registrationList;
//    private String strTimeStart;
//    private long lastNotify;
//
//    public MartialArtsFestival(String name, long maxRequiredPower, int fee, byte feeType, String strTimeStart) {
//        this.name = name;
//        this.maxRequiredPower = maxRequiredPower;
//        this.fee = fee;
//        this.feeType = feeType;
//        this.strTimeStart = strTimeStart;
//        this.round = 1;
//        this.registrationTimeoutCountdown = 1200;
//        this.registrationList = new ArrayList<>();
//        this.running = true;
//    }
//
//    public void nextRound() {
//        currentRound = new Round(round++, this);
//    }
//
//    public void registration(int id) {
//        if (!checkExist(id)) {
//            synchronized (registrationList) {
//                registrationList.add(id);
//            }
//        }
//    }
//
//    public List<Player> getList() {
//        ArrayList<Player> list = new ArrayList<>();
//        TMap map = MapManager.getInstance().getMap(MapName.DAI_HOI_VO_THUAT);
//        List<Integer> participants = currentRound.getParticipants();
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
//    public boolean checkExist(int id) {
//        synchronized (registrationList) {
//            return registrationList.indexOf(id) != -1;
//        }
//    }
//
//    public void cancelRegistration(int id) {
//        synchronized (registrationList) {
//            registrationList.remove(registrationList.indexOf(id));
//        }
//    }
//
//    public void createRound() {
//        nextRound();
//        for (int id : registrationList) {
//            currentRound.addParticipant(id);
//        }
//        System.out.println("create round");
//    }
//
//    public void startFighting() {
//        started = true;
//        Round current = currentRound;
//        nextRound();
//        current.startFighting();
//    }
//
//    public void update() {
//        if (registrationTimeoutCountdown > 0) {
//            registrationTimeoutCountdown--;
//            System.out.println("registrationTimeoutCountdown: " + registrationTimeoutCountdown);
//            if (registrationTimeoutCountdown == 0) {
//                countdownTimeStarts = 600;
//                createRound();
//            }
//        }
//        if (countdownTimeStarts > 0) {
//            long now = System.currentTimeMillis();
//            if (now - lastNotify >= 30000L) {
//                lastNotify = now;
//                sendNotify(String.format("Trận chiến của bạn sẽ bắt đầu sau %s nữa", Utils.timeAgo(countdownTimeStarts)));
//            }
//            countdownTimeStarts--;
//            System.out.println("countdownTimeStarts: " + countdownTimeStarts);
//            if (countdownTimeStarts == 0) {
//                startFighting();
//                countdownTimeStarts = 120;
//            }
//        }
//    }
//
//    public void sendNotify(String text) {
//        List<Player> list = getList();
//        for (Player _c : list) {
//            _c.service.serverMessage(text);
//        }
//    }
//
//    public void close() {
//        System.out.println("close");
//        running = false;
//        MapManager.getInstance().martialArtsFestival = null;
//    }
//
//    @Override
//    public void run() {
//        while (running) {
//            long delay = 1000;
//            try {
//                long l1 = System.currentTimeMillis();
//                update();
//                long l2 = System.currentTimeMillis();
//                long l3 = l2 - l1;
//                if (l3 > delay) {
//                    continue;
//                }
//                Thread.sleep(delay - l3);
//            } catch (Exception e) { 
//                e.printStackTrace();
//            }
//        }
//    }
//}
