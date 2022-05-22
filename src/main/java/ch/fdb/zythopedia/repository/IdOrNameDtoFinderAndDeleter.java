package ch.fdb.zythopedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/**
 * Conceptual nightmare for melted brain
 */
@NoRepositoryBean
public interface IdOrNameDtoFinderAndDeleter<T> extends JpaRepository<T, Long> {
    Optional<T> findByName(String name);
}
