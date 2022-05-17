package ch.fdb.zythopedia.repository;

import ch.fdb.zythopedia.entity.Edition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EditionRepository extends JpaRepository<Edition, Long> {
    Optional<Edition> findByName(String currentEditionName);
}
