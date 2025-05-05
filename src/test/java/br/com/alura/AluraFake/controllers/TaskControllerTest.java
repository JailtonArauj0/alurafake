package br.com.alura.AluraFake.controllers;

import br.com.alura.AluraFake.config.SecurityConfig;
import br.com.alura.AluraFake.config.SecurityFilter;
import br.com.alura.AluraFake.config.TokenService;
import br.com.alura.AluraFake.domain.service.TaskService;
import br.com.alura.AluraFake.domain.service.UserService;
import br.com.alura.AluraFake.dtos.request.ChoiceDTO;
import br.com.alura.AluraFake.dtos.request.OpenTextDTO;
import br.com.alura.AluraFake.dtos.request.Option;
import br.com.alura.AluraFake.exception.exceptionHandler.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import({
        SecurityConfig.class,
        ValidationAutoConfiguration.class,
        GlobalExceptionHandler.class,
})
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private SecurityFilter securityFilter;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void newOpenTextExercise__should_return_201_when_valid_input() throws Exception {
        OpenTextDTO request = new OpenTextDTO(
                1L,
                "Descreva o funcionamento do Spring Boot",
                1
        );

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(taskService).saveOpenText(any(OpenTextDTO.class));
    }

    @Test
    void newOpenTextExercise__should_return_400_when_statement_is_too_short() throws Exception {
        // Arrange
        OpenTextDTO request = new OpenTextDTO(
                1L,
                "ABC",  // Menos de 4 caracteres
                1
        );

        // Act & Assert
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).saveOpenText(any());
    }

    @Test
    void newOpenTextExercise__should_return_400_when_order_is_zero() throws Exception {
        // Arrange
        OpenTextDTO request = new OpenTextDTO(
                1L,
                "Descreva o funcionamento do Spring Boot",
                0  // Ordem inválida
        );

        // Act & Assert
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).saveOpenText(any());
    }

    @Test
    void newOpenTextExercise__should_return_400_when_course_id_is_null() throws Exception {
        // Arrange
        OpenTextDTO request = new OpenTextDTO(
                null,  // CourseId nulo
                "Descreva o funcionamento do Spring Boot",
                1
        );

        // Act & Assert
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).saveOpenText(any());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void newSingleChoice__should_return_201_when_valid_input() throws Exception {
        List<Option> options = List.of(
                new Option("Esta é a primeira opção", Boolean.TRUE),
                new Option("Esta é a segunda opção", Boolean.FALSE),
                new Option("Esta é a terceira opção", Boolean.FALSE)
        );

        ChoiceDTO request = new ChoiceDTO(
                1L,
                "Qual é a principal característica do Spring Boot?",
                1,
                options
        );

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(taskService).saveSingleChoice(any(ChoiceDTO.class));
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void newSingleChoice__should_return_400_when_statement_is_too_short() throws Exception {
        List<Option> options = List.of(
                new Option("Opção 1", true),
                new Option("Opção 2", false)
        );

        ChoiceDTO request = new ChoiceDTO(
                1L,
                "ABC",
                1,
                options
        );

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).saveSingleChoice(any());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void newSingleChoice__should_return_400_when_options_less_than_two() throws Exception {
        List<Option> options = List.of(
                new Option("Opção 1", true)
        );

        ChoiceDTO request = new ChoiceDTO(
                1L,
                "Qual é a principal característica do Spring Boot?",
                1,
                options
        );

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).saveSingleChoice(any());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void newSingleChoice__should_return_400_when_more_than_five_options() throws Exception {
        List<Option> options = List.of(
                new Option("Opção 1", true),
                new Option("Opção 2", false),
                new Option("Opção 3", false),
                new Option("Opção 4", false),
                new Option("Opção 5", false),
                new Option("Opção 6", false)
        );

        ChoiceDTO request = new ChoiceDTO(
                1L,
                "Qual é a principal característica do Spring Boot?",
                1,
                options
        );

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).saveSingleChoice(any());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void newMultipleChoice__should_return_201_when_valid_input() throws Exception {
        List<Option> options = List.of(
                new Option("Esta é a primeira opção válida", Boolean.TRUE),
                new Option("Esta é a segunda opção válida", Boolean.TRUE),
                new Option("Esta é a terceira opção válida", Boolean.FALSE)
        );

        ChoiceDTO request = new ChoiceDTO(
                1L,
                "Quais são as características do Spring Boot?",
                1,
                options
        );

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(taskService).saveMultipleChoice(any(ChoiceDTO.class));
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void newMultipleChoice__should_return_400_when_statement_is_too_short() throws Exception {
        List<Option> options = List.of(
                new Option("Esta é a primeira opção válida", Boolean.TRUE),
                new Option("Esta é a segunda opção válida", Boolean.TRUE)
        );

        ChoiceDTO request = new ChoiceDTO(
                1L,
                "ABC",
                1,
                options
        );

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).saveMultipleChoice(any());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void newMultipleChoice__should_return_400_when_options_less_than_two() throws Exception {
        List<Option> options = List.of(
                new Option("Esta é a primeira opção válida", Boolean.TRUE)
        );

        ChoiceDTO request = new ChoiceDTO(
                1L,
                "Quais são as características do Spring Boot?",
                1,
                options
        );

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).saveMultipleChoice(any());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void newMultipleChoice__should_return_400_when_more_than_five_options() throws Exception {
        List<Option> options = List.of(
                new Option("Esta é a primeira opção válida", Boolean.TRUE),
                new Option("Esta é a segunda opção válida", Boolean.TRUE),
                new Option("Esta é a terceira opção válida", Boolean.FALSE),
                new Option("Esta é a quarta opção válida", Boolean.FALSE),
                new Option("Esta é a quinta opção válida", Boolean.FALSE),
                new Option("Esta é a sexta opção válida", Boolean.FALSE)
        );

        ChoiceDTO request = new ChoiceDTO(
                1L,
                "Quais são as características do Spring Boot?",
                1,
                options
        );

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).saveMultipleChoice(any());
    }
}