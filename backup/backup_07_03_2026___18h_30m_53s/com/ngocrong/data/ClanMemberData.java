package com.ngocrong.data;

import com.ngocrong.user.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "nr_clan_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClanMemberData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Column(name = "clan_id")
    public Integer clanId;

    @Column(name = "player_id")
    public Integer playerId;

    @Column(name = "name")
    public String name;

    @Column(name = "role")
    public Byte role;

    @Column(name = "head")
    public Short head;

    @Column(name = "body")
    public Short body;

    @Column(name = "leg")
    public Short leg;

    @Column(name = "power_point")
    public Long powerPoint;

    @Column(name = "donate")
    public Integer donate;

    @Column(name = "receive_donate")
    public Integer receiveDonate;

    @Column(name = "clan_point")
    public Integer clanPoint;

    @Column(name = "current_point")
    public Integer currentPoint;

    @Column(name = "clan_reward")
    public String clanReward;

    @Column(name = "join_time")
    public Timestamp joinTime;

    public ClanMemberData(int clanId, Player _player, byte role) {
        playerId = _player.id;
        this.clanId = clanId;
        name = _player.name;
        this.role = role;
        head = _player.getHead();
        body = _player.getBody();
        leg = _player.getLeg();
        clanPoint = 0;
        currentPoint = 0;
        donate = 0;
        receiveDonate = 0;
        powerPoint = _player.info.power;
        clanReward = "[]";
        joinTime = new Timestamp(System.currentTimeMillis());
    }
}
