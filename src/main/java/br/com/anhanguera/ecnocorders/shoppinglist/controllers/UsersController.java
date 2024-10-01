package br.com.anhanguera.ecnocorders.shoppinglist.controllers;

import br.com.anhanguera.ecnocorders.shoppinglist.dtos.LoginDTO;
import br.com.anhanguera.ecnocorders.shoppinglist.dtos.RegisterDTO;
import br.com.anhanguera.ecnocorders.shoppinglist.dtos.UserResponseDTO;
import br.com.anhanguera.ecnocorders.shoppinglist.services.UsersService;
import br.com.anhanguera.ecnocorders.shoppinglist.exceptions.UserAlreadyExistsException;
import br.com.anhanguera.ecnocorders.shoppinglist.exceptions.UserNotFoundException;
import br.com.anhanguera.ecnocorders.shoppinglist.dtos.ErrorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDTO registerDTO) {
        try {
            UserResponseDTO registeredUser = usersService.registerUser(registerDTO);
            return ResponseEntity.status(201).body(registeredUser);
        } catch (UserAlreadyExistsException exception) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(
                    exception.getMessage(),
                    exception.getCode()
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
        try {
            UserResponseDTO authenticatedUser = usersService.authenticate(loginDTO);
            return ResponseEntity.ok(authenticatedUser);
        } catch (UserNotFoundException exception) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(
                    exception.getMessage(),
                    exception.getCode()
            ));
        }
    }
}