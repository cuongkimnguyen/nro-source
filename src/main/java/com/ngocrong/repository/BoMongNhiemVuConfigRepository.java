package com.ngocrong.repository;

import com.ngocrong.data.BoMongNhiemVuConfigData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoMongNhiemVuConfigRepository extends JpaRepository<BoMongNhiemVuConfigData, Integer> {

    @Query(value = "SELECT * FROM nr_bo_mong_nhiem_vu_config WHERE active = 1", nativeQuery = true)
    List<BoMongNhiemVuConfigData> findAllActive();
}

