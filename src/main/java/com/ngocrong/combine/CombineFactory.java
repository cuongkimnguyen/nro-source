package com.ngocrong.combine;

import com.ngocrong.NQMP.TamThangBa.TachVatPham;
import static com.ngocrong.combine.CombineType.AP_LINH_THU;

import static com.ngocrong.combine.CombineType.KICH_HOAT;
import static com.ngocrong.combine.CombineType.NANG_CAP;
import static com.ngocrong.combine.CombineType.PHA_LE_HOA;
import static com.ngocrong.combine.CombineType.TACH_DO_KICH_HOAT;
import static com.ngocrong.combine.CombineType.Tach_Vat_Pham;
import com.ngocrong.combine.LinhThu.ApLinhThu;
import com.ngocrong.combine.LinhThu.NangCapLinhThu;
import com.ngocrong.combine.LinhThu.NangBacLinhThu;
import com.ngocrong.combine.LinhThu.NangChiSoLinhThu;
import com.ngocrong.combine.LinhThu.XoaChiSoLinhThu;

public class CombineFactory {

    public static Combine getCombine(CombineType combineType) {
        switch (combineType) {
            case CHUYEN_HOA:
                return new ChuyenHoa();

            case NANG_CAP:
                return new NangCap();
            case NANG_CAP_2:
                return new NangCap_2();
            case EP_PHA_LE:
                return new EpPhaLe();

            case NANG_PORATA:
                return new NangPorata();

            case NANG_PORATA_CAP_3:
                return new NangPorata3();

            case NANG_OPTION_PORATA:
                return new NangOptionPorata();

            case NANG_OPTION_PORATA_CAP_3:
                return new NangOptionPorata3();

            case NHAP_DA:
                return new NhapDa();

            case NHAP_NGOC:
                return new NhapNgoc();

            case PHA_LE_HOA:
                return new PhaLeHoa();
            case HUY_DIET:
                return new DoiDoHuyDiet();
            case KICH_HOAT:
                return new DoiDoKichHoat();
            case GHEP_DA:
                return new GhepDaNangCap();
            case TACH_DO:
                return new TachDoThanLinh();
            case TACH_DO_KICH_HOAT:
                return new TachDoKichHoat();
            case Tach_Vat_Pham:
                return new TachVatPham();
            case Nang_Item_De_Tu:
                return new NangItemDeTu();
            case CAPSULE_VIPPRO:
                return new CapsuleVipPro();
            case CAPSULE_VIPPRO_OPTION:
                return new CapsuleVipProOption();
            case AP_LINH_THU:
                return new ApLinhThu();

            case NANG_CAP_LINH_THU:
                return new NangCapLinhThu();
            case NANG_BAC_LINH_THU:
                return new NangBacLinhThu();
            case NANG_CHI_SO_LINH_THU:
                return new NangChiSoLinhThu();
            case XOA_CHI_SO_LINH_THU:
                return new XoaChiSoLinhThu();
            case NANG_CAP_CHAN_THIEN_TU:
                return new NangCapChanThienTu();
            default:
                throw new IllegalArgumentException("This combine type is unsupported");
        }
    }
}
