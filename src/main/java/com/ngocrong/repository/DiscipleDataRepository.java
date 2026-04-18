package com.ngocrong.repository;

import com.ngocrong.data.DiscipleData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface DiscipleDataRepository extends JpaRepository<DiscipleData, Integer> {

}
