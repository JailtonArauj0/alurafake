package br.com.alura.AluraFake.domain.service;

import br.com.alura.AluraFake.domain.model.course.Course;
import br.com.alura.AluraFake.domain.model.course.Status;
import br.com.alura.AluraFake.domain.model.task.Task;
import br.com.alura.AluraFake.domain.model.task.Type;
import br.com.alura.AluraFake.domain.model.user.Role;
import br.com.alura.AluraFake.domain.model.user.User;
import br.com.alura.AluraFake.domain.repository.CourseRepository;
import br.com.alura.AluraFake.domain.repository.TaskRepository;
import br.com.alura.AluraFake.domain.repository.UserRepository;
import br.com.alura.AluraFake.dtos.request.NewCourseDTO;
import br.com.alura.AluraFake.dtos.response.CourseListItemDTO;
import br.com.alura.AluraFake.exception.CustomException;
import br.com.alura.AluraFake.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseService courseService;

    private User instructor;
    private Course course;

    @BeforeEach
    void setUp() {
        instructor = new User("John Doe", "john@test.com", Role.INSTRUCTOR, "password");
        course = new Course("Java", "Curso de Java", instructor);
    }

    @Test
    void listAllCourses_should_return_courses_when_exists() {
        when(courseRepository.findAll()).thenReturn(List.of(course));

        List<CourseListItemDTO> result = courseService.listAllCourses();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getTitle());
    }

    @Test
    void publishCourse_should_publish_when_valid() {
        course.setId(1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        List<Task> tasks = List.of(
                new Task(course, "Task 1", 1, Type.OPEN_TEXT),
                new Task(course, "Task 2", 2, Type.SINGLE_CHOICE),
                new Task(course, "Task 3", 3, Type.MULTIPLE_CHOICE)
        );

        when(taskRepository.findAllByCourseIdOrderByTaskOrder(course)).thenReturn(tasks);

        courseService.publishCourse(1L);

        assertEquals(Status.PUBLISHED, course.getStatus());
        verify(courseRepository).save(course);
    }

    @Test
    void publishCourse_should_throw_when_course_not_found() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                courseService.publishCourse(999L)
        );
    }

    @Test
    void publishCourse_should_throw_when_already_published() {
        course.setId(1L);
        course.setStatus(Status.PUBLISHED);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThrows(CustomException.class, () ->
                courseService.publishCourse(1L)
        );
    }

    @Test
    void createCourse_should_create_when_instructor_valid() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(instructor));

        NewCourseDTO dto = new NewCourseDTO("Java", "Curso de Java", "student@test.com");

        courseService.createCourse(dto, "john@test.com");

        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createCourse_should_throw_when_user_not_instructor() {
        User student = new User("Student", "student@test.com", Role.STUDENT, "password");
        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(student));

        NewCourseDTO dto = new NewCourseDTO("Java", "Curso de Java", "student@test.com");

        assertThrows(CustomException.class, () ->
                courseService.createCourse(dto, "student@test.com")
        );
    }

    @Test
    void publishCourse_should_throw_when_tasks_empty() {
        course.setId(1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.findAllByCourseIdOrderByTaskOrder(course)).thenReturn(List.of());

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.publishCourse(1L)
        );

        assertEquals("Course doesn't have any task.", exception.getMessage());
    }

    @Test
    void publishCourse_should_throw_when_tasks_wrong_order() {
        course.setId(1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        List<Task> tasksWrongOrder = List.of(
                new Task(course, "Task 1", 2, Type.OPEN_TEXT),
                new Task(course, "Task 2", 1, Type.SINGLE_CHOICE),
                new Task(course, "Task 3", 3, Type.MULTIPLE_CHOICE)
        );

        when(taskRepository.findAllByCourseIdOrderByTaskOrder(course)).thenReturn(tasksWrongOrder);

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.publishCourse(1L)
        );

        assertEquals("Tasks are not in the correct order.", exception.getMessage());
    }

    @Test
    void publishCourse_should_throw_when_missing_task_type() {
        course.setId(1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        List<Task> tasksIncomplete = List.of(
                new Task(course, "Task 1", 1, Type.OPEN_TEXT),
                new Task(course, "Task 2", 2, Type.SINGLE_CHOICE)
        );

        when(taskRepository.findAllByCourseIdOrderByTaskOrder(course)).thenReturn(tasksIncomplete);

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.publishCourse(1L)
        );

        assertEquals("Course must have at least one task of each type.", exception.getMessage());
    }

    @Test
    void createCourse_should_throw_when_instructor_not_found() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        NewCourseDTO dto = new NewCourseDTO("Java", "Curso de Java", "unknown@test.com");

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.createCourse(dto, "unknown@test.com")
        );

        assertEquals("The user is not an instructor.", exception.getMessage());
    }
}