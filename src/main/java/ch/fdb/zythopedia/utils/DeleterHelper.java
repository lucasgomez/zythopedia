package ch.fdb.zythopedia.utils;

import ch.fdb.zythopedia.dto.IdOrNameDto;
import ch.fdb.zythopedia.entity.NamedEntity;
import ch.fdb.zythopedia.repository.IdOrNameDtoFinderAndDeleter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
public final class DeleterHelper {
    private DeleterHelper() {
    }

    public static <E extends NamedEntity, C> Void delete(String entityName, String childrenEntityName, Long entityIdToDelete, IdOrNameDto entityIdToTransferTo,
                                                         IdOrNameDtoFinderAndDeleter<E> repository, Function<E, Collection<C>> childrenMapper,
                                                         BiFunction<C, E, C> parentSetter) {
        return delete(entityName, childrenEntityName, entityIdToDelete, entityIdToTransferTo,
                repository,
                childrenMapper, parentSetter,
                null, null);
    }

    public static <E extends NamedEntity, C> Void delete(String entityName, String childrenEntityName, Long entityIdToDelete, IdOrNameDto entityIdToTransferTo,
                                                         IdOrNameDtoFinderAndDeleter<E> repository,
                                                         Function<E, Collection<C>> childrenMapper, BiFunction<C, E, C> parentSetter,
                                                         Function<E, Collection<E>> childrenMapper2, BiFunction<E, E, E> parentSetter2) {
        var entityToDelete = repository.findById(entityIdToDelete);
        if (entityToDelete.isEmpty()) {
            log.error(String.format("Could not delete %s %s due to self nihilism", entityName, entityIdToDelete));
            return null;
        }
        if (Objects.nonNull(entityIdToTransferTo)) {
            var entityToTransferTo = Optional.ofNullable(entityIdToTransferTo.getId())
                    .flatMap(repository::findById)
                    .or(() -> repository.findByName(entityIdToTransferTo.getName()))
                    .orElseThrow(() -> new IllegalArgumentException(String.format(
                            "Could not transfer %s %s to %s %s due to self nihilism",
                            entityName,
                            entityIdToDelete,
                            entityName,
                            entityIdToTransferTo)));

            DeleterHelper.transfer(entityName, childrenEntityName, entityToDelete.get(), entityToTransferTo, childrenMapper, parentSetter);
            if (null != childrenMapper2) {
                DeleterHelper.transfer(entityName, childrenEntityName, entityToDelete.get(), entityToTransferTo, childrenMapper2, parentSetter2);
            }

        } else {
            DeleterHelper.transfer(entityName, childrenEntityName, entityToDelete.get(), null, childrenMapper, parentSetter);
            if (null != childrenMapper2) {
                DeleterHelper.transfer(entityName, childrenEntityName, entityToDelete.get(), null, childrenMapper2, parentSetter2);
            }
        }
        repository.delete(entityToDelete.get());
        log.info(String.format("Deleted color id %s", entityIdToDelete));
        return null;
    }

    private static <E extends NamedEntity, C> void transfer(String entityName, String childrenEntityName, E entityToDelete, E entityToTransferTo, Function<E, Collection<C>> childrenMapper, BiFunction<C, E, C> parentSetter) {
        childrenMapper.apply(entityToDelete).forEach(child -> parentSetter.apply(child, entityToTransferTo));
        log.info(String.format("Transfered %s from %s id %s to %s", childrenEntityName, entityName,
                entityToDelete.getId(), Optional.ofNullable(entityToTransferTo)
                        .map(NamedEntity::getId)
                        .map(id -> String.format("id %s", id))
                        .orElse("oblivion")));
    }
}
