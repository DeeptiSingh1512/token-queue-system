package com.tokenqueue.token_queue_system.dto;

import com.tokenqueue.token_queue_system.entity.User;

public record StaffResponse(
        Long id,
        String name,
        String email,
        boolean active) {

    public static StaffResponse from(User u) {
        return new StaffResponse(u.getId(), u.getName(), u.getEmail(), u.isActive());
    }
}