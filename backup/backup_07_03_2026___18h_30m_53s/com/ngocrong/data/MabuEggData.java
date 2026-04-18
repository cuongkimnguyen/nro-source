package com.ngocrong.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "nr_mabu_egg")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MabuEggData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "player_id")
    private Integer playerId;

    @Column(name = "last_time_create")
    private Long lastTimeCreate;

    @Column(name = "time_done")
    private Long timeDone;
}
