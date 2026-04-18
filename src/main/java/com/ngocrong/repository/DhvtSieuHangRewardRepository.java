package com.ngocrong.repository;

import com.ngocrong.data.DhvtSieuHangReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface DhvtSieuHangRewardRepository extends JpaRepository<DhvtSieuHangReward, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE DhvtSieuHangReward p SET p.reward = :reward WHERE p.playerId = :playerId")
    void setReward(int playerId, Integer reward);

    @Query(value = "SELECT * from nr_dhvt_sieu_hang_reward where player_id = :playerId order by id desc limit 1", nativeQuery = true)
    Optional<DhvtSieuHangReward> findFirstByName(int playerId);

}
