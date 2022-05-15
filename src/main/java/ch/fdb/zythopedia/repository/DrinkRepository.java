package ch.fdb.zythopedia.repository;

import ch.fdb.zythopedia.entity.Drink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrinkRepository extends JpaRepository<Drink, Long> {

}
