package com.tokenqueue.token_queue_system.service;

import com.tokenqueue.token_queue_system.dto.OfficeRequest;
import com.tokenqueue.token_queue_system.dto.OfficeResponse;
import com.tokenqueue.token_queue_system.entity.Office;
import com.tokenqueue.token_queue_system.repository.OfficeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfficeService {

    private final OfficeRepository officeRepository;

    @Transactional
    public OfficeResponse create(OfficeRequest request) {
        Office office = Office.builder()
                .name(request.name().trim())
                .address(request.address().trim())
                .district(request.district() == null ? null : request.district().trim())
                .build();
        return OfficeResponse.from(officeRepository.save(office));
    }

    @Transactional(readOnly = true)
    public List<OfficeResponse> getAll() {
        return officeRepository.findAll().stream().map(OfficeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OfficeResponse getById(Long id) {
        return OfficeResponse.from(findOrThrow(id));
    }

    @Transactional
    public OfficeResponse update(Long id, OfficeRequest request) {
        Office office = findOrThrow(id);
        office.setName(request.name().trim());
        office.setAddress(request.address().trim());
        office.setDistrict(request.district() == null ? null : request.district().trim());
        return OfficeResponse.from(officeRepository.save(office));
    }

    @Transactional
    public void disable(Long id) {
        Office office = findOrThrow(id);
        office.setActive(false);
        officeRepository.save(office);
    }

    private Office findOrThrow(Long id) {
        return officeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Office not found"));
    }
}