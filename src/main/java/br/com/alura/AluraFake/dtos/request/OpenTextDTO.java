package br.com.alura.AluraFake.dtos.request;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.Type;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class OpenTextDTO {
    @NotNull
    private Long courseId;

    @NotNull
    @Size(min = 4, max = 255, message = "Statement must be between 4 and 255 characters")
    private String statement;

    @NotNull
    @Min(value = 1, message = "Order must be an integer and greater than 0")
    private Integer order;

    public OpenTextDTO() {
    }

    public OpenTextDTO(Long courseId, String statement, Integer order) {
        this.courseId = courseId;
        this.statement = statement;
        this.order = order;
    }

    public Task toEntity(Course course, Type taskType) {
        return new Task(course, this.statement, this.order, taskType);
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OpenTextDTO that = (OpenTextDTO) o;
        return Objects.equals(courseId, that.courseId) && Objects.equals(statement, that.statement) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, statement, order);
    }
}
