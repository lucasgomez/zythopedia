package ch.fdb.zythopedia.utils;

import liquibase.pro.packaged.C;
import liquibase.pro.packaged.E;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
public final class DeleterHelper {
    private DeleterHelper() {
    }

    public static <E, C> Void delete(String entityName, Long entityIdToDelete, Long entityIdToTransferTo,
                                     CrudRepository<E, Long> repository, Function<E, Collection<C>> childrenMapper,
                                     BiFunction<C, E, C> parentSetter) {
        var entityToDelete = repository.findById(entityIdToDelete);
        if (entityToDelete.isEmpty()) {
            log.error(String.format("Could not delete %s %s due to self nihilism", entityName, entityToDelete));
            return null;
        }
        if (Objects.nonNull(entityIdToTransferTo)) {
            var entityToTransferTo = repository.findById(entityIdToTransferTo)
                    .orElseThrow(() -> new IllegalArgumentException(String.format(
                            "Could not transfer %s %s to %s %s due to self nihilism",
                            entityName,
                            entityIdToDelete,
                            entityName,
                            entityIdToTransferTo)));
            childrenMapper.apply(entityToDelete.get()).forEach(child -> parentSetter.apply(child, entityToTransferTo));
            log.info(String.format("Transfered drinks from color id %s to color id %s", entityIdToDelete, entityIdToTransferTo));
        } else {
            childrenMapper.apply(entityToDelete.get()).forEach(child -> parentSetter.apply(child, null));
            log.info(String.format("Unassociated drinks from color id %s", entityIdToDelete));
        }
        repository.delete(entityToDelete.get());
        log.info(String.format("Deleted color id %s", entityIdToDelete));
        return null;
    }
}
