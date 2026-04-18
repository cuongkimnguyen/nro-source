package com.ngocrong.repository;

import com.ngocrong.data.OsinLixiData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OsinLixiRepository extends JpaRepository<OsinLixiData, Integer> {

    @Query(value = "SELECT * FROM nr_event_osin_lixi WHERE player_id = :playerId AND DATE(lixi_date) = CURDATE() LIMIT 1", nativeQuery = true)
    Optional<OsinLixiData> findTodayByPlayer(@Param("playerId") int playerId);
}
