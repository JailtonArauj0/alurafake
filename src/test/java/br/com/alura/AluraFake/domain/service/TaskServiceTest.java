package br.com.alura.AluraFake.domain.service;

import br.com.alura.AluraFake.domain.model.course.Course;
import br.com.alura.AluraFake.domain.model.course.Status;
import br.com.alura.AluraFake.domain.model.task.Task;
import br.com.alura.AluraFake.domain.model.task.Type;
import br.com.alura.AluraFake.domain.repository.CourseRepository;
import br.com.alura.AluraFake.domain.repository.TaskRepository;
import br.com.alura.AluraFake.dtos.request.ChoiceDTO;
import br.com.alura.AluraFake.dtos.request.OpenTextDTO;
import br.com.alura.AluraFake.dtos.request.Option;
import br.com.alura.AluraFake.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private TaskService taskService;

    private Course course;

    @BeforeEach
    void setup() {
        course = new Course();
        course.setId(1L);
        course.setStatus(Status.BUILDING);
    }

    @Test
    void saveOpenText__should_save_successfully_when_valid_input() {
        OpenTextDTO dto = new OpenTextDTO(1L, "Valid statement", 1);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.findTaskByStatement(anyString())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> taskService.saveOpenText(dto));

        verify(taskRepository).save(ArgumentMatchers.any(Task.class));
    }

    @Test
    void saveOpenText__should_throw_exception_when_course_not_found() {
        OpenTextDTO dto = new OpenTextDTO(1L, "Valid statement", 1);
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(
                CustomException.class,
                () -> taskService.saveOpenText(dto)
        );

        assertEquals("Course not found", exception.getMessage());
    }

    @Test
    void saveSingleChoice__should_save_successfully_when_valid_input() {
        List<Option> options = List.of(
                new Option("Option 1", true),
                new Option("Option 2", false)
        );
        ChoiceDTO dto = new ChoiceDTO(1L, "Valid statement", 1, options);
        Task task = dto.toEntity(course, Type.SINGLE_CHOICE);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.findTaskByStatement(anyString())).thenReturn(Optional.empty());
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        assertDoesNotThrow(() -> taskService.saveSingleChoice(dto));

        verify(taskRepository, times(2)).save(any(Task.class));
    }

    @Test
    void saveSingleChoice__should_throw_exception_when_duplicate_options() {
        List<Option> options = List.of(
                new Option("Same option", true),
                new Option("Same option", false)
        );
        ChoiceDTO dto = new ChoiceDTO(1L, "Valid statement", 1, options);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> taskService.saveSingleChoice(dto)
        );

        assertEquals("Duplicate options are not allowed", exception.getMessage());
    }


    @Test
    void saveMultipleChoice__should_save_successfully_when_valid_input() {
        List<Option> options = List.of(
                new Option("Option 1", true),
                new Option("Option 2", true),
                new Option("Option 3", false)
        );
        ChoiceDTO dto = new ChoiceDTO(1L, "Valid statement", 1, options);
        Task task = dto.toEntity(course, Type.MULTIPLE_CHOICE);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.findTaskByStatement(anyString())).thenReturn(Optional.empty());
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        assertDoesNotThrow(() -> taskService.saveMultipleChoice(dto));

        verify(taskRepository, times(2)).save(any(Task.class));
    }

    @Test
    void saveMultipleChoice__should_throw_exception_when_invalid_correct_answers() {
        List<Option> options = List.of(
                new Option("Option 1", true),
                new Option("Option 2", false),
                new Option("Option 3", false)
        );
        ChoiceDTO dto = new ChoiceDTO(1L, "Valid statement", 1, options);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> taskService.saveMultipleChoice(dto)
        );

        assertEquals("At least two correct answers are required, and one incorrect answer is required", exception.getMessage());
    }

    @Test
    void task_order__should_throw_exception_when_not_continuous() {
        OpenTextDTO dto = new OpenTextDTO(1L, "Valid statement", 3);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.findAllByCourseIdOrderByTaskOrder(any(Course.class))).thenReturn(new ArrayList<>());

        CustomException exception = assertThrows(
                CustomException.class,
                () -> taskService.saveOpenText(dto)
        );

        assertEquals("Invalid order: sequence is not continuous", exception.getMessage());
    }

    @Test
    void validate_statement__should_throw_exception_when_duplicate() {
        OpenTextDTO dto = new OpenTextDTO(1L, "Duplicate statement", 1);
        when(taskRepository.findTaskByStatement("Duplicate statement"))
                .thenReturn(Optional.of(new Task()));

        CustomException exception = assertThrows(
                CustomException.class,
                () -> taskService.saveOpenText(dto)
        );

        assertEquals("Task with this statement already exists", exception.getMessage());
    }

    @Test
    void saveOpenText__should_throw_exception_when_options_empty() {
        List<Option> options = new ArrayList<>();
        ChoiceDTO dto = new ChoiceDTO(1L, "Valid statement", 1, options);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> taskService.saveSingleChoice(dto)
        );

        assertEquals("The number of options must be between 2 and 5", exception.getMessage());
    }

    @Test
    void saveOpenText__should_throw_exception_when_too_many_options() {
        List<Option> options = List.of(
                new Option("Option 1", true),
                new Option("Option 2", false),
                new Option("Option 3", false),
                new Option("Option 4", false),
                new Option("Option 5", false),
                new Option("Option 6", false)
        );
        ChoiceDTO dto = new ChoiceDTO(1L, "Valid statement", 1, options);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> taskService.saveSingleChoice(dto)
        );

        assertEquals("The number of options must be between 2 and 5", exception.getMessage());
    }

    @Test
    void saveMultipleChoice__should_throw_exception_when_less_than_three_options() {
        List<Option> options = List.of(
                new Option("Option 1", true),
                new Option("Option 2", false)
        );
        ChoiceDTO dto = new ChoiceDTO(1L, "Valid statement", 1, options);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> taskService.saveMultipleChoice(dto)
        );

        assertEquals("The number of options must be between 3 and 5", exception.getMessage());
    }

    @Test
    void saveMultipleChoice__should_throw_exception_when_too_many_options() {
        List<Option> options = List.of(
                new Option("Option 1", true),
                new Option("Option 2", true),
                new Option("Option 3", false),
                new Option("Option 4", false),
                new Option("Option 5", false),
                new Option("Option 6", false)
        );
        ChoiceDTO dto = new ChoiceDTO(1L, "Valid statement", 1, options);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> taskService.saveMultipleChoice(dto)
        );

        assertEquals("The number of options must be between 3 and 5", exception.getMessage());
    }

    @Test
    void saveMultipleChoice__should_throw_exception_when_all_options_correct() {
        List<Option> options = List.of(
                new Option("Option 1", true),
                new Option("Option 2", true),
                new Option("Option 3", true)
        );
        ChoiceDTO dto = new ChoiceDTO(1L, "Valid statement", 1, options);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> taskService.saveMultipleChoice(dto)
        );

        assertEquals("At least two correct answers are required, and one incorrect answer is required", exception.getMessage());
    }

    @Test
    void saveMultipleChoice__should_throw_exception_when_all_options_incorrect() {
        List<Option> options = List.of(
                new Option("Option 1", false),
                new Option("Option 2", false),
                new Option("Option 3", false)
        );
        ChoiceDTO dto = new ChoiceDTO(1L, "Valid statement", 1, options);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> taskService.saveMultipleChoice(dto)
        );

        assertEquals("At least two correct answers are required, and one incorrect answer is required", exception.getMessage());
    }

    @Test
    void saveSingleChoice__should_throw_exception_when_no_correct_option() {
        List<Option> options = List.of(
                new Option("Option 1", false),
                new Option("Option 2", false)
        );
        ChoiceDTO dto = new ChoiceDTO(1L, "Valid statement", 1, options);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> taskService.saveSingleChoice(dto)
        );

        assertEquals("Must have exactly 1 correct answer", exception.getMessage());
    }

    @Test
    void saveSingleChoice__should_throw_exception_when_multiple_correct_options() {
        List<Option> options = List.of(
                new Option("Option 1", true),
                new Option("Option 2", true)
        );
        ChoiceDTO dto = new ChoiceDTO(1L, "Valid statement", 1, options);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> taskService.saveSingleChoice(dto)
        );

        assertEquals("Must have exactly 1 correct answer", exception.getMessage());
    }

    @Test
    void save_task__should_throw_exception_when_course_not_building() {
        course.setStatus(Status.PUBLISHED);
        OpenTextDTO dto = new OpenTextDTO(1L, "Valid statement", 1);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CustomException exception = assertThrows(
                CustomException.class,
                () -> taskService.saveOpenText(dto)
        );

        assertEquals("Course is not in BUILDING status", exception.getMessage());
    }

    @Test
    void save_choice__should_throw_exception_when_option_equals_statement() {
        List<Option> options = List.of(
                new Option("Valid statement", true),
                new Option("Option 2", false)
        );
        ChoiceDTO dto = new ChoiceDTO(1L, "Valid statement", 1, options);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> taskService.saveSingleChoice(dto)
        );

        assertEquals("The option cannot be the same as the statement", exception.getMessage());
    }

    @Test
    void saveMultipleChoice__should_throw_exception_when_option_equals_statement() {
        List<Option> options = List.of(
                new Option("Question statement", true),
                new Option("Option 2", true),
                new Option("Option 3", false)
        );
        ChoiceDTO dto = new ChoiceDTO(1L, "Question statement", 1, options);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> taskService.saveMultipleChoice(dto)
        );

        assertEquals("The option cannot be the same as the statement", exception.getMessage());
    }
}