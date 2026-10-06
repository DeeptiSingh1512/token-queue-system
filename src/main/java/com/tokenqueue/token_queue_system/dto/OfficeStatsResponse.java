package com.tokenqueue.token_queue_system.dto;

import java.time.LocalDate;
import java.util.List;

public record OfficeStatsResponse(
        Long officeId,
        String officeName,
        LocalDate date,
        long totalIssued,
        long waiting,
        long inProgress,
        long completed,
        long skipped,
        long cancelled,
        Double averageWaitMinutes,
        Double averageServiceMinutes,
        List<ServiceStatsResponse> services) {
}
