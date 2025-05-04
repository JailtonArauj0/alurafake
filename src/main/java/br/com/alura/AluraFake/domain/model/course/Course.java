package br.com.alura.AluraFake.domain.model.course;

import br.com.alura.AluraFake.domain.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Size(max = 50)
    @NotNull
    private String title;

    @Size(max = 255)
    @NotNull
    private String description;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private User instructor;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime publishedAt;

    @Deprecated
    public Course(){}

    public Course(String title, String description, User instructor) {
        Assert.isTrue(instructor.isInstructor(), "User should be an instructor");
        this.title = title;
        this.instructor = instructor;
        this.description = description;
        this.status = Status.BUILDING;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
}
