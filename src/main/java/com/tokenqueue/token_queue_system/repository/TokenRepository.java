package com.tokenqueue.token_queue_system.repository;

import com.tokenqueue.token_queue_system.entity.Token;
import com.tokenqueue.token_queue_system.enums.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    // Latest token of a service today, used to generate the next number
    Optional<Token> findTopByServiceTypeIdAndTokenDateOrderBySequenceNumberDesc(
            Long serviceTypeId, LocalDate tokenDate);

    // How many people are ahead in the queue (for position and wait time)
    long countByServiceTypeIdAndTokenDateAndStatusAndSequenceNumberLessThan(
            Long serviceTypeId, LocalDate tokenDate,
            TokenStatus status, int sequenceNumber);

    // A citizen's tokens, newest first
    List<Token> findByCitizenIdOrderByCreatedAtDesc(Long citizenId);
}