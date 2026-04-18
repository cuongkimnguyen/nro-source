package com.ngocrong.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nr_statistic_server")
public class StatisticServerData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "player_id")
    private Integer playerId;

    @Column(name = "player_name")
    private String playerName;

    @Column(name = "gold_bar")
    private Integer goldBar;

    @Column(name = "gold_bar_spent_today")
    private Integer goldBarSpentToday;

    @Column(name = "ao_than_linh")
    private Integer aoThanLinh;

    @Column(name = "quan_than_linh")
    private Integer quanThanLinh;

    @Column(name = "gang_than_linh")
    private Integer gangThanLinh;

    @Column(name = "giay_than_linh")
    private Integer giayThanLinh;

    @Column(name = "nhan_than_linh")
    private Integer nhanThanLinh;

    @Column(name = "item_cap2")
    private Integer itemCap2;

    @Column(name = "nr_1s")
    private Integer nr1s;

    @Column(name = "nr_2s")
    private Integer nr2s;

    @Column(name = "nr_3s")
    private Integer nr3s;
    
//    @Column(name = "has_set_thien_menh")
//    private Boolean hasSetThienMenh;

    @Column(name = "create_date")
    private Instant createDate;
}