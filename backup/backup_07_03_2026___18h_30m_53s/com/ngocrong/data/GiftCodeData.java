package com.ngocrong.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "nr_gift_code")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GiftCodeData {

    @Id
    @Column(name = "id")
    public Integer id;

    @Column(name = "server_id")
    public Integer serverId;

    @Column(name = "code")
    public String code;

    @Column(name = "gold")
    public Long gold;

    @Column(name = "diamond")
    public Integer diamond;

    @Column(name = "diamond_lock")
    public Integer diamondLock;

    @Column(name = "items")
    public String items;

    @Column(name = "expires_at")
    public Timestamp expiryTime;

    @Column(name = "created_at")
    public Timestamp createTime;
}
