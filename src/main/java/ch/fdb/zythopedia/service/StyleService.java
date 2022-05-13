package ch.fdb.zythopedia.service;

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
    private BeerService beerService;

    public StyleService(StyleRepository styleRepository, BeerService beerService) {
        this.styleRepository = styleRepository;
        this.beerService = beerService;
    }

    public Optional<Style> findById(long styleId) {
        return styleRepository.findById(styleId);
    }

    public List<Style> findAll() {
        return styleRepository.findAll();
    }

    public Style create(String name, String description, Long parentStyleId) {
        var parent = Optional.ofNullable(parentStyleId)
                .flatMap(styleRepository::findById)
                .orElse(null);

        return styleRepository.save(Style.builder()
                .name(name)
                .description(description)
                .parent(parent)
                .build());
    }

    public Style update(long styleId, String newName, String newDescription, Long parentStyleId) {
        var styleToUpdate = styleRepository.findById(styleId)
                .orElseThrow();

        var newParent = Optional.ofNullable(parentStyleId)
                .flatMap(styleRepository::findById)
                .orElse(null);

        checkForCycles(styleToUpdate, newParent);

        return styleRepository
                .save(styleToUpdate
                        .setName(newName)
                        .setDescription(newDescription)
                        .setParent(newParent));
    }

    public void delete(long styleId) {
        var styleToDelete = styleRepository.findById(styleId)
                .orElseThrow();

        beerService.unsetStyle(styleToDelete);
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
