package models;

import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    public void shouldCreateTaskWithGivenParameters() {
        Task task = new Task(1, "Тестовая таска", "Описание", TaskStatus.NEW);

        assertEquals(1, task.getId());
        assertEquals("Тестовая таска", task.getName());
        assertEquals("Описание", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    public void shouldUpdateTaskStatus() {
        Task task = new Task(1, "Тестовая таска", "Описание", TaskStatus.NEW);
        task.setStatus(TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, task.getStatus());
    }
}
