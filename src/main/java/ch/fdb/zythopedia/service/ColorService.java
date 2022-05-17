package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.creation.CreateColorDto;
import ch.fdb.zythopedia.entity.Color;
import ch.fdb.zythopedia.repository.ColorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ColorService {

    private ColorRepository colorRepository;

    public ColorService(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    public List<Color> findAll() {
        return colorRepository.findAll();
    }

    public Optional<Color> findById(long colorId) {
        return colorRepository.findById(colorId);
    }

    public Color create(CreateColorDto createColorDto) {
        return create(createColorDto.getName(), createColorDto.getDescription());
    }

    public Color create(String name, String description) {
        return colorRepository.save(Color.builder()
                    .name(name)
                    .description(description)
                    .build());
    }

    public Color update(long colorId, String name, String description) {
        var colorToUpdate = colorRepository.findById(colorId)
                .orElseThrow();

        return colorRepository.save(colorToUpdate
                .setName(name)
                .setDescription(description));
    }

    public void delete(long colorId) {
        var colorToDelete = colorRepository.findById(colorId)
                .orElseThrow();

        colorToDelete.getDrinks()
                .forEach(drink -> drink.setColor(null));

        colorRepository.delete(colorToDelete);
    }
}
