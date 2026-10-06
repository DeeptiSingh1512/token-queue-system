package com.tokenqueue.token_queue_system.service;

import com.tokenqueue.token_queue_system.dto.StaffQueueResponse;
import com.tokenqueue.token_queue_system.dto.StaffTokenResponse;
import com.tokenqueue.token_queue_system.entity.Counter;
import com.tokenqueue.token_queue_system.entity.Token;
import com.tokenqueue.token_queue_system.entity.User;
import com.tokenqueue.token_queue_system.enums.TokenStatus;
import com.tokenqueue.token_queue_system.repository.CounterRepository;
import com.tokenqueue.token_queue_system.repository.TokenRepository;
import com.tokenqueue.token_queue_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffQueueService {

    private static final List<TokenStatus> BEING_SERVED =
            List.of(TokenStatus.CALLED, TokenStatus.IN_SERVICE);

    private final CounterRepository counterRepository;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public StaffQueueResponse getQueue(String staffEmail) {
        Counter counter = findCounter(staffEmail);
        LocalDate today = LocalDate.now(TokenService.ZONE);

        StaffTokenResponse currentToken = tokenRepository
                .findTopByCounterIdAndTokenDateAndStatusInOrderByCalledAtDesc(
                        counter.getId(), today, BEING_SERVED)
                .map(StaffTokenResponse::from)
                .orElse(null);

        long waitingCount = tokenRepository.countTokensForOfficeAndDateByStatus(
                counter.getOffice().getId(), today, TokenStatus.WAITING);

        List<String> nextTokens = tokenRepository.findTokensForOfficeAndDateByStatus(
                        counter.getOffice().getId(), today, TokenStatus.WAITING,
                        PageRequest.of(0, 5))
                .stream()
                .map(Token::getTokenNumber)
                .toList();

        return new StaffQueueResponse(
                counter.getId(),
                counter.getName(),
                counter.getOffice().getName(),
                currentToken,
                waitingCount,
                nextTokens);
    }

    @Transactional
    public StaffTokenResponse callNext(String staffEmail) {
        Counter counter = findCounter(staffEmail);
        LocalDate today = LocalDate.now(TokenService.ZONE);

        if (tokenRepository
                .findTopByCounterIdAndTokenDateAndStatusInOrderByCalledAtDesc(
                        counter.getId(), today, BEING_SERVED)
                .isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Finish or skip the current token first");
        }

        List<Token> candidates = tokenRepository.findNextWaitingTokenForOfficeAndDateForUpdate(
                counter.getOffice().getId(),
                today,
                TokenStatus.WAITING,
                PageRequest.of(0, 1));

        if (candidates.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No tokens are waiting");
        }

        Token token = candidates.get(0);
        if (token.getStatus() != TokenStatus.WAITING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Token was just taken, try again");
        }

        token.setStatus(TokenStatus.CALLED);
        token.setCounter(counter);
        token.setCalledAt(LocalDateTime.now(TokenService.ZONE));
        return StaffTokenResponse.from(token);
    }

    @Transactional
    public StaffTokenResponse start(String staffEmail, Long tokenId) {
        Counter counter = findCounter(staffEmail);
        Token token = findTokenForCounter(tokenId, counter);

        if (token.getStatus() != TokenStatus.CALLED) {
            throw invalidState();
        }

        token.setStatus(TokenStatus.IN_SERVICE);
        return StaffTokenResponse.from(token);
    }

    @Transactional
    public StaffTokenResponse complete(String staffEmail, Long tokenId) {
        Counter counter = findCounter(staffEmail);
        Token token = findTokenForCounter(tokenId, counter);

        if (!BEING_SERVED.contains(token.getStatus())) {
            throw invalidState();
        }

        token.setStatus(TokenStatus.COMPLETED);
        token.setCompletedAt(LocalDateTime.now(TokenService.ZONE));
        return StaffTokenResponse.from(token);
    }

    @Transactional
    public StaffTokenResponse skip(String staffEmail, Long tokenId) {
        Counter counter = findCounter(staffEmail);
        Token token = findTokenForCounter(tokenId, counter);

        if (token.getStatus() != TokenStatus.CALLED) {
            throw invalidState();
        }

        token.setStatus(TokenStatus.SKIPPED);
        token.setCompletedAt(LocalDateTime.now(TokenService.ZONE));
        return StaffTokenResponse.from(token);
    }

    private Counter findCounter(String staffEmail) {
        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));

        return counterRepository.findByStaffIdAndActiveTrue(staff.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT, "No counter is assigned to you"));
    }

    private Token findTokenForCounter(Long tokenId, Counter counter) {
        return tokenRepository.findById(tokenId)
                .filter(token -> token.getCounter() != null
                        && token.getCounter().getId().equals(counter.getId()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Token not found"));
    }

    private ResponseStatusException invalidState() {
        return new ResponseStatusException(
                HttpStatus.CONFLICT, "Token is not in a state that allows this action");
    }
}
