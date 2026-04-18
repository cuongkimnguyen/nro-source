package com.ngocrong.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "nr_bo_mong_boss_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoMongBossConfigData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @Column(name = "do_kho")
    public Integer doKho;

    @Column(name = "boss_class_name")
    public String bossClassName;

    @Column(name = "active")
    public Integer active;
}

