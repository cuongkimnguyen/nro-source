package com.ngocrong.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "nr_gift_code_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GiftHistoryData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Column(name = "player_id")
    public Integer playerId;

    @Column(name = "gift_code_id")
    public Integer giftCodeId;

    @Column(name = "created_at")
    public Timestamp createTime;
}
