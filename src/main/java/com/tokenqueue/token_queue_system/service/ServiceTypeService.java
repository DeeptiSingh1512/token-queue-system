package com.tokenqueue.token_queue_system.service;

import com.tokenqueue.token_queue_system.dto.ServiceTypeRequest;
import com.tokenqueue.token_queue_system.dto.ServiceTypeResponse;
import com.tokenqueue.token_queue_system.entity.Office;
import com.tokenqueue.token_queue_system.entity.ServiceType;
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
public class ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;
    private final OfficeRepository officeRepository;

    @Transactional
    public ServiceTypeResponse create(Long officeId, ServiceTypeRequest request) {
        Office office = officeRepository.findById(officeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Office not found"));

        String prefix = request.prefix().toUpperCase();
        if (serviceTypeRepository.existsByOfficeIdAndPrefixIgnoreCase(officeId, prefix)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Prefix already used in this office");
        }

        ServiceType service = ServiceType.builder()
                .name(request.name().trim())
                .prefix(prefix)
                .avgServiceMinutes(request.avgServiceMinutes())
                .office(office)
                .build();
        return ServiceTypeResponse.from(serviceTypeRepository.save(service));
    }

    @Transactional(readOnly = true)
    public List<ServiceTypeResponse> getByOffice(Long officeId) {
        if (!officeRepository.existsById(officeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Office not found");
        }
        return serviceTypeRepository.findByOfficeId(officeId).stream()
                .map(ServiceTypeResponse::from).toList();
    }

    @Transactional
    public ServiceTypeResponse update(Long id, ServiceTypeRequest request) {
        ServiceType service = findOrThrow(id);
        String prefix = request.prefix().toUpperCase();

        if (!service.getPrefix().equalsIgnoreCase(prefix)
                && serviceTypeRepository.existsByOfficeIdAndPrefixIgnoreCase(
                        service.getOffice().getId(), prefix)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Prefix already used in this office");
        }

        service.setName(request.name().trim());
        service.setPrefix(prefix);
        service.setAvgServiceMinutes(request.avgServiceMinutes());
        return ServiceTypeResponse.from(serviceTypeRepository.save(service));
    }

    @Transactional
    public void disable(Long id) {
        ServiceType service = findOrThrow(id);
        service.setActive(false);
        serviceTypeRepository.save(service);
    }

    private ServiceType findOrThrow(Long id) {
        return serviceTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Service not found"));
    }
}