package br.com.anhanguera.ecnocorders.shoppinglist.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "shopping_lists") // Nome da tabela no banco de dados
public class ShoppingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Renomeado para user_id para ficar mais claro
    private UsersModel user; // Alterado para UsersModel

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL)
    private List<ItemList> items;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public UsersModel getUser() {
        return user; // Retornando UsersModel
    }

    public void setUser(UsersModel user) { // Aceitando UsersModel
        this.user = user;
    }

    public List<ItemList> getItems() {
        return items;
    }

    public void setItems(List<ItemList> items) {
        this.items = items;
    }
}