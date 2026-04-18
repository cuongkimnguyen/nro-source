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
@Table(name = "nr_top_dhvt_sieu_hang_1")
public class DHVTSieuHangData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "player_id")
    public Integer playerId;

    @Column(name = "top")
    public Integer point;

    @Column(name = "is_reward")
    public int isReward;

    @Column(name = "create_date")
    public Instant createDate;

    @Column(name = "modify_date")
    public Instant modifyDate;

}
