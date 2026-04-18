package com.ngocrong.repository;

import com.ngocrong.data.MabuEggData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MabuEggRepository extends JpaRepository<MabuEggData, Integer> {

    @Query(value = "SELECT * FROM nr_mabu_egg WHERE player_id = :playerId LIMIT 1", nativeQuery = true)
    Optional<MabuEggData> findByPlayerId(Integer playerId);
}
