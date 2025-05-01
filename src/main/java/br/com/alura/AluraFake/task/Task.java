package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "Task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course courseId;

    @NotNull
    @Size(min = 10, max = 80)
    private String statement;

    @Column(name = "task_order", nullable = false)
    private Integer taskOrder;

    @Column(name = "task_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Type taskType;

    private final LocalDateTime createdAt = LocalDateTime.now();

    public Task() {

    }

    public Task(Course courseId, String statement, Integer order, Type taskType) {
        this.courseId = courseId;
        this.statement = statement;
        this.taskOrder = order;
        this.taskType = taskType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourseId() {
        return courseId;
    }

    public void setCourseId(Course courseId) {
        this.courseId = courseId;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Integer getTaskOrder() {
        return taskOrder;
    }

    public void setTaskOrder(Integer taskOrder) {
        this.taskOrder = taskOrder;
    }

    public Type getTaskType() {
        return taskType;
    }

    public void setTaskType(Type taskType) {
        this.taskType = taskType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
