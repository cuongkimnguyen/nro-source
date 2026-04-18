package com.ngocrong.lib;

import lombok.Data;

@Data
public class Menu {

    public static final byte MENU = 0;
    public static final byte MUA_CAI_TRANG = 100;
    public static final byte REPORT = 101;

    private int playerId;
    private String caption;
    private String caption2;
    private byte type;

    public Menu(int playerId, String caption, String caption2, byte type) {
        this.playerId = playerId;
        this.caption = caption;
        this.caption2 = caption2;
        this.type = type;
    }
}
