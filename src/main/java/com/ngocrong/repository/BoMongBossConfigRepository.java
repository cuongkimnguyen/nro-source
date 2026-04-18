package com.ngocrong.repository;

import com.ngocrong.data.BoMongBossConfigData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoMongBossConfigRepository extends JpaRepository<BoMongBossConfigData, Integer> {

    @Query(value = "SELECT * FROM nr_bo_mong_boss_config WHERE do_kho = :doKho AND active = 1", nativeQuery = true)
    List<BoMongBossConfigData> findByDoKhoAndActive(@Param("doKho") Integer doKho);
}

