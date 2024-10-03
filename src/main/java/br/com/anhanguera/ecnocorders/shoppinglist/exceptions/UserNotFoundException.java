package br.com.anhanguera.ecnocorders.shoppinglist.exceptions;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {

    private String code;

    public UserNotFoundException() {
        super("Usuário ou senha incorreto!");
        this.code = "USER_NOT_FOUND";
    }

}
