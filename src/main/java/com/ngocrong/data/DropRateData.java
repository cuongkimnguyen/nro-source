package com.ngocrong.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nr_drop_rate")
public class DropRateData {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "mob_rate")
    private Integer mobRate;

    @Column(name = "boss_rate")
    private Integer bossRate;

    @Column(name = "tilephoi_mob")
    private Integer tilephoiMob;

    @Column(name = "tilephoi_boss")
    private Integer tilephoiBoss;

    @Column(name = "pet_final_4s")
    private Integer petFinal4s;

    @Column(name = "pet_final_5s")
    private Integer petFinal5s;

    @Column(name = "pet_final_6s")
    private Integer petFinal6s;

    @Column(name = "pet_final_7s")
    private Integer petFinal7s;

    @Column(name = "pet_final_8s")
    private Integer petFinal8s;

    @Column(name = "pet_divine")
    private Integer petDivine;

    @Column(name = "pet_destroy")
    private Integer petDestroy;

    @Column(name = "dropratefish1")
    private String dropratefish1;

    @Column(name = "dropratefish2")
    private String dropratefish2;

    @Column(name = "dropratefish3")
    private String dropratefish3;

    @Column(name = "dropratefish4")
    private String dropratefish4;



}
