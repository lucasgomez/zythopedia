package ch.fdb.zythopedia.repository;

import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.entity.Edition;
import ch.fdb.zythopedia.enums.ServiceMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoughtDrinkRepository extends JpaRepository<BoughtDrink, Long> {

    Optional<BoughtDrink> findByCodeAndEditionName(String code, String editionName);

    List<BoughtDrink> findByEdition(Edition currentEdition);

    List<BoughtDrink> findByDrinkColorIdAndEditionName(Long colorId, String currentEditionName);

    List<BoughtDrink> findByDrinkStyleIdAndEditionName(Long styleId, String currentEditionName);

    List<BoughtDrink> findByDrinkProducerIdAndEditionName(Long producerId, String currentEditionName);

    List<BoughtDrink> findByDrinkProducerOriginIdAndEditionName(Long originId, String currentEditionName);

    List<BoughtDrink> findByServiceMethodAndEditionName(ServiceMethod serviceMethod, String currentEditionName);
}
