package com.ngocrong.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "nr_whis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WhisData {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "player_id")
    private Integer playerId;

    @Column(name = "current_level")
    private Integer currentLevel;

    @Column(name = "create_date")
    private Instant createDate;
}
