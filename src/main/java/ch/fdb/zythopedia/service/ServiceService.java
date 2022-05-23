package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.ServiceDto;
import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.entity.Service;
import ch.fdb.zythopedia.enums.ServiceMethod;
import ch.fdb.zythopedia.repository.ServiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@org.springframework.stereotype.Service
public class ServiceService {

    @Value("${service.tap.volumes}")
    private List<Long> tapVolumes;
    private ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public Collection<Service> createNeededService(BoughtDrink boughtDrink) {
        var volumesToCreate = ServiceMethod.TAP == boughtDrink.getServiceMethod()
                ? tapVolumes
                : List.of(Optional.ofNullable(boughtDrink.getVolumeInCl()).orElse(0L));

        var existingServices = serviceRepository.findByBoughtDrink(boughtDrink).stream()
                .map(Service::getVolumeInCl)
                .collect(Collectors.toSet());

        return volumesToCreate.stream()
                .filter(Predicate.not(existingServices::contains))
                .map(volumeToCreate -> Service.builder()
                        .boughtDrink(boughtDrink)
                        .volumeInCl(volumeToCreate)
                        .build())
                .map(serviceRepository::save)
                .collect(Collectors.toSet());
    }

    public Service updatePrice(ServiceDto serviceDto) {
        return serviceRepository.findById(serviceDto.getId())
                .map(service -> service.setSellingPrice(serviceDto.getSellingPrice()))
                .map(serviceRepository::save)
                .orElse(null);
    }
}
