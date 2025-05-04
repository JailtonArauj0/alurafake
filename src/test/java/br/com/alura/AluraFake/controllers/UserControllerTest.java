package br.com.alura.AluraFake.controllers;

import br.com.alura.AluraFake.config.SecurityConfig;
import br.com.alura.AluraFake.config.SecurityFilter;
import br.com.alura.AluraFake.config.TokenService;
import br.com.alura.AluraFake.domain.model.user.Role;
import br.com.alura.AluraFake.domain.model.user.User;
import br.com.alura.AluraFake.domain.repository.UserRepository;
import br.com.alura.AluraFake.domain.service.UserService;
import br.com.alura.AluraFake.dtos.request.LoginRequestDTO;
import br.com.alura.AluraFake.dtos.request.NewUserDTO;
import br.com.alura.AluraFake.dtos.response.UserListItemDTO;
import br.com.alura.AluraFake.exception.exceptionHandler.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({
        SecurityConfig.class,
        ValidationAutoConfiguration.class,
        GlobalExceptionHandler.class,
        WebMvcAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private SecurityFilter securityFilter;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void newUser__should_return_bad_request_when_email_is_blank() throws Exception {
        NewUserDTO newUserDTO = new NewUserDTO();
        newUserDTO.setEmail("");
        newUserDTO.setName("Caio Bugorin");
        newUserDTO.setRole(Role.STUDENT);
        newUserDTO.setPassword("123456");

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andDo(print()) // para debug
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("One or more fields are invalid, please fill them correctly!"))
                .andExpect(jsonPath("$.fields[?(@.name == 'email')].message")
                        .value("Email cannot be blank"));
    }

    @Test
    void newUser__should_return_bad_request_when_email_is_invalid() throws Exception {
        NewUserDTO newUserDTO = new NewUserDTO();
        newUserDTO.setEmail("caio");
        newUserDTO.setName("Caio Bugorin");
        newUserDTO.setRole(Role.STUDENT);
        newUserDTO.setPassword("123456");

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].name").value("email"))
                .andExpect(jsonPath("$.fields[0].message").isNotEmpty());
    }

    @Test
    void newUser__should_return_created_when_user_request_is_valid() throws Exception {
        NewUserDTO newUserDTO = new NewUserDTO();
        newUserDTO.setEmail("caio.bugorin@alura.com.br");
        newUserDTO.setName("Caio Bugorin");
        newUserDTO.setPassword("123456");
        newUserDTO.setRole(Role.STUDENT);

        when(userRepository.existsByEmail(newUserDTO.getEmail())).thenReturn(false);

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void listAllUsers__should_list_all_users() throws Exception {
        UserListItemDTO user1 = new UserListItemDTO(new User("User 1", "user1@test.com", Role.STUDENT));
        UserListItemDTO user2 = new UserListItemDTO(new User("User 2", "user2@test.com", Role.STUDENT));
        when(userService.listAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/user/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[1].name").value("User 2"));
    }

    @Test
    void login__should_return_token_on_valid_credentials() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("user@test.com", "123456");

        User fakeUser = new User("User Test", "user@test.com", Role.STUDENT);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(fakeUser, null, List.of());
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authToken);
        when(tokenService.generateToken(any(User.class)))
                .thenReturn("fake-jwt-token");

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }
}