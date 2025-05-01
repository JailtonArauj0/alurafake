package br.com.alura.AluraFake.services;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.dtos.request.OpenTextDTO;
import br.com.alura.AluraFake.exception.CustomException;
import br.com.alura.AluraFake.repositories.CourseRepository;
import br.com.alura.AluraFake.repositories.TaskRepository;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.Type;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;

    public TaskService(TaskRepository taskRepository, CourseRepository courseRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveOpenText(OpenTextDTO openTextDTO) {
        taskRepository.findTaskByStatement(openTextDTO.getStatement())
                .ifPresent(task -> {
                    throw new CustomException("Task with this statement already exists");
                });

        courseRepository.findById(openTextDTO.getCourseId())
                .ifPresentOrElse(course -> {
                    if(!course.getStatus().name().equals("BUILDING")) {
                        throw new CustomException("Course is not in BUILDING status");
                    }
                    validateOrder(course, openTextDTO.getOrder());
                    taskRepository.save(openTextDTO.toEntity(course, Type.OPEN_TEXT));
                },
                    () -> {
                        throw new CustomException("Course not found");
                    }
                );
    }

    private void validateOrder(Course course, Integer order) {
        List<Task> existingTasks = taskRepository.findAllByCourseIdOrderByTaskOrder(course);
        if (order > existingTasks.size() + 1) {
            throw new CustomException("Invalid order: sequence is not continuous");
        }

        for (Task task : existingTasks) {
            if (task.getTaskOrder() >= order) {
                task.setTaskOrder(task.getTaskOrder() + 1);
            }
        }

        taskRepository.saveAll(existingTasks);
    }
}
