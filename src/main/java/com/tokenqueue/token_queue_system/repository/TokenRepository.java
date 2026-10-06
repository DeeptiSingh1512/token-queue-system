package com.tokenqueue.token_queue_system.repository;

import com.tokenqueue.token_queue_system.entity.Token;
import com.tokenqueue.token_queue_system.enums.TokenStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findTopByServiceTypeIdAndTokenDateOrderBySequenceNumberDesc(
            Long serviceTypeId, LocalDate tokenDate);

    long countByServiceTypeIdAndTokenDateAndStatusAndSequenceNumberLessThan(
            Long serviceTypeId, LocalDate tokenDate,
            TokenStatus status, int sequenceNumber);

    List<Token> findByCitizenIdOrderByCreatedAtDesc(Long citizenId);

    boolean existsByCitizenIdAndServiceTypeIdAndTokenDateAndStatusIn(
            Long citizenId, Long serviceTypeId, LocalDate tokenDate,
            Collection<TokenStatus> statuses);

    Optional<Token> findTopByServiceTypeIdAndTokenDateAndStatusInOrderByCalledAtDesc(
        Long serviceTypeId,
        LocalDate tokenDate,
        List<TokenStatus> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select t from Token t
            join t.serviceType s
            where s.office.id = :officeId
              and t.tokenDate = :tokenDate
              and t.status = :status
            order by t.createdAt asc
            """)
    List<Token> findNextWaitingTokenForOfficeAndDateForUpdate(
            @Param("officeId") Long officeId,
            @Param("tokenDate") LocalDate tokenDate,
            @Param("status") TokenStatus status,
            Pageable pageable);

    @Query("""
            select count(t) from Token t
            join t.serviceType s
            where s.office.id = :officeId
              and s.active = true
              and t.tokenDate = :tokenDate
              and t.status = :status
            """)
    long countTokensForOfficeAndDateByStatus(
            @Param("officeId") Long officeId,
            @Param("tokenDate") LocalDate tokenDate,
            @Param("status") TokenStatus status);

    @Query("""
            select t from Token t
            join t.serviceType s
            where s.office.id = :officeId
              and t.tokenDate = :tokenDate
              and t.status = :status
            order by t.createdAt asc
            """)
    List<Token> findTokensForOfficeAndDateByStatus(
            @Param("officeId") Long officeId,
            @Param("tokenDate") LocalDate tokenDate,
            @Param("status") TokenStatus status,
            Pageable pageable);

    Optional<Token> findTopByCounterIdAndTokenDateAndStatusInOrderByCalledAtDesc(
            Long counterId, LocalDate tokenDate, Collection<TokenStatus> statuses);
}
