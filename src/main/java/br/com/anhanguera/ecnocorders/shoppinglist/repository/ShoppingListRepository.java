package br.com.anhanguera.ecnocorders.shoppinglist.repository;

import br.com.anhanguera.ecnocorders.shoppinglist.entities.ShoppingList;
import br.com.anhanguera.ecnocorders.shoppinglist.entities.UsersModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {
    List<ShoppingList> findByUser(UsersModel login);
    Optional<ShoppingList> findByIdAndUser_Login(Long id, String username);
}