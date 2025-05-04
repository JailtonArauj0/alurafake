package br.com.alura.AluraFake.controllers;

import br.com.alura.AluraFake.domain.service.CourseService;
import br.com.alura.AluraFake.dtos.request.NewCourseDTO;
import br.com.alura.AluraFake.dtos.response.CourseListItemDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/course/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse) {
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        courseService.createCourse(newCourse, loggedUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/course/all")
    public ResponseEntity<List<CourseListItemDTO>> listAllCourses() {
        List<CourseListItemDTO> courses = courseService.listAllCourses();
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/course/{id}/publish")
    public ResponseEntity publishCourse(@PathVariable("id") Long id) {
        courseService.publishCourse(id);
        return ResponseEntity.ok().build();
    }

}
