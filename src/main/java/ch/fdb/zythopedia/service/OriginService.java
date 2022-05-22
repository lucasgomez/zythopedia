package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.OriginDto;
import ch.fdb.zythopedia.dto.creation.CreateOriginDto;
import ch.fdb.zythopedia.dto.mapper.OriginMapper;
import ch.fdb.zythopedia.entity.Origin;
import ch.fdb.zythopedia.repository.OriginRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public Void delete(Long originIdToDelete, Long originIdToTransferTo) {
        var originToDelete = originRepository.findById(originIdToDelete);
        if (originToDelete.isEmpty()) {
            log.error(String.format("Could not delete origin %s due to self nihilism", originToDelete));
            return null;
        }
        if (Objects.nonNull(originIdToTransferTo)) {
            var originToTransferTo = originRepository.findById(originIdToTransferTo)
                    .orElseThrow(() -> new IllegalArgumentException(String.format(
                            "Could not transfer origin %s to origin %s due to self nihilism",
                            originIdToDelete,
                            originIdToTransferTo)));
            originToDelete.get().getProducers().forEach(producer -> producer.setOrigin(originToTransferTo));
            log.info(String.format("Transfered producers from origin id %s to origin id %s", originIdToDelete, originIdToTransferTo));
        } else {
            originToDelete.get().getProducers().forEach(producer -> producer.setOrigin(null));
            log.info(String.format("Unassociated producers from origin id %s", originIdToDelete));
        }
        originRepository.delete(originToDelete.get());
        log.info(String.format("Deleted origin id %s", originIdToDelete));
        return null;
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
