package com.tokenqueue.token_queue_system.dto;

import com.tokenqueue.token_queue_system.entity.Office;

public record OfficeResponse(
        Long id,
        String name,
        String address,
        String district,
        boolean active) {

    public static OfficeResponse from(Office o) {
        return new OfficeResponse(o.getId(), o.getName(), o.getAddress(),
                o.getDistrict(), o.isActive());
    }
}