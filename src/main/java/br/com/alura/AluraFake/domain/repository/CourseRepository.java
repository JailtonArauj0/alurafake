package br.com.alura.AluraFake.domain.repository;

import br.com.alura.AluraFake.domain.model.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long>{

}
