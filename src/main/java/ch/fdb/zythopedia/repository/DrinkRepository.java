package ch.fdb.zythopedia.repository;

import ch.fdb.zythopedia.entity.Drink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrinkRepository extends JpaRepository<Drink, Long> {

    Optional<Drink> findByNameIgnoreCaseAndProducerNameIgnoreCase(String name, String producerName);
}
