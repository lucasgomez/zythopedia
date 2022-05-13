package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.entity.Style;
import ch.fdb.zythopedia.repository.StyleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class StyleService {

    private StyleRepository styleRepository;

    public StyleService(StyleRepository styleRepository) {
        this.styleRepository = styleRepository;
    }

    public Optional<Style> findById(long styleId) {
        return styleRepository.findById(styleId);
    }

    public List<Style> findAll() {
        return styleRepository.findAll();
    }
}
