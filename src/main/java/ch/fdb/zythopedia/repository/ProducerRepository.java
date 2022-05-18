package ch.fdb.zythopedia.repository;

import ch.fdb.zythopedia.entity.Producer;
import liquibase.pro.packaged.U;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProducerRepository extends JpaRepository<Producer, Long> {

    List<Producer> findByOriginId(long originId);

    Optional<Producer> findByName(String producerName);
}
