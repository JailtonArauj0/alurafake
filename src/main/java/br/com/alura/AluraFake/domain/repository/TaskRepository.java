package br.com.alura.AluraFake.domain.repository;

import br.com.alura.AluraFake.domain.model.course.Course;
import br.com.alura.AluraFake.domain.model.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findTaskByStatement(String statement);

    List<Task> findAllByCourseIdOrderByTaskOrder(Course courseId);
}
