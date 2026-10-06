package com.tokenqueue.token_queue_system.service;

import com.tokenqueue.token_queue_system.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenMaintenanceService {

    private final TokenRepository tokenRepository;

    @Transactional
    public int autoSkipNoShows(long timeoutMinutes) {
        LocalDateTime now = LocalDateTime.now(TokenService.ZONE);
        LocalDateTime cutoff = now.minusMinutes(timeoutMinutes);
        return tokenRepository.skipCalledTokensBefore(cutoff, now);
    }

    @Transactional
    public int cleanupPreviousDays() {
        LocalDate today = LocalDate.now(TokenService.ZONE);
        LocalDateTime now = LocalDateTime.now(TokenService.ZONE);
        int cancelled = tokenRepository.cancelOldWaitingTokens(today);
        int skipped = tokenRepository.skipOldCalledOrInServiceTokens(today, now);
        return cancelled + skipped;
    }
}
