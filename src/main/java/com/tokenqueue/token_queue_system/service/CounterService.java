package com.tokenqueue.token_queue_system.service;

import com.tokenqueue.token_queue_system.dto.CounterRequest;
import com.tokenqueue.token_queue_system.dto.CounterResponse;
import com.tokenqueue.token_queue_system.entity.Counter;
import com.tokenqueue.token_queue_system.entity.Office;
import com.tokenqueue.token_queue_system.entity.User;
import com.tokenqueue.token_queue_system.enums.Role;
import com.tokenqueue.token_queue_system.repository.CounterRepository;
import com.tokenqueue.token_queue_system.repository.OfficeRepository;
import com.tokenqueue.token_queue_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CounterService {

    private final CounterRepository counterRepository;
    private final OfficeRepository officeRepository;
    private final UserRepository userRepository;

    @Transactional
    public CounterResponse create(Long officeId, CounterRequest request) {
        Office office = officeRepository.findById(officeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Office not found"));

        User staff = resolveStaff(request.staffId(), null);

        Counter counter = Counter.builder()
                .name(request.name().trim())
                .office(office)
                .staff(staff)
                .build();

        return CounterResponse.from(counterRepository.save(counter));
    }

    @Transactional(readOnly = true)
    public List<CounterResponse> getByOffice(Long officeId) {
        if (!officeRepository.existsById(officeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Office not found");
        }
        return counterRepository.findByOfficeId(officeId).stream()
                .map(CounterResponse::from).toList();
    }

    @Transactional
    public CounterResponse update(Long id, CounterRequest request) {
        Counter counter = findOrThrow(id);
        counter.setName(request.name().trim());
        counter.setStaff(resolveStaff(request.staffId(), id));
        return CounterResponse.from(counterRepository.save(counter));
    }

    @Transactional
    public void disable(Long id) {
        Counter counter = findOrThrow(id);
        counter.setActive(false);
        counter.setStaff(null);
        counterRepository.save(counter);
    }

    private Counter findOrThrow(Long id) {
        return counterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Counter not found"));
    }

    // Returns the staff user to assign, or null if no staff was given.
    private User resolveStaff(Long staffId, Long counterId) {
        if (staffId == null) {
            return null;
        }

        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Staff user not found"));

        if (staff.getRole() != Role.STAFF || !staff.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User is not an active staff member");
        }

        boolean alreadyAssigned = (counterId == null)
                ? counterRepository.existsByStaffIdAndActiveTrue(staffId)
                : counterRepository.existsByStaffIdAndActiveTrueAndIdNot(staffId, counterId);

        if (alreadyAssigned) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Staff is already assigned to another counter");
        }

        return staff;
    }
}