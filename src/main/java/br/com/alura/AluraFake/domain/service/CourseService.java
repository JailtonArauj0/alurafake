package br.com.alura.AluraFake.domain.service;

import br.com.alura.AluraFake.domain.repository.CourseRepository;
import br.com.alura.AluraFake.dtos.response.CourseListItemDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseListItemDTO> listAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseListItemDTO::new)
                .toList();
    }
}
