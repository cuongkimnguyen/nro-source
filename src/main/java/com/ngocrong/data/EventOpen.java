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
@Table(name = "nr_event_open")
public class EventOpen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "player_name")
    public String playerName;

    @Column(name = "point")
    public int point;

    @Column(name = "create_date")
    public Instant createDate;

    @Column(name = "modify_date")
    public Instant modifyDate;
}
