package com.ngocrong.combine;

import lombok.Data;

@Data
public class ChuyenHoa extends Combine {

    public static byte BANG_VANG = 0;
    public static byte BANG_NGOC = 1;

    private byte type;

    public ChuyenHoa() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vào hành trang").append("\n");
        sb.append("Chọn trang bị gốc").append("\n");
        sb.append("(Áo,quần,găng,giày hoặc rada)").append("\n");
        sb.append("từ cấp [+4] trở lên").append("\n");
        sb.append("Chọn tiếp trang bị mới").append("\n");
        sb.append("chưa nâng cấp cần nhập thể").append("\n");
        sb.append("sau đó chọn 'Nâng cấp'");
        setInfo(sb.toString());

        setInfo2("Lưu ý trang bị mới phải hơn trang bị gốc 1 bậc");
    }

    @Override
    public void confirm() {

    }

    @Override
    public void combine() {

    }

    @Override
    public void showTab() {
        player.service.combine((byte) 0, this, (short) -1, (short) -1);
    }
}
