package com.tokenqueue.token_queue_system.service;

import com.tokenqueue.token_queue_system.dto.OfficeStatsResponse;
import com.tokenqueue.token_queue_system.dto.ServiceStatsResponse;
import com.tokenqueue.token_queue_system.dto.TokenStatusCount;
import com.tokenqueue.token_queue_system.dto.TokenTiming;
import com.tokenqueue.token_queue_system.entity.Office;
import com.tokenqueue.token_queue_system.entity.ServiceType;
import com.tokenqueue.token_queue_system.enums.TokenStatus;
import com.tokenqueue.token_queue_system.repository.OfficeRepository;
import com.tokenqueue.token_queue_system.repository.ServiceTypeRepository;
import com.tokenqueue.token_queue_system.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminStatsService {

    private final OfficeRepository officeRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final TokenRepository tokenRepository;

    @Transactional(readOnly = true)
    public OfficeStatsResponse getStats(Long officeId, LocalDate date) {
        LocalDate statsDate = date == null ? LocalDate.now(TokenService.ZONE) : date;
        if (statsDate.isAfter(LocalDate.now(TokenService.ZONE))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Date cannot be in the future");
        }

        Office office = officeRepository.findById(officeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Office not found"));

        Map<Long, Map<TokenStatus, Long>> countsByService = new HashMap<>();
        for (TokenStatusCount row : tokenRepository
                .countByOfficeAndDateGroupedByServiceAndStatus(officeId, statsDate)) {
            countsByService.computeIfAbsent(row.serviceId(), ignored -> new HashMap<>())
                    .put(row.status(), row.count());
        }

        long totalIssued = 0;
        long waiting = 0;
        long inProgress = 0;
        long completed = 0;
        long skipped = 0;
        long cancelled = 0;
        for (Map<TokenStatus, Long> counts : countsByService.values()) {
            totalIssued += counts.values().stream().mapToLong(Long::longValue).sum();
            waiting += count(counts, TokenStatus.WAITING);
            inProgress += count(counts, TokenStatus.CALLED)
                    + count(counts, TokenStatus.IN_SERVICE);
            completed += count(counts, TokenStatus.COMPLETED);
            skipped += count(counts, TokenStatus.SKIPPED);
            cancelled += count(counts, TokenStatus.CANCELLED);
        }

        List<ServiceStatsResponse> services = serviceTypeRepository.findByOfficeId(officeId)
                .stream()
                .map(service -> serviceStats(service, countsByService))
                .toList();

        List<TokenTiming> timings =
                tokenRepository.findTokenTimingsByOfficeAndDate(officeId, statsDate);
        Double averageWaitMinutes = averageWaitMinutes(timings);
        Double averageServiceMinutes = averageServiceMinutes(timings);

        return new OfficeStatsResponse(
                office.getId(),
                office.getName(),
                statsDate,
                totalIssued,
                waiting,
                inProgress,
                completed,
                skipped,
                cancelled,
                averageWaitMinutes,
                averageServiceMinutes,
                services);
    }

    private ServiceStatsResponse serviceStats(
            ServiceType service, Map<Long, Map<TokenStatus, Long>> countsByService) {
        Map<TokenStatus, Long> counts = countsByService.getOrDefault(service.getId(), Map.of());
        long issued = counts.values().stream().mapToLong(Long::longValue).sum();
        return new ServiceStatsResponse(
                service.getId(),
                service.getName(),
                service.getPrefix(),
                issued,
                count(counts, TokenStatus.WAITING),
                count(counts, TokenStatus.COMPLETED),
                count(counts, TokenStatus.SKIPPED),
                count(counts, TokenStatus.CANCELLED));
    }

    private long count(Map<TokenStatus, Long> counts, TokenStatus status) {
        return counts.getOrDefault(status, 0L);
    }

    private Double averageWaitMinutes(List<TokenTiming> timings) {
        double totalMinutes = 0;
        long count = 0;
        for (TokenTiming timing : timings) {
            totalMinutes += Duration.between(timing.createdAt(), timing.calledAt())
                    .toSeconds() / 60.0;
            count++;
        }
        return roundedAverage(totalMinutes, count);
    }

    private Double averageServiceMinutes(List<TokenTiming> timings) {
        double totalMinutes = 0;
        long count = 0;
        for (TokenTiming timing : timings) {
            if (timing.status() == TokenStatus.COMPLETED && timing.completedAt() != null) {
                totalMinutes += Duration.between(timing.calledAt(), timing.completedAt())
                        .toSeconds() / 60.0;
                count++;
            }
        }
        return roundedAverage(totalMinutes, count);
    }

    private Double roundedAverage(double totalMinutes, long count) {
        return count == 0 ? null : Math.round((totalMinutes / count) * 10.0) / 10.0;
    }
}
