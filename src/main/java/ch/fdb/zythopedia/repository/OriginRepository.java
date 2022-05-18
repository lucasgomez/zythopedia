package ch.fdb.zythopedia.repository;

import ch.fdb.zythopedia.entity.Origin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OriginRepository extends JpaRepository<Origin, Long> {

    Optional<Origin> findByShortName(String originShortName);

    Optional<Origin> findByName(String name);
}
