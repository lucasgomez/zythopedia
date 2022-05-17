package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.creation.CreateOriginDto;
import ch.fdb.zythopedia.entity.Origin;
import ch.fdb.zythopedia.repository.OriginRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OriginService {

    private OriginRepository originRepository;

    public OriginService(OriginRepository originRepository) {
        this.originRepository = originRepository;
    }

    public List<Origin> findAll() {
        return originRepository.findAll();
    }

    public Optional<Origin> findById(long originId) {
        return originRepository.findById(originId);
    }

    public Origin create(CreateOriginDto createOriginDto) {
        return originRepository.save(Origin.builder()
                .name(createOriginDto.getName())
                .shortName(createOriginDto.getShortName())
                .flag(createOriginDto.getFlag())
                .build());
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
        var originToDelete = originRepository.findById(originId)
                .orElseThrow();

        originToDelete.getProducers()
                .forEach(producer -> producer.setOrigin(null));

        originRepository.delete(originToDelete);
    }

    public Optional<Origin> findByIdOrName(Long originId, String originShortName) {
        return Optional.ofNullable(originId)
                .flatMap(originRepository::findById)
                .or(() -> Optional.ofNullable(originShortName)
                        .flatMap(originRepository::findByShortName));
    }
}
