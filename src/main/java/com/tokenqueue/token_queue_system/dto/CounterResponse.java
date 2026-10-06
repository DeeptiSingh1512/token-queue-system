package com.tokenqueue.token_queue_system.dto;

import com.tokenqueue.token_queue_system.entity.Counter;
import com.tokenqueue.token_queue_system.entity.User;

public record CounterResponse(
        Long id,
        String name,
        boolean active,
        Long officeId,
        Long staffId,
        String staffName) {

    public static CounterResponse from(Counter c) {
        User s = c.getStaff();
        return new CounterResponse(
                c.getId(),
                c.getName(),
                c.isActive(),
                c.getOffice().getId(),
                s == null ? null : s.getId(),
                s == null ? null : s.getName());
    }
}