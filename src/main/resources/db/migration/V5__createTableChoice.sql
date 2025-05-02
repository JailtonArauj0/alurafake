CREATE TABLE Choice
(
    id          BIGINT(20) AUTO_INCREMENT,
    option_text VARCHAR(255) NOT NULL,
    is_correct  BOOLEAN      NOT NULL,
    task_id     BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_choice_task FOREIGN KEY (task_id) REFERENCES Task (id) ON DELETE CASCADE
);