package ch.fdb.zythopedia.repository;

import ch.fdb.zythopedia.entity.BoughtDrink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoughtDrinkRepository extends JpaRepository<BoughtDrink, Long> {

    Optional<BoughtDrink> findByCodeAndEditionName(String code, String editionName);
}
