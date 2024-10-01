package br.com.anhanguera.ecnocorders.shoppinglist.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {
    private String login;
    private String password;
}