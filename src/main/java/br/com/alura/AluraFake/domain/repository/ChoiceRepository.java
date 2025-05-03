package br.com.alura.AluraFake.domain.repository;

import br.com.alura.AluraFake.domain.model.task.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
}
