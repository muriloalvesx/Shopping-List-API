package br.com.anhanguera.ecnocorders.shoppinglist.dtos;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListDTO {
    private Long id;  // Adicione o campo id
    private String nome;
    private List<String> itens;
    private String usuario;

    // Construtor
    public ShoppingListDTO(Long id, String nome, List<String> itens, String usuario) {
        this.id = id;
        this.nome = nome;
        this.itens = (itens != null) ? itens : new ArrayList<>();
        this.usuario = usuario;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public List<String> getItens() {
        return itens;
    }

    public String getUsuario() {
        return usuario;
    }

    // Adicione outros métodos, se necessário
}