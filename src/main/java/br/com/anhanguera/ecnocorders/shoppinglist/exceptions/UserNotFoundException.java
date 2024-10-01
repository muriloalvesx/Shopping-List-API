package br.com.anhanguera.ecnocorders.shoppinglist.exceptions;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {

    private String code;

    public UserNotFoundException() {
        super("O usuário inserido não existe!");
        this.code = "USER_NOT_FOUND";
    }

}