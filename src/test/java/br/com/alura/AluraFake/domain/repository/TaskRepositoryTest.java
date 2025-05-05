package br.com.alura.AluraFake.domain.repository;

import br.com.alura.AluraFake.domain.model.course.Course;
import br.com.alura.AluraFake.domain.model.task.Task;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {
    @Mock
    private TaskRepository taskRepository;

    @Test
    void findTaskByStatement__should_return_task_when_statement_exists() {
        String statement = "Complete the exercise";
        Task expectedTask = new Task();
        expectedTask.setStatement(statement);

        when(taskRepository.findTaskByStatement(statement))
                .thenReturn(Optional.of(expectedTask));

        Optional<Task> result = taskRepository.findTaskByStatement(statement);

        assertTrue(result.isPresent());
        assertEquals(statement, result.get().getStatement());
        verify(taskRepository, times(1)).findTaskByStatement(statement);
    }

    @Test
    void findTaskByStatement__should_return_empty_optional_when_statement_not_found() {
        String statement = "Non-existent statement";

        when(taskRepository.findTaskByStatement(statement))
                .thenReturn(Optional.empty());

        Optional<Task> result = taskRepository.findTaskByStatement(statement);

        assertFalse(result.isPresent());
        verify(taskRepository, times(1)).findTaskByStatement(statement);
    }

    @Test
    void findAllByCourseIdOrderByTaskOrder__should_return_ordered_tasks_when_course_has_tasks() {
        Course course = new Course();
        course.setId(1L);

        Task task1 = new Task();
        task1.setTaskOrder(1);
        Task task2 = new Task();
        task2.setTaskOrder(2);

        List<Task> expectedTasks = Arrays.asList(task1, task2);

        when(taskRepository.findAllByCourseIdOrderByTaskOrder(course))
                .thenReturn(expectedTasks);

        List<Task> result = taskRepository.findAllByCourseIdOrderByTaskOrder(course);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getTaskOrder());
        assertEquals(2, result.get(1).getTaskOrder());
        verify(taskRepository, times(1)).findAllByCourseIdOrderByTaskOrder(course);
    }

    @Test
    void findAllByCourseIdOrderByTaskOrder__should_return_empty_list_when_course_has_no_tasks() {
        Course course = new Course();
        course.setId(1L);

        when(taskRepository.findAllByCourseIdOrderByTaskOrder(course))
                .thenReturn(Collections.emptyList());

        List<Task> result = taskRepository.findAllByCourseIdOrderByTaskOrder(course);

        assertTrue(result.isEmpty());
        verify(taskRepository, times(1)).findAllByCourseIdOrderByTaskOrder(course);
    }
}