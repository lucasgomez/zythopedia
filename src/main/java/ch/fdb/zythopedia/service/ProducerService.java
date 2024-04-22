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
    private final BoughtDrinkService boughtDrinkService;

    public ProducerService(ProducerRepository producerRepository, ProducerMapper producerMapper, OriginService originService, BoughtDrinkService boughtDrinkService) {
        this.producerRepository = producerRepository;
        this.producerMapper = producerMapper;
        this.originService = originService;
        this.boughtDrinkService = boughtDrinkService;
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
                createProducerDto.getOriginShortName(),
                createProducerDto.getOriginName());

        if (origin.isEmpty() &&
                (null != createProducerDto.getOriginShortName() || null != createProducerDto.getOriginName())) {
            origin = Optional.ofNullable(createProducerDto.getOriginShortName())
                    .or(() -> Optional.of(createProducerDto.getOriginName()))
                    .map(name -> name.substring(0, Math.min(4, name.length())))
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

    public List<ProducerDto> findProducersWithService() {
        return boughtDrinkService.findCurrentEditionList(
                boughtDrink -> Optional.ofNullable(boughtDrink)
                        .map(BoughtDrink::getDrink)
                        .map(Drink::getProducer)
                        .orElse(null),
                producerMapper::toDto
        );
    }

    public Producer findOrCreate(String producerName, String originShortName) {
        return producerRepository.findByNameIgnoreCase(producerName)
                .orElseGet(() -> create(CreateProducerDto.builder()
                        .name(producerName)
                        .originShortName(originShortName)
                        .build()));
    }
}
