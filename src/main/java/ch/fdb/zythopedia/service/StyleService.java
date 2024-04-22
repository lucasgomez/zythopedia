package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.IdOrNameDto;
import ch.fdb.zythopedia.dto.StyleDto;
import ch.fdb.zythopedia.dto.creation.CreateStyleDto;
import ch.fdb.zythopedia.dto.mapper.StyleFlatMapper;
import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.entity.Drink;
import ch.fdb.zythopedia.entity.Style;
import ch.fdb.zythopedia.repository.StyleRepository;
import ch.fdb.zythopedia.utils.DeleterHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StyleService {

    private final StyleRepository styleRepository;
    private final StyleFlatMapper styleFlatMapper;
    private final BoughtDrinkService boughtDrinkService;

    public StyleService(StyleRepository styleRepository, StyleFlatMapper styleFlatMapper, BoughtDrinkService boughtDrinkService) {
        this.styleRepository = styleRepository;
        this.styleFlatMapper = styleFlatMapper;
        this.boughtDrinkService = boughtDrinkService;
    }

    public Optional<Style> findById(long styleId) {
        return styleRepository.findById(styleId);
    }

    public List<Style> findAll() {
        return styleRepository.findAll();
    }

    public List<StyleDto> findAllDto() {
        return styleRepository.findAll().stream()
                .map(styleFlatMapper::toDto)
                .sorted(Comparator.comparing(StyleDto::getName))
                .collect(Collectors.toList());
    }

    public Style create(StyleDto styleDto) {
        return create(styleFlatMapper.toCreateDto(styleDto));
    }

    public Style create(CreateStyleDto createStyleDto) {
        var parent = Optional.ofNullable(createStyleDto.getParentStyleId())
                .flatMap(styleRepository::findById)
                .orElseGet(() -> Optional.ofNullable(createStyleDto.getParentName())
                        .flatMap(styleRepository::findByNameIgnoreCase)
                        .orElse(null));

        return styleRepository.save(Style.builder()
                .name(createStyleDto.getName())
                .description(createStyleDto.getDescription())
                .parent(parent)
                .build());
    }

    public Style update(StyleDto styleDto) {
        return update(styleDto.getId(), styleFlatMapper.toCreateDto(styleDto));
    }

    public Style update(long styleId, CreateStyleDto createStyleDto) {
        var styleToUpdate = styleRepository.findById(styleId)
                .orElseThrow();

        var newParent = Optional.ofNullable(createStyleDto.getParentStyleId())
                .flatMap(styleRepository::findById)
                .orElseGet(() -> Optional.ofNullable(createStyleDto.getParentName())
                        .flatMap(styleRepository::findByNameIgnoreCase)
                        .orElse(null));

        checkForCycles(styleToUpdate, newParent);

        return styleRepository
                .save(styleToUpdate
                        .setName(createStyleDto.getName())
                        .setDescription(createStyleDto.getDescription())
                        .setParent(newParent));
    }

    public void delete(long styleId) {
        var styleToDelete = styleRepository.findById(styleId)
                .orElseThrow();

        styleToDelete.getDrinks()
                .forEach(drink -> drink.setStyle(null));
        styleToDelete.getChildren()
                .forEach(child -> child.setParent(styleToDelete.getParent()));

        styleRepository.delete(styleToDelete);
    }

    public Void delete(Long styleIdToDelete, IdOrNameDto styleIdToTransferTo) {
        return DeleterHelper.delete("Style", "Drink", styleIdToDelete, styleIdToTransferTo,
                styleRepository,
                Style::getDrinks, Drink::setStyle,
                Style::getChildren, Style::setParent);
    }

    private void checkForCycles(Style styleToUpdate, Style newParent) {
        if (null == newParent) {
            return;
        }
        if (styleToUpdate.getId().equals(newParent.getId())) {
            throw new IllegalArgumentException("Marty, you can't be your own father!");
        }
        if (getDescendants(styleToUpdate).stream()
                .map(Style::getId)
                .anyMatch(id -> id.equals(newParent.getId()))) {
            throw new IllegalArgumentException(String.format(
                    "Descendant style %s - %s can't be set as parent style to %s - %s",
                    newParent.getId(), newParent.getName(),
                    styleToUpdate.getId(), styleToUpdate.getName()));
        }
    }

    private Collection<Style> getDescendants(Style styleToUpdate) {
        var children = styleToUpdate.getChildren();

        children.addAll(children.stream()
                .map(this::getDescendants)
                .flatMap(Collection::stream)
                .toList());

        return children;
    }

    public List<StyleDto> findStyleWithService() {
        return boughtDrinkService.findCurrentEditionList(
                boughtDrink -> Optional.ofNullable(boughtDrink)
                        .map(BoughtDrink::getDrink)
                        .map(Drink::getStyle)
                        .orElse(null),
                styleFlatMapper::toDto
        );
    }

    public Style findOrCreate(String name) {
        return styleRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> create(CreateStyleDto.builder().name(name).build()));
    }
}
