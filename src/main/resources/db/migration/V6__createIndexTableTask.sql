CREATE INDEX idx_task_course ON Task(course_id);
CREATE INDEX idx_task_course_order ON Task(course_id, task_order);