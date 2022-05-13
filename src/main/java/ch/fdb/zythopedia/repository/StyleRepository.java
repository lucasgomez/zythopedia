package ch.fdb.zythopedia.repository;

import ch.fdb.zythopedia.entity.Style;
import liquibase.pro.packaged.U;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StyleRepository extends JpaRepository<Style, Long> {

    Optional<Style> findByName(String name);
}
