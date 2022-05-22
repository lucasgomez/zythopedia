package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.ColorDto;
import ch.fdb.zythopedia.dto.creation.CreateColorDto;
import ch.fdb.zythopedia.entity.Color;
import ch.fdb.zythopedia.repository.ColorRepository;
import liquibase.pro.packaged.M;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public Color create(ColorDto colorDto) {
        return create(colorDto.getName(), colorDto.getDescription());
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

    public Color update(ColorDto colorData) {
        var colorToUpdate = findById(colorData.getId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No color of id %s could be found", colorData.getId())));

        try {
            return colorRepository.save(colorToUpdate
                    .setName(colorData.getName())
                    .setDescription(colorData.getDescription()));
        } catch (PropertyValueException exception) {
            log.error(String.format("Failed to update color %s (field %s)", colorData.getId(), exception.getPropertyName()), exception);
            throw new IllegalArgumentException(String.format("Failed to update drink %s", colorData.getId()), exception);
        }
    }

    public Void delete(Long colorIdToDelete, Long colorIdToTransferTo) {
        var colorToDelete = colorRepository.findById(colorIdToDelete);
        if (colorToDelete.isEmpty()) {
            log.error(String.format("Could not delete color %s due to self nihilism", colorToDelete));
            return null;
        }
        if (Objects.nonNull(colorIdToTransferTo)) {
            var colorToTransferTo = colorRepository.findById(colorIdToTransferTo)
                    .orElseThrow(() -> new IllegalArgumentException(String.format(
                            "Could not transfer color %s to color %s due to self nihilism",
                            colorIdToDelete,
                            colorIdToTransferTo)));
            colorToDelete.get().getDrinks().forEach(drink -> drink.setColor(colorToTransferTo));
            log.info(String.format("Transfered drinks from color id %s to color id %s", colorIdToDelete, colorIdToTransferTo));
        } else {
            colorToDelete.get().getDrinks().forEach(drink -> drink.setColor(null));
            log.info(String.format("Unassociated drinks from color id %s", colorIdToDelete));
        }
        colorRepository.delete(colorToDelete.get());
        log.info(String.format("Deleted color id %s", colorIdToDelete));
        return null;
    }
}
