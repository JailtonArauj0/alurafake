package br.com.alura.AluraFake.controllers;

import br.com.alura.AluraFake.config.SecurityConfig;
import br.com.alura.AluraFake.config.SecurityFilter;
import br.com.alura.AluraFake.config.TokenService;
import br.com.alura.AluraFake.domain.model.course.Course;
import br.com.alura.AluraFake.domain.model.user.User;
import br.com.alura.AluraFake.domain.service.CourseService;
import br.com.alura.AluraFake.domain.service.UserService;
import br.com.alura.AluraFake.dtos.request.NewCourseDTO;
import br.com.alura.AluraFake.dtos.response.CourseListItemDTO;
import br.com.alura.AluraFake.exception.CustomException;
import br.com.alura.AluraFake.exception.EntityNotFoundException;
import br.com.alura.AluraFake.exception.exceptionHandler.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
@Import({
        SecurityConfig.class,
        ValidationAutoConfiguration.class,
        GlobalExceptionHandler.class,
})
@AutoConfigureMockMvc(addFilters = false)
class CourseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

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

    @MockBean
    private User instructor;

    @BeforeEach
    void setUp() {
        when(instructor.isInstructor()).thenReturn(true);
    }

    @Test
    @WithMockUser(roles = {"INSTRUCTOR"})
    void createCourse__should_return_201_when_valid_request() throws Exception {
        NewCourseDTO dto = new NewCourseDTO();
        dto.setTitle("Java Spring Boot");
        dto.setDescription("Curso completo de Spring Boot");
        dto.setEmailInstructor("instructor@email.com");

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(courseService).createCourse(any(NewCourseDTO.class), anyString());
    }

    @Test
    void createCourse__should_return_400_when_title_is_blank() throws Exception {
        NewCourseDTO dto = new NewCourseDTO();
        dto.setTitle("");
        dto.setDescription("Curso completo de Spring Boot");
        dto.setEmailInstructor("instructor@email.com");

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].name").value("title"))
                .andExpect(jsonPath("$.fields[0].message").isNotEmpty());

        verify(courseService, never()).createCourse(any(), anyString());
    }

    @Test
    void createCourse__should_return_400_when_description_is_too_short() throws Exception {
        NewCourseDTO dto = new NewCourseDTO();
        dto.setTitle("Java Spring Boot");
        dto.setDescription("ABC");
        dto.setEmailInstructor("instructor@email.com");

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].name").value("description"))
                .andExpect(jsonPath("$.fields[0].message").isNotEmpty());

        verify(courseService, never()).createCourse(any(), anyString());
    }

    @Test
    void createCourse__should_return_400_when_title_is_null() throws Exception {
        NewCourseDTO dto = new NewCourseDTO();
        dto.setTitle(null);
        dto.setDescription("Curso completo de Spring Boot");
        dto.setEmailInstructor("instructor@email.com");

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].name").value("title"))
                .andExpect(jsonPath("$.fields[0].message").isNotEmpty());

        verify(courseService, never()).createCourse(any(), anyString());
    }

    @Test
    void createCourse__should_return_400_when_description_is_null() throws Exception {
        NewCourseDTO dto = new NewCourseDTO();
        dto.setTitle("Java Spring Boot");
        dto.setDescription(null);
        dto.setEmailInstructor("instructor@email.com");

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].name").value("description"))
                .andExpect(jsonPath("$.fields[0].message").isNotEmpty());

        verify(courseService, never()).createCourse(any(), anyString());
    }

    @Test
    void listAllCourses__should_return_empty_list_when_no_courses() throws Exception {
        when(courseService.listAllCourses()).thenReturn(List.of());

        mockMvc.perform(get("/course/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void listAllCourses__should_return_courses_when_exists() throws Exception {
        when(instructor.isInstructor()).thenReturn(true);
        Course course1 = new Course("Java", "Curso de Java", instructor);
        Course course2 = new Course("Spring", "Curso de Spring", instructor);

        List<CourseListItemDTO> courses = List.of(
                new CourseListItemDTO(course1),
                new CourseListItemDTO(course2)
        );

        when(courseService.listAllCourses()).thenReturn(courses);

        mockMvc.perform(get("/course/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Java"))
                .andExpect(jsonPath("$[0].description").value("Curso de Java"))
                .andExpect(jsonPath("$[1].title").value("Spring"))
                .andExpect(jsonPath("$[1].description").value("Curso de Spring"));
    }

    @Test
    void listAllCourses__should_return_all_fields() throws Exception {
        when(instructor.isInstructor()).thenReturn(true);
        Course course = new Course("Java", "Curso de Java", instructor);
        course.setId(1L);
        CourseListItemDTO courseDTO = new CourseListItemDTO(course);

        when(courseService.listAllCourses()).thenReturn(List.of(courseDTO));

        mockMvc.perform(get("/course/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].description").exists())
                .andExpect(jsonPath("$[0].status").exists())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = {"INSTRUCTOR"})
    void publishCourse__should_return_200_when_course_exists() throws Exception {
        Long courseId = 1L;

        mockMvc.perform(post("/course/{id}/publish", courseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(courseService).publishCourse(courseId);
    }

    @Test
    @WithMockUser(roles = {"INSTRUCTOR"})
    void publishCourse__should_return_404_when_course_not_found() throws Exception {
        Long courseId = 999L;

        doThrow(new EntityNotFoundException("Course not found."))
                .when(courseService).publishCourse(courseId);

        mockMvc.perform(post("/course/{id}/publish", courseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Course not found."));
    }

    @Test
    @WithMockUser(roles = {"INSTRUCTOR"})
    void publishCourse__should_return_400_when_course_already_published() throws Exception {
        Long courseId = 1L;

        doThrow(new CustomException("Course already published."))
                .when(courseService).publishCourse(courseId);

        mockMvc.perform(post("/course/{id}/publish", courseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Course already published."));
    }
}