package com.tokenqueue.token_queue_system.repository;

import com.tokenqueue.token_queue_system.entity.Counter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CounterRepository extends JpaRepository<Counter, Long> {
    List<Counter> findByOfficeId(Long officeId);
    boolean existsByStaffIdAndActiveTrue(Long staffId);
    boolean existsByStaffIdAndActiveTrueAndIdNot(Long staffId, Long id);
    Optional<Counter> findByStaffIdAndActiveTrue(Long staffId);
}