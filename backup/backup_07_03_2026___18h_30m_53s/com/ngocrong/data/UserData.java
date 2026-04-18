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
@Table(name = "nr_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserData {

    @Id
    @Column(name = "id")
    public Integer id;

    @Column(name = "username")
    public String username;

    @Column(name = "password")
    public String password;

    @Column(name = "status")
    public Integer status;

    @Column(name = "gold_bar")
    public Integer goldBar;

    @Column(name = "coin")
    public Long coin;

    @Column(name = "role")
    public Integer role;

    @Column(name = "activated")
    public Integer activated;

    @Column(name = "lock_time")
    public Timestamp lockTime;

    @Column(name = "create_time")
    public Timestamp createTime;
}
