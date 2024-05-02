package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.ProducerDto;
import ch.fdb.zythopedia.dto.creation.CreateProducerDto;
import ch.fdb.zythopedia.dto.mapper.ProducerMapper;
import ch.fdb.zythopedia.entity.Producer;
import ch.fdb.zythopedia.exceptions.EntityNotFoundException;
import ch.fdb.zythopedia.service.ProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class ProducerController {

    private final ProducerService producerService;
    private final ProducerMapper producerMapper;

    public ProducerController(ProducerService styleService, ProducerMapper producerMapper) {
        this.producerService = styleService;
        this.producerMapper = producerMapper;
    }

    @GetMapping(value = "/producer")
    public List<ProducerDto> findAll() {
        return producerService.findAll().stream()
                .map(producerMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/edition/current/producer")
    public List<ProducerDto> findProducersWithService() {
        return producerService.findProducersWithService();
    }

    @GetMapping(value = "/producer/{producerId}")
    public ProducerDto findById(@PathVariable long producerId) {
        return producerService.findById(producerId)
                .map(producerMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(producerId, Producer.class.getName()));
    }

    @PostMapping(value = "/producer")
    @Secured("ROLE_MANAGER")
    public ProducerDto create(@RequestBody CreateProducerDto createProducerDto) {
        return producerMapper.toDto(
                producerService.create(createProducerDto));
    }

    @PutMapping(value = "/producer/{styleId}")
    @Secured("ROLE_MANAGER")
    public ProducerDto update(@PathVariable long styleId, @RequestBody CreateProducerDto createProducerDto) {
        return producerMapper.toDto(
                producerService.update(styleId, createProducerDto));
    }

    @DeleteMapping(value = "/producer/{producerId}")
    @Secured("ROLE_MANAGER")
    public void delete(@PathVariable long producerId) {
        producerService.delete(producerId);
    }

}
