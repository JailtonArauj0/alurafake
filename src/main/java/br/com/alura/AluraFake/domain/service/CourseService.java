package br.com.alura.AluraFake.domain.service;

import br.com.alura.AluraFake.domain.model.course.Course;
import br.com.alura.AluraFake.domain.model.course.Status;
import br.com.alura.AluraFake.domain.model.task.Task;
import br.com.alura.AluraFake.domain.model.user.User;
import br.com.alura.AluraFake.domain.repository.CourseRepository;
import br.com.alura.AluraFake.domain.repository.TaskRepository;
import br.com.alura.AluraFake.domain.repository.UserRepository;
import br.com.alura.AluraFake.dtos.request.NewCourseDTO;
import br.com.alura.AluraFake.dtos.response.CourseListItemDTO;
import br.com.alura.AluraFake.exception.CustomException;
import br.com.alura.AluraFake.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<CourseListItemDTO> listAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseListItemDTO::new)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public void publishCourse(Long id) {
        Optional<Course> hasCourse = courseRepository.findById(id);

        hasCourse.ifPresentOrElse(course -> {
                if (course.getStatus().name().equals(Status.PUBLISHED.name())) {
                    throw new CustomException("Course already published.");
                }

                List<Task> tasks = taskRepository.findAllByCourseIdOrderByTaskOrder(course);
                validateOrderAndComposition(tasks);

                course.setPublishedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                course.setStatus(Status.PUBLISHED);
                courseRepository.save(course);
            },
            () -> {
                throw new EntityNotFoundException("Course not found.");
            }
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void createCourse(NewCourseDTO newCourse, String loggedUser) {
        Optional<User> possibleAuthor = userRepository
                .findByEmail(loggedUser)
                .filter(User::isInstructor);

        if(possibleAuthor.isEmpty()) {
            throw new CustomException("The user is not an instructor.");
        }

        Course course = newCourse.toEntity(possibleAuthor.get());
        courseRepository.save(course);
    }

    private void validateOrderAndComposition(List<Task> tasks) {
        if (tasks.isEmpty()) {
            throw new CustomException("Course doesn't have any task.");
        }

        int openText = 0;
        int singleChoice = 0;
        int multipleChoice = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getTaskOrder() != i + 1) {
                throw new CustomException("Tasks are not in the correct order.");
            }
            switch (tasks.get(i).getTaskType().name()) {
                case "OPEN_TEXT" -> openText++;
                case "SINGLE_CHOICE" -> singleChoice++;
                case "MULTIPLE_CHOICE" -> multipleChoice++;
            }
        }

        if (openText == 0 || singleChoice == 0 || multipleChoice == 0) {
            throw new CustomException("Course must have at least one task of each type.");
        }
    }
}
