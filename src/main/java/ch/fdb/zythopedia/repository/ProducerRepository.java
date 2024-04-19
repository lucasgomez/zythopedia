package ch.fdb.zythopedia.repository;

import ch.fdb.zythopedia.entity.Producer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProducerRepository extends JpaRepository<Producer, Long>, IdOrNameDtoFinderAndDeleter<Producer> {
    List<Producer> findByOriginId(long originId);
}
