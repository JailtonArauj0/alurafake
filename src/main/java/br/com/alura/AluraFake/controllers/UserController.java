package br.com.alura.AluraFake.controllers;

import br.com.alura.AluraFake.dtos.request.LoginRequestDTO;
import br.com.alura.AluraFake.dtos.request.NewUserDTO;
import br.com.alura.AluraFake.dtos.response.LoginResponseDTO;
import br.com.alura.AluraFake.dtos.response.UserListItemDTO;
import br.com.alura.AluraFake.config.TokenService;
import br.com.alura.AluraFake.domain.service.UserService;
import br.com.alura.AluraFake.domain.model.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public UserController(UserService userService, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }


    @Transactional
    @PostMapping("/user/new")
    public ResponseEntity newUser(@RequestBody @Valid NewUserDTO newUser) {
        userService.saveUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/user/all")
    public ResponseEntity<List<UserListItemDTO>> listAllUsers() {
        List<UserListItemDTO> userList = userService.listAllUsers();
        return ResponseEntity.ok(userList);
    }

    @PostMapping("/user/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
        var auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

}
