package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.ServiceDto;
import ch.fdb.zythopedia.dto.SoldDrinkDetailedDto;
import ch.fdb.zythopedia.dto.mapper.ServiceMapper;
import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.entity.Service;
import ch.fdb.zythopedia.enums.ServiceMethod;
import ch.fdb.zythopedia.repository.BoughtDrinkRepository;
import ch.fdb.zythopedia.repository.ServiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
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
    private final ServiceRepository serviceRepository;
    private final BoughtDrinkRepository boughtDrinkRepository;
    private final ServiceMapper serviceMapper;

    public ServiceService(ServiceRepository serviceRepository, BoughtDrinkRepository boughtDrinkRepository, ServiceMapper serviceMapper) {
        this.serviceRepository = serviceRepository;
        this.boughtDrinkRepository = boughtDrinkRepository;
        this.serviceMapper = serviceMapper;
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

    public Optional<ServiceDto> findById(long serviceId) {
        return serviceRepository.findById(serviceId)
                .map(serviceMapper::toDto);
    }

    public Service updatePrice(ServiceDto serviceDto) {
        return serviceRepository.findById(serviceDto.getId())
                .map(service -> service.setSellingPrice(serviceDto.getSellingPrice()))
                .map(serviceRepository::save)
                .orElse(null);
    }

    public void delete(Service service) {
        serviceRepository.delete(service);
    }

    public List<ServiceDto> updateServices(Long boughtDrinkId, List<ServiceDto> services) {
        var boughtDrink = boughtDrinkRepository.findById(boughtDrinkId).orElseThrow(EntityNotFoundException::new);

        // Récupérer les services existants
        var existingServices = boughtDrink.getServices();

        // Identifier les IDs des services à supprimer (ceux qui ne sont plus dans la nouvelle liste)
        var servicesIdToDelete = existingServices.stream()
                .map(Service::getId)
                .filter(id -> services.stream()
                        .noneMatch(serviceDto -> serviceDto.getId() != null && serviceDto.getId().equals(id)))
                .toList();

        var upToDataServices = new ArrayList<Service>();
        // Mettre à jour les services existants et créer les nouveaux
        for (ServiceDto serviceDto : services) {
            if (Optional.ofNullable(serviceDto.getId()).orElse(0L) > 0L) {
                // Pour les services existants
                existingServices.stream()
                        .filter(s -> s.getId().equals(serviceDto.getId()))
                        .findFirst()
                        .map(existingService -> existingService
                                .setVolumeInCl(serviceDto.getVolumeInCl())
                                .setSellingPrice(serviceDto.getSellingPrice()))
                        .map(serviceRepository::save)
                        .ifPresent(upToDataServices::add);
            } else {
                // Pour les nouveaux services
                var service = new Service();
                service.setBoughtDrink(boughtDrink);
                service.setVolumeInCl(serviceDto.getVolumeInCl());
                service.setSellingPrice(serviceDto.getSellingPrice());
                upToDataServices.add(serviceRepository.save(service));
            }
        }

        // Supprimer les services qui ne sont plus présents
        servicesIdToDelete.forEach(serviceRepository::deleteById);

        return upToDataServices.stream().map(serviceMapper::toDto).toList();
    }
}