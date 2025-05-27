package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.ServiceDto;
import ch.fdb.zythopedia.dto.SoldDrinkDetailedDto;
import ch.fdb.zythopedia.dto.creation.FullDrinkDto;
import ch.fdb.zythopedia.service.ServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping(value = "/service/{serviceId}")
    public ServiceDto findById(@PathVariable long serviceId) {
        return serviceService.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    @PutMapping("/boughtdrink/{boughtDrinkId}/service")
    @Secured("ROLE_MANAGER")
    public List<ServiceDto> updateBoughtDrink(@PathVariable Long boughtDrinkId, @RequestBody List<ServiceDto> services) {
        return serviceService.updateServices(boughtDrinkId, services);
    }
}
