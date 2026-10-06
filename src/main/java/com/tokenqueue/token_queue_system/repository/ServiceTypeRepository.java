package com.tokenqueue.token_queue_system.repository;

import com.tokenqueue.token_queue_system.entity.ServiceType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {

    List<ServiceType> findByOfficeIdAndActiveTrue(Long officeId);

    List<ServiceType> findByOfficeId(Long officeId);

    boolean existsByOfficeIdAndPrefixIgnoreCase(Long officeId, String prefix);

    // Locks the row until the transaction ends, so numbering can't be duplicated
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ServiceType s where s.id = :id")
    Optional<ServiceType> findByIdForUpdate(@Param("id") Long id);
}