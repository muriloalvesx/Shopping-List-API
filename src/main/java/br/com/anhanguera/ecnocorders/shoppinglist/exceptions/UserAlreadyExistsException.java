package br.com.anhanguera.ecnocorders.shoppinglist.exceptions;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {

    private String code;

    public UserAlreadyExistsException() {
        super("O usuário inserido já existe!");
        this.code = "USER_ALREADY_EXISTS";
    }

}