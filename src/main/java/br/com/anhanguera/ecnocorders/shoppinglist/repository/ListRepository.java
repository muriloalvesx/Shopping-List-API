package br.com.anhanguera.ecnocorders.shoppinglist.repository;

import br.com.anhanguera.ecnocorders.shoppinglist.entities.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListRepository extends JpaRepository<ShoppingList, Long> {
}
