package com.ngocrong.repository;

import com.ngocrong.data.BoMongConfigData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoMongConfigRepository extends JpaRepository<BoMongConfigData, Integer> {

    @Query(value = "SELECT * FROM nr_bo_mong_config WHERE config_key = :configKey LIMIT 1", nativeQuery = true)
    Optional<BoMongConfigData> findByConfigKey(@Param("configKey") String configKey);
}

