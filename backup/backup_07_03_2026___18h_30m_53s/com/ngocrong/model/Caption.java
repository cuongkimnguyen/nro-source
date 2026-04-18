package com.ngocrong.model;

import java.util.ArrayList;

public class Caption {

    private static ArrayList<String> TRAI_DAT = new ArrayList<>();
    private static ArrayList<String> NAMEC = new ArrayList<>();
    private static ArrayList<String> XAYDA = new ArrayList<>();

    public static void addCaption(byte planet, String caption) {
        switch (planet) {
            case 0:
                TRAI_DAT.add(caption);
                break;
            case 1:
                NAMEC.add(caption);
                break;
            case 2:
                XAYDA.add(caption);
                break;
        }
    }

    public static ArrayList<String> getCaption(byte planet) {
        switch (planet) {
            case 0:
                return TRAI_DAT;
            case 1:
                return NAMEC;
            case 2:
                return XAYDA;
            default:
                return null;
        }
    }
}
