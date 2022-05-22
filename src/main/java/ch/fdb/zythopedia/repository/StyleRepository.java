package ch.fdb.zythopedia.repository;

import ch.fdb.zythopedia.entity.Style;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StyleRepository extends JpaRepository<Style, Long>, IdOrNameDtoFinderAndDeleter<Style> {
}
