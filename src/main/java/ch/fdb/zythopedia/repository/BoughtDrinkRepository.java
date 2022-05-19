package ch.fdb.zythopedia.repository;

import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.entity.Edition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BoughtDrinkRepository extends JpaRepository<BoughtDrink, Long> {

    Optional<BoughtDrink> findByCodeAndEditionName(String code, String editionName);

    List<BoughtDrink> findByEdition(Edition currentEdition);
}
