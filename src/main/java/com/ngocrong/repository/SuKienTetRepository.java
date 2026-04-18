package com.ngocrong.repository;

import com.ngocrong.data.SuKienTetData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface SuKienTetRepository extends JpaRepository<SuKienTetData, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE SuKienTetData p SET p.point = :point WHERE p.playerId = :playerId")
    void setReward(Integer playerId, String point);

    @Query(value = "SELECT * from nr_event_tet where player_id = :playerId order by id desc limit 1", nativeQuery = true)
    Optional<SuKienTetData> findFirstByName(Integer playerId);
}
