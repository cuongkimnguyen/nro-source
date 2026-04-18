package com.ngocrong.effect;

public class EffectChar {

    public EffectChar(byte templateId, int timeStart, int timeLenght, short param) {
        this.template = EffectChar.effTemplates[(int) templateId];
        this.timeStart = timeStart;
        this.timeLenght = timeLenght / 1000;
        this.param = param;
    }

    public static EffectTemplate[] effTemplates;
    public static byte EFF_ME;
    public static byte EFF_FRIEND = 1;
    public int timeStart;
    public int timeLenght;
    public short param;
    public EffectTemplate template;
}
