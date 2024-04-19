package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.ColorDto;
import ch.fdb.zythopedia.dto.IdOrNameDto;
import ch.fdb.zythopedia.dto.creation.CreateColorDto;
import ch.fdb.zythopedia.dto.mapper.ColorMapper;
import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.entity.Color;
import ch.fdb.zythopedia.entity.Drink;
import ch.fdb.zythopedia.repository.ColorRepository;
import ch.fdb.zythopedia.utils.DeleterHelper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ColorService {

    private final ColorRepository colorRepository;
    private final ColorMapper colorMapper;
    private final BoughtDrinkService boughtDrinkService;

    public ColorService(
            ColorRepository colorRepository, ColorMapper colorMapper,
            BoughtDrinkService boughtDrinkService) {
        this.colorRepository = colorRepository;
        this.colorMapper = colorMapper;
        this.boughtDrinkService = boughtDrinkService;
    }

    public List<Color> findAll() {
        return colorRepository.findAll();
    }

    public List<ColorDto> findColorsWithService() {
        return boughtDrinkService.findCurrentEditionList(
                boughtDrink -> Optional.ofNullable(boughtDrink)
                        .map(BoughtDrink::getDrink)
                        .map(Drink::getColor)
                        .orElse(null),
                colorMapper::toDto
        );
    }

    public List<ColorDto> findAllDto() {
        return colorRepository.findAll().stream()
                .map(colorMapper::toDto)
                .sorted(Comparator.comparing(ColorDto::getName))
                .collect(Collectors.toList());
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

    public Void delete(Long colorIdToDelete, IdOrNameDto colorIdToTransferTo) {
        return DeleterHelper.delete("Color", "Drink", colorIdToDelete, colorIdToTransferTo,
                colorRepository, Color::getDrinks, Drink::setColor);
    }
}
