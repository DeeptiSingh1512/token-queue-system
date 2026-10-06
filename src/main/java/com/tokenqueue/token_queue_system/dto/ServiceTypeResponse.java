package com.tokenqueue.token_queue_system.dto;

import com.tokenqueue.token_queue_system.entity.ServiceType;

public record ServiceTypeResponse(
        Long id,
        String name,
        String prefix,
        int avgServiceMinutes,
        boolean active,
        Long officeId) {

    public static ServiceTypeResponse from(ServiceType s) {
        return new ServiceTypeResponse(s.getId(), s.getName(), s.getPrefix(),
                s.getAvgServiceMinutes(), s.isActive(), s.getOffice().getId());
    }
}