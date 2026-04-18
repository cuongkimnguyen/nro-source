package com.ngocrong.server;

import com.ngocrong.data.DropRateData;
import com.ngocrong.repository.DropRateRepository;
import com.ngocrong.repository.GameRepository;
import com.ngocrong.util.Utils;

import java.util.Optional;

public class DropRateService {

    private static final int CONFIG_ID = 1;
    private static int mobRate = 100;
    private static int bossRate = 100;
    private static int tilePhoiMob = 95;
    private static int tilePhoiBoss = 95;
    private static int petFinal4s = 2000;
    private static int petFinal5s = 3000;
    private static int petFinal6s = 5000;
    private static int petFinal7s = 30000;
    private static int petFinal8s = 50000;
    private static int petDivine = 50000;
    private static int petDestroy = 50000;

    private static int[] fishBiteRates = {33, 66};
    private static int[] fishTypeRates = {30, 30, 5, 30};
    private static int[] fishSharkRates = {70, 30};
    private static int[] fishItemRates = {30, 30, 5, 30, 5};

    private static DropRateRepository repo() {
        return GameRepository.getInstance().dropRateRepository;
    }

    public static void load() {
        try {
            Optional<DropRateData> data = repo().findById(CONFIG_ID);
            if (data.isPresent()) {
                mobRate = Optional.ofNullable(data.get().getMobRate()).orElse(100);
                bossRate = Optional.ofNullable(data.get().getBossRate()).orElse(100);
                tilePhoiMob = Optional.ofNullable(data.get().getTilephoiMob()).orElse(95);
                tilePhoiBoss = Optional.ofNullable(data.get().getTilephoiBoss()).orElse(95);
                petFinal4s = Optional.ofNullable(data.get().getPetFinal4s()).orElse(2000);
                petFinal5s = Optional.ofNullable(data.get().getPetFinal5s()).orElse(3000);
                petFinal6s = Optional.ofNullable(data.get().getPetFinal6s()).orElse(5000);
                petFinal7s = Optional.ofNullable(data.get().getPetFinal7s()).orElse(30000);
                petFinal8s = Optional.ofNullable(data.get().getPetFinal8s()).orElse(50000);
                petDivine = Optional.ofNullable(data.get().getPetDivine()).orElse(50000);
                petDestroy = Optional.ofNullable(data.get().getPetDestroy()).orElse(50000);
                parseRates(data.get().getDropratefish1(), fishBiteRates);
                parseRates(data.get().getDropratefish2(), fishTypeRates);
                parseRates(data.get().getDropratefish3(), fishSharkRates);
                parseRates(data.get().getDropratefish4(), fishItemRates);
            }
        } catch (Exception ignored) {
        }
    }

    public static void update(int mob, int boss, int phoiMob, int phoiBoss) {
        DropRateData cfg = repo().findById(CONFIG_ID).orElse(new DropRateData());
        cfg.setId(CONFIG_ID);
        cfg.setMobRate(mob);
        cfg.setBossRate(boss);
        cfg.setTilephoiMob(phoiMob);
        cfg.setTilephoiBoss(phoiBoss);
        cfg.setPetFinal4s(petFinal4s);
        cfg.setPetFinal5s(petFinal5s);
        cfg.setPetFinal6s(petFinal6s);
        cfg.setPetFinal7s(petFinal7s);
        cfg.setPetFinal8s(petFinal8s);
        cfg.setPetDivine(petDivine);
        cfg.setPetDestroy(petDestroy);
        cfg.setDropratefish1(joinRates(fishBiteRates));
        cfg.setDropratefish2(joinRates(fishTypeRates));
        cfg.setDropratefish3(joinRates(fishSharkRates));
        cfg.setDropratefish4(joinRates(fishItemRates));
        repo().save(cfg);
        mobRate = mob;
        bossRate = boss;
        tilePhoiMob = phoiMob;
        tilePhoiBoss = phoiBoss;
    }

