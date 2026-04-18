package com.ngocrong.model;

import com.ngocrong.network.Message;
import com.ngocrong.network.Service;
import lombok.Data;
import org.apache.log4j.Logger;

import java.io.IOException;

@Data
public class InputDialog {

    private static final Logger logger = Logger.getLogger(InputDialog.class);

    private String title;
    private byte type;
    private TextField[] fields;
    private Service service;
    private byte index;

    public InputDialog(byte type, String title, TextField... fields) {
        this.title = title;
        this.type = type;
        this.fields = fields;
    }

    public void show() {
        service.openTextBox(this);
    }

    public InputDialog next() {
        index++;
        return this;
    }

    public boolean first() {
        if (fields.length == 0) {
            return false;
        }
        index = 0;
        return true;
    }

    public boolean last() {
        if (fields.length == 0) {
            return false;
        }
        index = (byte) (fields.length - 1);
        return true;
    }

    public String getText() {
        String text = fields[index].getText();
        next();
        return text;
    }

    public long getLong() {
        try {
            String text = getText();
            long value = Long.parseLong(text);
            return value;
        } catch (NumberFormatException e) {
            
            throw e;
        }
    }

    public int getInt() {
        return ((Long) getLong()).intValue();
    }

    public short getShort() {
        return ((Long) getLong()).shortValue();
    }

    public byte getByte() {
        return ((Long) getLong()).byteValue();
    }

    public String getText(byte index) {
        return fields[index].getText();
    }

    public boolean input(Message ms) {
        try {
            byte size = ms.reader().readByte();
            for (int i = 0; i < size; i++) {
                TextField field = fields[i];
                field.setText(ms.reader().readUTF());
            }
            return true;
        } catch (IOException ex) {
            
            logger.error("input err");
            return false;
        }
    }
}
