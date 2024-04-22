package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.entity.Edition;
import ch.fdb.zythopedia.repository.EditionRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class EditionService {

    @Getter
    @Value("${edition.current.name}")
    private String currentEditionName;

    private final EditionRepository editionRepository;

    public EditionService(EditionRepository editionRepository) {
        this.editionRepository = editionRepository;
    }

    public Edition getCurrentEdition() {
        return editionRepository.findByName(currentEditionName)
                .orElseGet(() -> editionRepository.save(Edition.builder().name(currentEditionName).build()));
    }

    public Optional<Edition> findEdition(String editionName) {
        return editionRepository.findByName(editionName);
    }
}
