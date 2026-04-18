package com.ngocrong.repository;

import com.ngocrong.data.TopSuperRankData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface TopSuperRankRepository extends JpaRepository<TopSuperRankData, Integer> {
    @Query(value = "SELECT * FROM nr_top_superrank WHERE player_id = :playerId ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<TopSuperRankData> findLatestByPlayerId(Integer playerId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM nr_top_superrank WHERE player_id = :playerId", nativeQuery = true)
    void deleteByPlayerId(Integer playerId);
}
