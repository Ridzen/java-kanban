package models;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    public void shouldCreateTaskWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task(1, "Test", "Descl", TaskStatus.NEW, Duration.ofMinutes(60), now);
        assertEquals(1, task.getId());
        assertEquals("Test", task.getName());
        assertEquals("Descl", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(Duration.ofMinutes(60), task.getDuration());
        assertEquals(now, task.getStartTime());
    }

    @Test
    public void getEndTimeShouldReturnStartPlusDuration() {
        LocalDateTime start = LocalDateTime.of(2024, 6, 21, 10, 0);
        Task task = new Task(2, "Test2", "Desc2", TaskStatus.NEW, Duration.ofMinutes(90), start);
        assertEquals(start.plusMinutes(90), task.getEndTime());
    }

    @Test
    public void getEndTimeShouldReturnNullIfStartNull() {
        Task task = new Task(3, "Test", "Desc", TaskStatus.NEW, Duration.ofMinutes(60), null);
        assertNull(task.getEndTime());
    }
}
