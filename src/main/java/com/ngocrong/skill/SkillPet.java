package com.ngocrong.skill;

import com.ngocrong.util.Utils;

import java.util.ArrayList;

public class SkillPet {

    public static ArrayList<SkillPet> list;

    public int id;
    public String name;
    public long powerRequire;
    public byte[] skills;

    public String getMoreInfo() {
        return String.format("Cần %s sức mạnh để mở", Utils.formatNumber(powerRequire));
    }
}
