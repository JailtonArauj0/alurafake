package br.com.alura.AluraFake.services;

import br.com.alura.AluraFake.dtos.request.NewUserDTO;
import br.com.alura.AluraFake.dtos.response.UserListItemDTO;
import br.com.alura.AluraFake.exception.CustomException;
import br.com.alura.AluraFake.repositories.UserRepository;
import br.com.alura.AluraFake.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(NewUserDTO newUser) {
        if(userRepository.existsByEmail(newUser.getEmail())) {
            throw new CustomException("Email já cadastrado no sistema");
        }
        newUser.setPassword(new BCryptPasswordEncoder().encode(newUser.getPassword()));
        User user = newUser.toModel();
        userRepository.save(user);
    }

    public List<UserListItemDTO> listAllUsers() {
        return userRepository.findAll().stream().map(UserListItemDTO::new).toList();
    }
}
