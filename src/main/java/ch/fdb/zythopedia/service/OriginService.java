package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.IdOrNameDto;
import ch.fdb.zythopedia.dto.OriginDto;
import ch.fdb.zythopedia.dto.StyleDto;
import ch.fdb.zythopedia.dto.creation.CreateOriginDto;
import ch.fdb.zythopedia.dto.mapper.OriginMapper;
import ch.fdb.zythopedia.entity.Origin;
import ch.fdb.zythopedia.entity.Producer;
import ch.fdb.zythopedia.repository.OriginRepository;
import ch.fdb.zythopedia.utils.DeleterHelper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OriginService {

    private OriginRepository originRepository;
    private OriginMapper originMapper;

    public OriginService(OriginRepository originRepository, OriginMapper originMapper) {
        this.originRepository = originRepository;
        this.originMapper = originMapper;
    }

    public List<Origin> findAll() {
        return originRepository.findAll();
    }

    public List<OriginDto> findAllDto() {
        return originRepository.findAll().stream()
                .map(originMapper::toDto)
                .sorted(Comparator.comparing(OriginDto::getName))
                .collect(Collectors.toList());
    }

    public Optional<Origin> findById(long originId) {
        return originRepository.findById(originId);
    }

    public Origin create(OriginDto originDto) {
        return create(originMapper.toCreateDto(originDto));
    }

    public Origin create(CreateOriginDto createOriginDto) {
        return originRepository.save(Origin.builder()
                .name(createOriginDto.getName())
                .shortName(createOriginDto.getShortName())
                .flag(createOriginDto.getFlag())
                .build());
    }

    public Origin update(OriginDto originDto) {
        return update(originDto.getId(), originMapper.toCreateDto(originDto));
    }

    public Origin update(long originId, CreateOriginDto createOriginDto) {
        var originToUpdate = originRepository.findById(originId)
                .orElseThrow();

        return originRepository.save(originToUpdate
                .setName(createOriginDto.getName())
                .setShortName(createOriginDto.getShortName())
                .setFlag(createOriginDto.getFlag()));
    }

    public void delete(long originId) {
        delete(originId, null);
    }

    public Void delete(Long originIdToDelete, IdOrNameDto originIdToTransferTo) {
        return DeleterHelper.delete("Origin", "Producer", originIdToDelete, originIdToTransferTo,
                originRepository, Origin::getProducers, Producer::setOrigin);
    }

    public Optional<Origin> findByIdOrName(Long originId, String originShortName, String originLongName) {
        return Optional.ofNullable(originId)
                .flatMap(originRepository::findById)
                .or(() -> Optional.ofNullable(originShortName)
                        .flatMap(originRepository::findByShortName))
                .or(() -> Optional.ofNullable(originLongName)
                        .flatMap(originRepository::findByName));
    }
}
