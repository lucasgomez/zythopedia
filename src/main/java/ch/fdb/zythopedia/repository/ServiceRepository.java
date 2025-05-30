package ch.fdb.zythopedia.repository;

import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByBoughtDrink(BoughtDrink boughtDrink);

    List<Service> findByBoughtDrinkId(Long id);

    List<Service> findByBoughtDrinkDrinkNameAndBoughtDrinkDrinkProducerNameAndVolumeInCl(String drinkName, String producerName, Long volumeInCl);

}
