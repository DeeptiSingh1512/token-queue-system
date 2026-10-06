package com.tokenqueue.token_queue_system.repository;

import com.tokenqueue.token_queue_system.entity.Token;
import com.tokenqueue.token_queue_system.enums.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
