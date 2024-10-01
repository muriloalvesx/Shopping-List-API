package br.com.anhanguera.ecnocorders.shoppinglist.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private String login;
    private String token;

    // Construtor com argumentos
    public UserResponseDTO(String login, String token) {
        this.login = login;
        this.token = token;
    }
}