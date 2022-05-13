package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.CreateProducerDto;
import ch.fdb.zythopedia.entity.Origin;
import ch.fdb.zythopedia.entity.Producer;
import ch.fdb.zythopedia.repository.ProducerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProducerService {

    private ProducerRepository producerRepository;
    private OriginService originService;

    public ProducerService(ProducerRepository producerRepository, OriginService originService) {
        this.producerRepository = producerRepository;
        this.originService = originService;
    }

    public List<Producer> findAll() {
        return producerRepository.findAll();
    }

    public Optional<Producer> findById(long producerId) {
        return producerRepository.findById(producerId);
    }

    public Producer create(CreateProducerDto createProducerDto) {
        var origin = originService.findByIdOrName(
                createProducerDto.getOriginId(),
                createProducerDto.getOriginShortName());

        return producerRepository.save(Producer.builder()
                .name(createProducerDto.getName())
                .origin(origin.orElse(null))
                .build());
    }

    public Producer update(long producerId, CreateProducerDto createProducerDto) {
        var producerToUpdate = producerRepository.findById(producerId)
                .orElseThrow();
        var origin = originService.findByIdOrName(
                createProducerDto.getOriginId(),
                createProducerDto.getOriginShortName());

        return producerRepository.save(producerToUpdate
                .setName(createProducerDto.getName())
                .setOrigin(origin.orElse(null)));
    }

    public void delete(long producerId) {
        var producerToDelete = producerRepository.findById(producerId)
                .orElseThrow();

//        producerToDelete.getDrinks()
//                .forEach(drink -> drink.setProducer(null));

        producerRepository.delete(producerToDelete);
    }

}
