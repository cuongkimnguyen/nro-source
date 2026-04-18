package com.ngocrong.model;

import lombok.Data;

@Data
public class TextField {

    public static final byte INPUT_TYPE_NUMERIC = 0;
    public static final byte INPUT_TYPE_ANY = 1;
    public static final byte INPUT_TYPE_PASSWORD = 2;

    private String title;
    private byte type;
    private String text;

    public TextField(String title, byte type) {
        this.title = title;
        this.type = type;
    }

    public TextField(String text) {
        this.title = text;
        this.type = INPUT_TYPE_ANY;
    }

}
