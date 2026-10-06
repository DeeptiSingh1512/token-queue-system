package com.tokenqueue.token_queue_system.repository;

import com.tokenqueue.token_queue_system.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {

    List<ServiceType> findByOfficeIdAndActiveTrue(Long officeId);

    List<ServiceType> findByOfficeId(Long officeId);

    boolean existsByOfficeIdAndPrefixIgnoreCase(Long officeId, String prefix);
}