package br.com.alura.AluraFake.dtos.request;

import br.com.alura.AluraFake.domain.model.task.Choice;
import br.com.alura.AluraFake.domain.model.task.Task;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class Option {
    @NotNull
    @Size(min = 4, max = 80, message = "Option must be between 4 and 80 characters")
    private String option;

    @NotNull
    private Boolean isCorrect;

    public Option() {
    }

    public Option(String option, Boolean isCorrect) {
        this.option = option;
        this.isCorrect = isCorrect;
    }

    public Choice toEntity(Task task) {
        return new Choice(this.option, this.isCorrect, task);
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Boolean isCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Option option1 = (Option) o;
        return Objects.equals(option, option1.option) && Objects.equals(isCorrect, option1.isCorrect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(option, isCorrect);
    }
}
