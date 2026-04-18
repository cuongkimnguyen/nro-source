package com.ngocrong.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nr_event_vqtd")
public class VongQuayThuongDeData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "player_name")
    public Integer playerName;

    @Column(name = "point")
    public int point;

    @Column(name = "reward")
    public int reward;

    @Column(name = "create_date")
    public Instant createDate;

    @Column(name = "modify_date")
    public Instant modifyDate;

}
