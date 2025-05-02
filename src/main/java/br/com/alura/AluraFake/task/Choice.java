package br.com.alura.AluraFake.task;

import jakarta.persistence.*;

@Entity
@Table(name = "Choice")
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_text", nullable = false)
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}
