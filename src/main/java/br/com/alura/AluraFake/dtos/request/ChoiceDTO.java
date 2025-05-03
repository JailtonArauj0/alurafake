package br.com.alura.AluraFake.dtos.request;

import br.com.alura.AluraFake.domain.model.course.Course;
import br.com.alura.AluraFake.domain.model.task.Task;
import br.com.alura.AluraFake.domain.model.task.Type;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Objects;

public class ChoiceDTO {
    @NotNull
    private Long courseId;

    @NotNull
    @Size(min = 4, max = 255, message = "Statement must be between 4 and 255 characters")
    private String statement;

    @NotNull
    @Min(value = 1, message = "Order must be an integer and greater than 0")
    private Integer order;

    @NotNull
    @Size(min = 2, max = 5, message = "The number of options must be between 2 and 5")
    @Valid
    private List<Option> options;

    public ChoiceDTO() {
    }

    public ChoiceDTO(Long courseId, String statement, Integer order, List<Option> options) {
        this.courseId = courseId;
        this.statement = statement;
        this.order = order;
        this.options = options;
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

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChoiceDTO choiceDTO = (ChoiceDTO) o;
        return Objects.equals(courseId, choiceDTO.courseId) && Objects.equals(statement, choiceDTO.statement) && Objects.equals(order, choiceDTO.order) && Objects.equals(options, choiceDTO.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, statement, order, options);
    }
}
