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
@Table(name = "history_receive_goldbar")
public class HistoryGoldBar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "username")
    public String userName;

    @Column(name = "player")
    public String playerName;

    @Column(name = "gold_before")
    public Integer goldBefore;

    @Column(name = "gold_after")
    public Integer goldAfter;

    @Column(name = "number_gold")
    public Integer numberGold;

    @Column(name = "create_date")
    public Instant createDate;

    @Column(name = "note")
    public String note;
}
