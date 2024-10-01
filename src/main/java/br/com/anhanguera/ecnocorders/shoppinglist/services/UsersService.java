package br.com.anhanguera.ecnocorders.shoppinglist.services;

import br.com.anhanguera.ecnocorders.shoppinglist.dtos.LoginDTO;
import br.com.anhanguera.ecnocorders.shoppinglist.dtos.RegisterDTO;
import br.com.anhanguera.ecnocorders.shoppinglist.dtos.UserResponseDTO;
import br.com.anhanguera.ecnocorders.shoppinglist.exceptions.UserAlreadyExistsException;
import br.com.anhanguera.ecnocorders.shoppinglist.exceptions.UserNotFoundException;
import br.com.anhanguera.ecnocorders.shoppinglist.entities.UsersModel;
import br.com.anhanguera.ecnocorders.shoppinglist.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    private final UserRepository usersRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UserRepository usersRepository,
                        AuthenticationManager authenticationManager,
                        JwtTokenProvider tokenProvider,
                        PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;

        // Log para verificar a instância do PasswordEncoder
        System.out.println("PasswordEncoder class: " + passwordEncoder.getClass().getName());
    }

    public UserResponseDTO registerUser(RegisterDTO registerDTO) {
        if (usersRepository.findByLogin(registerDTO.getLogin()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        UsersModel newUser = new UsersModel();
        newUser.setLogin(registerDTO.getLogin());
        // Codificar a senha usando BCryptPasswordEncoder
        newUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        usersRepository.save(newUser);

        return new UserResponseDTO(newUser.getLogin(), null);
    }

    public UserResponseDTO authenticate(LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getLogin(),
                            loginDTO.getPassword()
                    )
            );

            String token = tokenProvider.generateToken(authentication);

            return new UserResponseDTO(loginDTO.getLogin(), token);
        } catch (AuthenticationException e) {
            throw new UserNotFoundException();
        }
    }
}