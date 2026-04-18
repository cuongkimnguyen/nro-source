package com.ngocrong.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "nr_bo_mong_nhiem_vu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoMongNhiemVuData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @Column(name = "player_id")
    public Integer playerId;

    @Column(name = "loai_nv")
    public Integer loaiNv;

    @Column(name = "do_kho")
    public Integer doKho;

    @Column(name = "yeu_cau")
    public Integer yeuCau;

    @Column(name = "tien_do")
    public Integer tienDo;

    @Column(name = "diem_thuong")
    public Integer diemThuong;

    @Column(name = "trang_thai")
    public Integer trangThai;

    @Column(name = "created_at")
    public Long createdAt;

    @Column(name = "expired_at")
    public Long expiredAt;
}

