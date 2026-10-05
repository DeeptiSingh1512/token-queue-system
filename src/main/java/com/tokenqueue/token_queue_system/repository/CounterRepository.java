package com.tokenqueue.token_queue_system.repository;

import com.tokenqueue.token_queue_system.entity.Counter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CounterRepository extends JpaRepository<Counter, Long> {
    List<Counter> findByOfficeId(Long officeId);
}