package br.com.alura.AluraFake.repositories;

import br.com.alura.AluraFake.task.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
}
