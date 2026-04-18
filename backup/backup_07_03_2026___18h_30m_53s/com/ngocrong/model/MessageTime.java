package com.ngocrong.model;

import lombok.Data;

@Data
public class MessageTime {

    public static final byte DOANH_TRAI = 0;
    public static final byte HANG_KHO_BAU = 1;
    public static final byte CAU_CA = 2;

    private byte id;
    private String text;
    private short time;

    public MessageTime(byte id, String text, short time) {
        this.id = id;
        this.text = text;
        this.time = time;
    }

    public void update() {
        this.time--;
    }

}
