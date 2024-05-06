package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.IdOrNameDto;
import ch.fdb.zythopedia.dto.ProducerDto;
import ch.fdb.zythopedia.dto.creation.CreateOriginDto;
import ch.fdb.zythopedia.dto.creation.CreateProducerDto;
import ch.fdb.zythopedia.dto.mapper.ProducerMapper;
import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.entity.Drink;
import ch.fdb.zythopedia.entity.Producer;
import ch.fdb.zythopedia.repository.ProducerRepository;
import ch.fdb.zythopedia.utils.DeleterHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProducerService {

    private final ProducerRepository producerRepository;
    private final ProducerMapper producerMapper;
    private final OriginService originService;

    public ProducerService(ProducerRepository producerRepository, ProducerMapper producerMapper, OriginService originService) {
        this.producerRepository = producerRepository;
        this.producerMapper = producerMapper;
        this.originService = originService;
    }

    public List<Producer> findAll() {
        return producerRepository.findAll();
    }

    public List<ProducerDto> findAllDto() {
        return producerRepository.findAll().stream()
                .map(producerMapper::toDto)
                .sorted(Comparator.comparing(ProducerDto::getName))
                .collect(Collectors.toList());
    }

    public Optional<Producer> findById(long producerId) {
        return producerRepository.findById(producerId);
    }

    public Producer create(ProducerDto producerDto) {
        return create(producerMapper.toCreateDto(producerDto));
    }

    public Producer create(CreateProducerDto createProducerDto) {
        var origin = originService.findByIdOrName(
                createProducerDto.getOriginId(),
                Optional.ofNullable(createProducerDto.getOriginName())
                        .filter(String::isBlank)
                        .or(() -> Optional.ofNullable(createProducerDto.getOriginShortName()))
                        .map(ProducerService::formatOriginShortName)
                        .orElse(""),
                createProducerDto.getOriginName());

        if (origin.isEmpty() &&
                (null != createProducerDto.getOriginShortName() || null != createProducerDto.getOriginName())) {
            origin = Optional.ofNullable(createProducerDto.getOriginShortName())
                    .or(() -> Optional.of(createProducerDto.getOriginName()))
                    .map(ProducerService::formatOriginShortName)
                    .map(shortName -> originService.create(CreateOriginDto.builder()
                            .shortName(shortName)
                            .name(createProducerDto.getOriginName())
                            .build()));
        }

        return producerRepository.save(Producer.builder()
                .name(createProducerDto.getName())
                .origin(origin.orElse(null))
                .build());
    }

    private static String formatOriginShortName(String name) {
        return name.substring(0, Math.min(4, name.length()));
    }

    public Producer update(ProducerDto producerDto) {
        return update(producerDto.getId(), producerMapper.toCreateDto(producerDto));
    }

    public Producer update(long producerId, CreateProducerDto createProducerDto) {
        var producerToUpdate = producerRepository.findById(producerId)
                .orElseThrow();
        var origin = originService.findByIdOrName(
                createProducerDto.getOriginId(),
                createProducerDto.getOriginShortName(),
                createProducerDto.getOriginName());

        return producerRepository.save(producerToUpdate
                .setName(createProducerDto.getName())
                .setOrigin(origin.orElse(null)));
    }

    public void delete(long producerId) {
        delete(producerId, null);
    }

    public Void delete(Long producerIdToDelete, IdOrNameDto producerIdToTransferTo) {
        return DeleterHelper.delete("Producer", "Drink", producerIdToDelete, producerIdToTransferTo,
                producerRepository, Producer::getDrinks, Drink::setProducer);
    }

    public Producer findOrCreate(String producerName, String originShortName) {
        return producerRepository.findByNameIgnoreCase(producerName)
                .orElseGet(() -> create(CreateProducerDto.builder()
                        .name(producerName)
                        .originName(originShortName)
                        .originShortName(originShortName)
                        .build()));
    }
}
