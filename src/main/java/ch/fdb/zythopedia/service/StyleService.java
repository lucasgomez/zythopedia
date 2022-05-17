package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.creation.CreateStyleDto;
import ch.fdb.zythopedia.entity.Style;
import ch.fdb.zythopedia.repository.StyleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
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

    public Style create(CreateStyleDto createStyleDto) {
        var parent = Optional.ofNullable(createStyleDto.getParentStyleId())
                .flatMap(styleRepository::findById)
                .orElseGet(() -> Optional.ofNullable(createStyleDto.getParentName())
                        .flatMap(styleRepository::findByName)
                        .orElse(null));

        return styleRepository.save(Style.builder()
                .name(createStyleDto.getName())
                .description(createStyleDto.getDescription())
                .parent(parent)
                .build());
    }

    public Style update(long styleId, CreateStyleDto createStyleDto) {
        var styleToUpdate = styleRepository.findById(styleId)
                .orElseThrow();

        var newParent = Optional.ofNullable(createStyleDto.getParentStyleId())
                .flatMap(styleRepository::findById)
                .orElseGet(() -> Optional.ofNullable(createStyleDto.getParentName())
                        .flatMap(styleRepository::findByName)
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

//        styleToDelete.getDrinks()
//                .forEach(drink -> drink.setStyle(null));
        styleToDelete.getChildren()
                .forEach(child -> child.setParent(styleToDelete.getParent()));

        styleRepository.delete(styleToDelete);
    }

    private void checkForCycles(Style styleToUpdate, Style newParent) {
        if (null == newParent) {
            return;
        }
        if (styleToUpdate.getId() == newParent.getId()) {
            throw new IllegalArgumentException("Marty, you can't be your own father!");
        }
        if (getDescendants(styleToUpdate).stream()
                .map(Style::getId)
                .anyMatch(id -> id == newParent.getId())) {
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
                .collect(Collectors.toList()));

        return children;
    }

}
