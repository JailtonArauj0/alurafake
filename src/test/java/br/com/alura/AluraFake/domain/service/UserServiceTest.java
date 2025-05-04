package br.com.alura.AluraFake.domain.service;

import br.com.alura.AluraFake.domain.model.user.Role;
import br.com.alura.AluraFake.domain.model.user.User;
import br.com.alura.AluraFake.domain.repository.UserRepository;
import br.com.alura.AluraFake.dtos.request.NewUserDTO;
import br.com.alura.AluraFake.dtos.response.UserListItemDTO;
import br.com.alura.AluraFake.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private NewUserDTO validUserDTO;

    @BeforeEach
    void setUp() {
        validUserDTO = new NewUserDTO();
        validUserDTO.setName("João Silva");
        validUserDTO.setEmail("joao@email.com");
        validUserDTO.setPassword("123456");
        validUserDTO.setRole(Role.STUDENT);
    }

    @Test
    void saveUser__should_saveCorrectly_when_validData() {
        when(userRepository.existsByEmail(validUserDTO.getEmail())).thenReturn(false);

        userService.saveUser(validUserDTO);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser);
        assertEquals(validUserDTO.getName(), savedUser.getName());
        assertEquals(validUserDTO.getEmail(), savedUser.getEmail());
        assertEquals(validUserDTO.getRole(), savedUser.getRole());
    }

    @Test
    void saveUser__should_throwException_when_emailAlreadyExist() {
        when(userRepository.existsByEmail(validUserDTO.getEmail())).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class,
                () -> userService.saveUser(validUserDTO));

        assertEquals("This email is already registered", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void listAllUsers__shouldReturn_allUsers() {
        List<User> users = List.of(
                new User("João Silva", "joao@email.com", Role.STUDENT),
                new User("Maria Santos", "maria@email.com", Role.INSTRUCTOR)
        );
        when(userRepository.findAll()).thenReturn(users);

        List<UserListItemDTO> result = userService.listAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("João Silva", result.get(0).getName());
        assertEquals("Maria Santos", result.get(1).getName());
        verify(userRepository).findAll();
    }

    @Test
    void listAllUsers_shouldReturnEmptyList_withNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserListItemDTO> result = userService.listAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }
}