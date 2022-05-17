package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.creation.CreateProducerDto;
import ch.fdb.zythopedia.dto.ProducerDto;
import ch.fdb.zythopedia.dto.mapper.ProducerMapper;
import ch.fdb.zythopedia.service.ProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class ProducerController {

    private ProducerService producerService;
    private ProducerMapper producerMapper;

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

    @GetMapping(value = "/producer/{styleId}")
    public ProducerDto findById(@PathVariable long styleId) {
        return producerService.findById(styleId)
                .map(producerMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    @PostMapping(value = "/producer")
    public ProducerDto create(@RequestBody CreateProducerDto createProducerDto) {
        return producerMapper.toDto(
                producerService.create(createProducerDto));
    }

    @PutMapping(value = "/producer/{styleId}")
    public ProducerDto update(@PathVariable long styleId, @RequestBody CreateProducerDto createProducerDto) {
        return producerMapper.toDto(
                producerService.update(styleId, createProducerDto));
    }

    @DeleteMapping(value = "/producer/{producerId}")
    public void delete(@PathVariable long producerId) {
        producerService.delete(producerId);
    }

}
