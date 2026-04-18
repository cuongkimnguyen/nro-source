package com.ngocrong.repository;

import com.ngocrong.data.EventOpen;
import com.ngocrong.data.VongQuayThuongDeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface EventVQTD extends JpaRepository<VongQuayThuongDeData, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE VongQuayThuongDeData p SET p.reward = :reward WHERE p.playerName = :playerId")
    void setReward(Integer playerId, Integer reward);

    @Query(value = "SELECT * from nr_event_vqtd where player_name = :playerId order by id desc limit 1", nativeQuery = true)
    Optional<VongQuayThuongDeData> findFirstByName(Integer playerId);

    @Query(value = "SELECT sum(point), player_name, reward from nr_event_vqtd where player_name = :playerId group by player_name, reward order by reward desc limit 1", nativeQuery = true)
    Optional<VongQuayThuongDeData> findFirstByNameTopMoc(Integer playerId);
}
