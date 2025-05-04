package br.com.alura.AluraFake.controllers;

import br.com.alura.AluraFake.dtos.request.ChoiceDTO;
import br.com.alura.AluraFake.dtos.request.OpenTextDTO;
import br.com.alura.AluraFake.domain.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Secured({"ROLE_INSTRUCTOR"})
    @PostMapping("/task/new/opentext")
    public ResponseEntity newOpenTextExercise(@RequestBody @Valid OpenTextDTO openTextDTO) {
        taskService.saveOpenText(openTextDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Secured({"ROLE_INSTRUCTOR"})
    @PostMapping("/task/new/singlechoice")
    public ResponseEntity newSingleChoice(@RequestBody @Valid ChoiceDTO choiceDTO) {
        taskService.saveSingleChoice(choiceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Secured({"ROLE_INSTRUCTOR"})
    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice(@RequestBody @Valid ChoiceDTO choiceDTO) {
        taskService.saveMultipleChoice(choiceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}