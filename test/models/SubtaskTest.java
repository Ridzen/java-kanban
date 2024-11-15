package models;

import models.Subtask;
import models.TaskStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    @Test
    public void shouldCreateSubtaskWithGivenParameters() {
        Subtask subtask = new Subtask(1, "Подтаска 1", "Описание", TaskStatus.NEW, 100);

        assertEquals(1, subtask.getId());
        assertEquals("Подтаска 1", subtask.getName());
        assertEquals("Описание", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(100, subtask.getEpicId());
    }

    @Test
    public void shouldUpdateSubtaskStatus() {
        Subtask subtask = new Subtask(2, "Подтаска 2", "Описание", TaskStatus.NEW, 100);
        subtask.setStatus(TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, subtask.getStatus());
    }

    @Test
    public void shouldReturnCorrectEpicId() {
        Subtask subtask = new Subtask(3, "Подтаска 3", "Описание", TaskStatus.IN_PROGRESS, 200);
        assertEquals(200, subtask.getEpicId());
    }
}
