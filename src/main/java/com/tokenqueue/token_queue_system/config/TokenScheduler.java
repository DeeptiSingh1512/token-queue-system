package com.tokenqueue.token_queue_system.config;

import com.tokenqueue.token_queue_system.service.TokenMaintenanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenScheduler {

    private final TokenMaintenanceService tokenMaintenanceService;

    @Value("${queue.called-timeout-minutes:15}")
    private long calledTimeoutMinutes;

    @Scheduled(fixedDelay = 60000)
    public void autoSkipNoShows() {
        int changed = tokenMaintenanceService.autoSkipNoShows(calledTimeoutMinutes);
        if (changed > 0) {
            log.info("Auto-skipped {} no-show token(s)", changed);
        }
    }

    @Scheduled(cron = "${queue.cleanup-cron:0 5 0 * * *}", zone = "Asia/Kolkata")
    public void cleanupPreviousDays() {
        int changed = tokenMaintenanceService.cleanupPreviousDays();
        if (changed > 0) {
            log.info("Cleaned up {} leftover token(s)", changed);
        }
    }
}
