/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _HunrProvision;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class MainConfig {

    private static final String path = "Config/config.ini";

    public int percentEXP = 1;
    public int percentRewardMob = 1;
    public int percentRewardBoss = 1;

    public int maxLength = 1;
    public float[] percentCombineShow = new float[]{};
    public float[] percentCombineReal = new float[]{};
    public long[] goldCombine = new long[]{};

    public int rewardCold_DoThan;
    public int rewardCold_DoCuoi;
    public int rewardCold_HuyDiet;

    public static MainConfig instance;

    public static MainConfig gI() {
        if (instance == null) {
            instance = new MainConfig();
        }
        return instance;
    }

    public String loadStr(String key) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            for (String line : lines) {
                if (line.startsWith(key + ":")) {
                    String value = line.replace(key + ":", "").trim();
                    return value;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String[] loadArrayStr(String key) {
        String value = loadStr(key);
        if (value.equals("")) {
            return new String[0];
        }
        try {
            return value.split(";");
        } catch (Exception e) {
            return new String[0];
        }
    }

    public int loadInt(String key) {
        String value = loadStr(key);
        if (value.equals("")) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public float loadFloat(String key) {
        String value = loadStr(key);
        if (value.equals("")) {
            return 0;
        }
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public long loadLong(String key) {
        String value = loadStr(key);
        if (value.equals("")) {
            return 0;
        }
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public void load() {
        try {
            percentEXP = loadInt("exp");
            percentRewardMob = loadInt("mobReward");
            percentRewardBoss = loadInt("bossReward");
            maxLength = loadInt("SoSao");
            percentCombineShow = new float[maxLength];
            percentCombineReal = new float[maxLength];
            goldCombine = new long[maxLength];
            for (int i = 1; i <= maxLength; i++) {
                var x = loadArrayStr("Sao_" + i);
                percentCombineShow[i - 1] = Float.parseFloat(x[0]);
                percentCombineReal[i - 1] = Float.parseFloat(x[1]);
                goldCombine[i - 1] = Long.parseLong(x[2]);
            }

            rewardCold_DoThan = loadInt("dothancold");
            rewardCold_DoCuoi = loadInt("docuoicold");
            rewardCold_HuyDiet = loadInt("huydietcold");

            System.out.println("exp: " + percentEXP);
            System.out.println("mobReward: " + percentRewardMob);
            System.out.println("bossReward: " + percentRewardBoss);
            System.out.println("SoSao: " + maxLength);

            for (int i = 0; i < maxLength; i++) {
                System.out.println("Sao_" + (i + 1) + ": " + percentCombineShow[i] + "; " + percentCombineReal[i] + "; " + goldCombine[i]);
            }

            System.out.println("dothancold: " + rewardCold_DoThan);
            System.out.println("docuoicold: " + rewardCold_DoCuoi);
            System.out.println("huydietcold: " + rewardCold_HuyDiet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
