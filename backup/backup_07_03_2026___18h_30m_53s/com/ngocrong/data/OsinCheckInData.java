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
@Table(name = "nr_event_checkin")
public class OsinCheckInData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "player_id")
    private Integer playerId;

    @Column(name = "checkin_date")
    private Instant checkinDate;

    @Column(name = "rewarded")
    private Byte rewarded;
    
    @Column(name = "is_rewarded")
    private Integer is_rewarded;
}
