package br.com.anhanguera.ecnocorders.shoppinglist.repository;

import br.com.anhanguera.ecnocorders.shoppinglist.entities.UsersModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UsersModel, Integer> {
    Optional<UsersModel> findByLogin(String login);
}