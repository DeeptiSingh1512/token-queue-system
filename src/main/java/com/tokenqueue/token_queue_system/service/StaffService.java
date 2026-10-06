package com.tokenqueue.token_queue_system.service;

import com.tokenqueue.token_queue_system.dto.StaffRequest;
import com.tokenqueue.token_queue_system.dto.StaffResponse;
import com.tokenqueue.token_queue_system.entity.User;
import com.tokenqueue.token_queue_system.enums.Role;
import com.tokenqueue.token_queue_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public StaffResponse create(StaffRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User staff = User.builder()
                .name(request.name().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .role(Role.STAFF)
                .build();

        return StaffResponse.from(userRepository.save(staff));
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> getAll() {
        return userRepository.findByRole(Role.STAFF).stream()
                .map(StaffResponse::from).toList();
    }
}