package com.ngocrong.repository;

import com.ngocrong.data.EventOpen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventOpenRepository extends JpaRepository<EventOpen, Integer> {
}
