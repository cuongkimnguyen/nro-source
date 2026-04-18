package com.ngocrong.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "nr_bo_mong_nhiem_vu_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoMongNhiemVuConfigData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @Column(name = "loai_nv")
    public Integer loaiNv;

    @Column(name = "do_kho")
    public Integer doKho;

    @Column(name = "yeu_cau_min")
    public Integer yeuCauMin;

    @Column(name = "yeu_cau_max")
    public Integer yeuCauMax;

    @Column(name = "diem_min")
    public Integer diemMin;

    @Column(name = "diem_max")
    public Integer diemMax;

    @Column(name = "active")
    public Integer active;
}

