package br.com.alura.AluraFake.dtos.request;

import br.com.alura.AluraFake.domain.model.course.Course;
import br.com.alura.AluraFake.domain.model.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class NewCourseDTO {

    @NotNull
    @NotBlank
    private String title;
    @NotNull
    @NotBlank
    @Length(min = 4, max = 255)
    private String description;
    private String emailInstructor;

    public NewCourseDTO() {}

    public Course toEntity(User instructor) {
        return new Course(this.title, this.description, instructor);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmailInstructor() {
        return emailInstructor;
    }

    public void setEmailInstructor(String emailInstructor) {
        this.emailInstructor = emailInstructor;
    }
}
