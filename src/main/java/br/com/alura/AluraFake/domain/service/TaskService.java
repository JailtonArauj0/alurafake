package br.com.alura.AluraFake.domain.service;

import br.com.alura.AluraFake.domain.model.course.Course;
import br.com.alura.AluraFake.dtos.request.ChoiceDTO;
import br.com.alura.AluraFake.dtos.request.OpenTextDTO;
import br.com.alura.AluraFake.dtos.request.Option;
import br.com.alura.AluraFake.exception.CustomException;
import br.com.alura.AluraFake.domain.repository.ChoiceRepository;
import br.com.alura.AluraFake.domain.repository.CourseRepository;
import br.com.alura.AluraFake.domain.repository.TaskRepository;
import br.com.alura.AluraFake.domain.model.task.Task;
import br.com.alura.AluraFake.domain.model.task.Type;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    // MANTIVE AS VALIDAÇÕES AQUI, DEVIDO A LÓGICA SER ESPECÍFICA DESTE SERVICE
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

    private void validadeOptionsSize(List<Option> options, Type type) {
        if (type == Type.SINGLE_CHOICE) {
            if (options.size() < 2 || options.size() > 5) {
                throw new CustomException("The number of options must be between 2 and 5");
            }

        } else {
            if (options.size() < 3 || options.size() > 5) {
                throw new CustomException("The number of options must be between 3 and 5");
            }
        }
    }

    private void validateUniqueOptions(List<Option> options) {
        Set<String> uniqueOptions = options.stream()
                .map(Option::getOption)
                .collect(Collectors.toSet());
        if (uniqueOptions.size() != options.size()) {
            throw new CustomException("Duplicate options are not allowed");
        }
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
        validateStatement(choiceDTO.getStatement());
        validadeOptionsSize(options, Type.SINGLE_CHOICE);
        validateUniqueOptions(options);

        for (int i = 0; i < options.size(); i++) {
            if (options.stream().filter(Option::isCorrect).count() != 1) {
                throw new CustomException("Must have exactly 1 correct answer");
            }
            if (options.stream().anyMatch(option -> option.getOption().equals(choiceDTO.getStatement()))) {
                throw new CustomException("The option cannot be the same as the statement");
            }
        }

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
        validateStatement(choiceDTO.getStatement());
        validadeOptionsSize(options, Type.MULTIPLE_CHOICE);
        validateUniqueOptions(options);

        for (int i = 0; i < options.size(); i++) {
            if (options.stream().filter(Option::isCorrect).count() < 2 ||
                    options.stream().noneMatch(option -> option.isCorrect().equals(Boolean.FALSE))) {
                throw new CustomException("At least two correct answers are required, and one incorrect answer is required");
            }
            if (options.stream().anyMatch(option -> option.getOption().equals(choiceDTO.getStatement()))) {
                throw new CustomException("The option cannot be the same as the statement");
            }
        }

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
