package com.tokenqueue.token_queue_system.controller;

import com.tokenqueue.token_queue_system.dto.OfficeResponse;
import com.tokenqueue.token_queue_system.dto.ServiceTypeResponse;
import com.tokenqueue.token_queue_system.service.PublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final PublicService publicService;

    @GetMapping("/offices")
    public List<OfficeResponse> offices() {
        return publicService.getActiveOffices();
    }

    @GetMapping("/offices/{officeId}/services")
    public List<ServiceTypeResponse> services(@PathVariable Long officeId) {
        return publicService.getActiveServices(officeId);
    }
}