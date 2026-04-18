package com.ngocrong.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "nr_bo_mong_moc_diem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoMongMocDiemData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @Column(name = "diem_can_thiet")
    public Integer diemCanThiet;

    @Column(name = "item_id_1", columnDefinition = "TEXT")
    public String itemId1;

    @Column(name = "item_id_2", columnDefinition = "TEXT")
    public String itemId2;

    @Column(name = "item_id_3", columnDefinition = "TEXT")
    public String itemId3;

    @Column(name = "item_id_4", columnDefinition = "TEXT")
    public String itemId4;

    @Column(name = "active")
    public Integer active;

    @Column(name = "sort_order")
    public Integer sortOrder;
}
