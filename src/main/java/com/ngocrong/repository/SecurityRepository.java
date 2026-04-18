package com.ngocrong.repository;

import com.ngocrong.data.SecurityData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityRepository extends JpaRepository<SecurityData, Integer> {
}
