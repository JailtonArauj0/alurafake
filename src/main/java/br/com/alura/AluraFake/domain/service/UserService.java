package br.com.alura.AluraFake.domain.service;

import br.com.alura.AluraFake.dtos.request.NewUserDTO;
import br.com.alura.AluraFake.dtos.response.UserListItemDTO;
import br.com.alura.AluraFake.exception.CustomException;
import br.com.alura.AluraFake.domain.repository.UserRepository;
import br.com.alura.AluraFake.domain.model.user.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveUser(NewUserDTO newUser) {
        if(userRepository.existsByEmail(newUser.getEmail())) {
            throw new CustomException("This email is already registered");
        }
        newUser.setPassword(new BCryptPasswordEncoder().encode(newUser.getPassword()));
        User user = newUser.toModel();
        userRepository.save(user);
    }

    public List<UserListItemDTO> listAllUsers() {
        return userRepository.findAll().stream().map(UserListItemDTO::new).toList();
    }
}
