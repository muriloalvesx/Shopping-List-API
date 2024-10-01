package br.com.anhanguera.ecnocorders.shoppinglist.dtos;

public record ErrorResponseDTO(
        String message,
        String code
){
}