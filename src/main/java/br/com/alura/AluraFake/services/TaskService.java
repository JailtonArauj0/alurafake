package br.com.alura.AluraFake.services;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.dtos.request.ChoiceDTO;
import br.com.alura.AluraFake.dtos.request.OpenTextDTO;
import br.com.alura.AluraFake.dtos.request.Option;
import br.com.alura.AluraFake.exception.CustomException;
import br.com.alura.AluraFake.repositories.ChoiceRepository;
import br.com.alura.AluraFake.repositories.CourseRepository;
import br.com.alura.AluraFake.repositories.TaskRepository;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.Type;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;
    private final ChoiceRepository choiceRepository;

    public TaskService(TaskRepository taskRepository, CourseRepository courseRepository, ChoiceRepository choiceRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
        this.choiceRepository = choiceRepository;
    }

    private void validateAndSaveOrder(Course course, Integer order) {
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

    private void validateStatement(String statement) {
        taskRepository.findTaskByStatement(statement)
                .ifPresent(task -> {
                    throw new CustomException("Task with this statement already exists");
                });
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveOpenText(OpenTextDTO openTextDTO) {
        validateStatement(openTextDTO.getStatement());

        courseRepository.findById(openTextDTO.getCourseId())
                .ifPresentOrElse(course -> {
                            if (!course.getStatus().name().equals("BUILDING")) {
                                throw new CustomException("Course is not in BUILDING status");
                            }
                            validateAndSaveOrder(course, openTextDTO.getOrder());
                            taskRepository.save(openTextDTO.toEntity(course, Type.OPEN_TEXT));
                        },
                        () -> {
                            throw new CustomException("Course not found");
                        }
                );
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveSingleChoice(@Valid ChoiceDTO choiceDTO) {
        List<Option> options = choiceDTO.getOptions();

        if( options.size() < 2 || options.size() > 5) {
            throw new CustomException("The number of options must be between 2 and 5");
        }

        for (int i = 0; i < options.size(); i++) {
            String actualOption = options.get(i).getOption();

            if (options.stream().filter(option -> option.getOption().equals(actualOption)).count() > 1) {
                throw new CustomException("Duplicate options are not allowed");
            }
            if (options.stream().filter(option -> option.isCorrect().equals(Boolean.TRUE)).count() > 1) {
                throw new CustomException("Duplicate correct answers are not allowed");
            }
            if (options.stream().anyMatch(option -> option.getOption().equals(choiceDTO.getStatement()))) {
                throw new CustomException("The option cannot be the same as the statement");
            }
        }

        validateStatement(choiceDTO.getStatement());

        courseRepository.findById(choiceDTO.getCourseId())
                .ifPresentOrElse(course -> {
                    if(!course.getStatus().name().equals("BUILDING")) {
                        throw new CustomException("Course is not in BUILDING status");
                    }
                    validateAndSaveOrder(course, choiceDTO.getOrder());
                    Task savedTask = taskRepository.save(choiceDTO.toEntity(course, Type.SINGLE_CHOICE));

                    var choices = options.stream().map(option -> option.toEntity(savedTask)).toList();
                    choiceRepository.saveAll(choices);
                },
                    () -> {
                        throw new CustomException("Course not found");
                    }
                );
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveMultipleChoice(@Valid ChoiceDTO choiceDTO) {
        List<Option> options = choiceDTO.getOptions();

        if( options.size() < 3 || options.size() > 5) {
            throw new CustomException("The number of options must be between 3 and 5");
        }

        for (int i = 0; i < options.size(); i++) {
            String actualOption = options.get(i).getOption();

            if (options.stream().filter(option -> option.getOption().equals(actualOption)).count() > 1) {
                throw new CustomException("Duplicate options are not allowed");
            }
            if (options.stream().filter(option -> option.isCorrect().equals(Boolean.TRUE)).count() < 2 ||
                    options.stream().noneMatch(option -> option.isCorrect().equals(Boolean.FALSE))) {
                throw new CustomException("At least two correct answers are required, and one incorrect answer is required");
            }
            if (options.stream().anyMatch(option -> option.getOption().equals(choiceDTO.getStatement()))) {
                throw new CustomException("The option cannot be the same as the statement");
            }
        }

        validateStatement(choiceDTO.getStatement());

        courseRepository.findById(choiceDTO.getCourseId())
                .ifPresentOrElse(course -> {
                    if(!course.getStatus().name().equals("BUILDING")) {
                        throw new CustomException("Course is not in BUILDING status");
                    }
                    validateAndSaveOrder(course, choiceDTO.getOrder());
                    Task savedTask = taskRepository.save(choiceDTO.toEntity(course, Type.MULTIPLE_CHOICE));

                    var choices = options.stream().map(option -> option.toEntity(savedTask)).toList();
                    choiceRepository.saveAll(choices);
                },
                    () -> {
                        throw new CustomException("Course not found");
                    }
                );
    }
}
