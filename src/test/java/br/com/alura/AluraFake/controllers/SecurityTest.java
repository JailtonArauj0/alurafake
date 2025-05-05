package br.com.alura.AluraFake.controllers;

import br.com.alura.AluraFake.domain.service.CourseService;
import br.com.alura.AluraFake.domain.service.TaskService;
import br.com.alura.AluraFake.dtos.request.ChoiceDTO;
import br.com.alura.AluraFake.dtos.request.OpenTextDTO;
import br.com.alura.AluraFake.dtos.request.Option;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void newOpenTextExercise_should_return_403_when_not_instructor() throws Exception {
        OpenTextDTO request = new OpenTextDTO(
                1L,
                "Descreva o funcionamento do Spring Boot",
                1
        );

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void newSingleChoice_should_return_403_when_not_instructor() throws Exception {
        List<Option> options = List.of(
                new Option("Opção 1", true),
                new Option("Opção 2", false)
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
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void newMultipleChoice_should_return_403_when_not_instructor() throws Exception {
        List<Option> options = List.of(
                new Option("Esta é a primeira opção válida", Boolean.TRUE),
                new Option("Esta é a segunda opção válida", Boolean.TRUE)
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
                .andExpect(status().isForbidden());

        verify(taskService, never()).saveMultipleChoice(any());
    }

    @Test
    @WithMockUser(roles = {"STUDENT"})
    void publishCourse__should_return_403_when_user_is_not_instructor() throws Exception {
        Long courseId = 1L;

        mockMvc.perform(post("/course/{id}/publish", courseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(courseService, never()).publishCourse(any());
    }

    @Test
    void publishCourse__should_return_403_when_user_is_not_authenticated() throws Exception {
        Long courseId = 1L;

        mockMvc.perform(post("/course/{id}/publish", courseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(courseService, never()).publishCourse(any());
    }
}