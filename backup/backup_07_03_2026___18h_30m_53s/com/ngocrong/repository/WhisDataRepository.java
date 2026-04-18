package com.ngocrong.repository;

import com.ngocrong.data.WhisData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface WhisDataRepository extends JpaRepository<WhisData, Integer> {

    @Query(value = "SELECT * from nr_whis where player_id = :playerId limit 1", nativeQuery = true)
    Optional<WhisData> findByPlayerId(int playerId);

    @Query(value = "SELECT * from nr_whis order by current_level DESC limit 15", nativeQuery = true)
    List<WhisData> findTopReward();

    @Query(value = "SELECT * from nr_whis where player_id = :playerId order by id desc limit 1", nativeQuery = true)
    WhisData findFirstById(Integer playerId);

    @Transactional
    @Query(value = "UPDATE nr_whis set current_level = 0 where id > 0", nativeQuery = true)
    WhisData clearCurrentLevel(Integer playerId);
}
