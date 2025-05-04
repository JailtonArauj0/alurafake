package br.com.alura.AluraFake.domain.repository;

import br.com.alura.AluraFake.domain.model.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>{

}
