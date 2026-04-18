package com.ngocrong.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "nr_history_trade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryTradeData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Column(name = "player_name_1")
    public String playerName1;

    @Column(name = "player_name_2")
    public String playerName2;

    @Column(name = "item_bag_1_before")
    public String itemBefore1;

    @Column(name = "item_bag_2_before")
    public String itemBefore2;

    @Column(name = "item_bag_1_after")
    public String itemAfter1;

    @Column(name = "item_bag_2_after")
    public String itemAfter2;

    @Column(name = "item_trade_1")
    public String itemTrade1;

    @Column(name = "item_trade_2")
    public String itemTrade2;

    @Column(name = "create_time")
    public Timestamp createTime;

}
