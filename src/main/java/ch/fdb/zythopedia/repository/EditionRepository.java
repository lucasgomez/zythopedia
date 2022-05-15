package ch.fdb.zythopedia.repository;

import ch.fdb.zythopedia.entity.Edition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EditionRepository extends JpaRepository<Edition, Long> {

}