    public static void updatePet(int rate4s, int rate5s, int rate6s, int rate7s, int rate8s, int divine, int destroy) {
        DropRateData cfg = repo().findById(CONFIG_ID).orElse(new DropRateData());
        cfg.setId(CONFIG_ID);
        cfg.setMobRate(mobRate);
        cfg.setBossRate(bossRate);
        cfg.setTilephoiMob(tilePhoiMob);
        cfg.setTilephoiBoss(tilePhoiBoss);
        cfg.setPetFinal4s(rate4s);
        cfg.setPetFinal5s(rate5s);
        cfg.setPetFinal6s(rate6s);
        cfg.setPetFinal7s(rate7s);
        cfg.setPetFinal8s(rate8s);
        cfg.setPetDivine(divine);
        cfg.setPetDestroy(destroy);
        cfg.setDropratefish1(joinRates(fishBiteRates));
        cfg.setDropratefish2(joinRates(fishTypeRates));
        cfg.setDropratefish3(joinRates(fishSharkRates));
        cfg.setDropratefish4(joinRates(fishItemRates));
        repo().save(cfg);
        petFinal4s = rate4s;
        petFinal5s = rate5s;
        petFinal6s = rate6s;
        petFinal7s = rate7s;
        petFinal8s = rate8s;
        petDivine = divine;
        petDestroy = destroy;
    }

    public static void updateFish(int[] fish1, int[] fish2, int[] fish3, int[] fish4) {
        DropRateData cfg = repo().findById(CONFIG_ID).orElse(new DropRateData());
        cfg.setId(CONFIG_ID);
        cfg.setMobRate(mobRate);
        cfg.setBossRate(bossRate);
        cfg.setTilephoiMob(tilePhoiMob);
        cfg.setTilephoiBoss(tilePhoiBoss);
        cfg.setPetFinal4s(petFinal4s);
        cfg.setPetFinal5s(petFinal5s);
        cfg.setPetFinal6s(petFinal6s);
        cfg.setPetFinal7s(petFinal7s);
        cfg.setPetFinal8s(petFinal8s);
        cfg.setPetDivine(petDivine);
        cfg.setPetDestroy(petDestroy);
        if (fish1 != null) fishBiteRates = fish1;
        if (fish2 != null) fishTypeRates = fish2;
        if (fish3 != null) fishSharkRates = fish3;
        if (fish4 != null) fishItemRates = fish4;
        cfg.setDropratefish1(joinRates(fishBiteRates));
        cfg.setDropratefish2(joinRates(fishTypeRates));
        cfg.setDropratefish3(joinRates(fishSharkRates));
        cfg.setDropratefish4(joinRates(fishItemRates));
        repo().save(cfg);
    }

    public static int getMobRate() {
        return mobRate;
    }

    public static int getBossRate() {
        return bossRate;
    }

    public static int getTilePhoiMob() {
        return tilePhoiMob;
    }

    public static int getTilePhoiBoss() {
        return tilePhoiBoss;
    }

    public static boolean shouldDropMobItem() {
        return Utils.isTrue(mobRate, 100);
    }

    public static boolean shouldDropBossItem() {
        return Utils.isTrue(bossRate, 100);
    }

    public static int getPetFinal4s() {
        return petFinal4s;
    }

    public static int getPetFinal5s() {
        return petFinal5s;
    }

    public static int getPetFinal6s() {
        return petFinal6s;
    }

    public static int getPetFinal7s() {
        return petFinal7s;
    }

    public static int getPetFinal8s() {
        return petFinal8s;
    }

    public static int getPetDivine() {
        return petDivine;
    }

    public static int getPetDestroy() {
        return petDestroy;
    }

    public static int getFishBiteRate(boolean vip) {
        return fishBiteRates[vip ? 1 : 0];
    }

    public static int[] getFishTypeRates() {
        return fishTypeRates;
    }

    public static int[] getFishSharkRates() {
        return fishSharkRates;
    }

    public static int[] getFishItemRates() {
        return fishItemRates;
    }

    private static void parseRates(String src, int[] dest) {
        try {
            if (src != null) {
                String[] arr = src.split(",");
                for (int i = 0; i < Math.min(arr.length, dest.length); i++) {
                    dest[i] = Integer.parseInt(arr[i].trim());
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static String joinRates(int[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(arr[i]);
        }
        return sb.toString();
    }
}
