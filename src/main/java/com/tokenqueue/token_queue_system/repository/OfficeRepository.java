package com.tokenqueue.token_queue_system.repository;

import com.tokenqueue.token_queue_system.entity.Office;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeRepository extends JpaRepository<Office, Long> {
}