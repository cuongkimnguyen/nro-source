package com.ngocrong.model;

import com.ngocrong.lib.CaptchaGenerate;
import com.ngocrong.util.Utils;
import lombok.Data;

@Data
public class Captcha {

    private byte zoomLevel;
    private byte[] data;
    private String captcha;
    private String keyStr;

    public void generate() {
        generateKey();
        generateCaptcha();
        generateImage();
    }

    private void generateKey() {
        byte[] array = new byte[5];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) Utils.nextInt(97, 122);
        }
        keyStr = new String(array);
    }

    private void generateCaptcha() {
        byte[] array = new byte[5];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) keyStr.charAt(Utils.nextInt(keyStr.length()));
        }
        captcha = new String(array);
    }

    private void generateImage() {
        data = CaptchaGenerate.generateImage(captcha, zoomLevel);
    }

    public void input(char ch) {

    }

}
