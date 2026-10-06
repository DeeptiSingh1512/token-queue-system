package com.tokenqueue.token_queue_system.service;

import com.tokenqueue.token_queue_system.dto.OfficeResponse;
import com.tokenqueue.token_queue_system.dto.ServiceTypeResponse;
import com.tokenqueue.token_queue_system.repository.OfficeRepository;
import com.tokenqueue.token_queue_system.repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicService {

    private final OfficeRepository officeRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    @Transactional(readOnly = true)
    public List<OfficeResponse> getActiveOffices() {
        return officeRepository.findByActiveTrue().stream()
                .map(OfficeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ServiceTypeResponse> getActiveServices(Long officeId) {
        if (!officeRepository.existsById(officeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Office not found");
        }
        return serviceTypeRepository.findByOfficeIdAndActiveTrue(officeId).stream()
                .map(ServiceTypeResponse::from).toList();
    }
}