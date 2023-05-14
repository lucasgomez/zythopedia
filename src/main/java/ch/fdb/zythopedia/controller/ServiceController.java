package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.ServiceDto;
import ch.fdb.zythopedia.service.ServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/api/service", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping(value = "/{serviceId}")
    public ServiceDto findById(@PathVariable long serviceId) {
        return serviceService.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }
}
