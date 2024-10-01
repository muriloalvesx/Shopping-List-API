package br.com.anhanguera.ecnocorders.shoppinglist.repository;

import br.com.anhanguera.ecnocorders.shoppinglist.entities.ItemList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<ItemList, Long>{
}
