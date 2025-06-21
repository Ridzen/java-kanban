package models;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    @Test
    public void shouldCreateSubtaskWithGivenParameters() {
        Duration duration = Duration.ofMinutes(45);
        LocalDateTime start = LocalDateTime.of(2024, 6, 20, 12, 0);
        Subtask subtask = new Subtask(1, "Подтаска 1", "Описание", TaskStatus.NEW, duration, start, 100);

        assertEquals(1, subtask.getId());
        assertEquals("Подтаска 1", subtask.getName());
        assertEquals("Описание", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(duration, subtask.getDuration());
        assertEquals(start, subtask.getStartTime());
        assertEquals(100, subtask.getEpicId());
    }

    @Test
    public void shouldUpdateSubtaskStatus() {
        Duration duration = Duration.ofMinutes(10);
        LocalDateTime start = LocalDateTime.now();
        Subtask subtask = new Subtask(2, "Подтаска 2", "Описание", TaskStatus.NEW, duration, start, 100);
        subtask.setStatus(TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, subtask.getStatus());
    }

    @Test
    public void shouldReturnCorrectEpicId() {
        Duration duration = Duration.ofMinutes(15);
        LocalDateTime start = LocalDateTime.now();
        Subtask subtask = new Subtask(3, "Подтаска 3", "Описание", TaskStatus.IN_PROGRESS, duration, start, 200);
        assertEquals(200, subtask.getEpicId());
    }
}
