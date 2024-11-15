package models;

import models.Epic;
import models.Subtask;
import models.TaskStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class EpicTest {

    @Test
    public void shouldCreateEpicWithEmptySubtasks() {
        Epic epic = new Epic(1, "Тестовой эпик", "Описание");

        assertEquals(1, epic.getId());
        assertEquals("Тестовой эпик", epic.getName());
        assertEquals("Описание", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    public void shouldAddAndRemoveSubtask() {
        Epic epic = new Epic(1, "Тестовой Эпик", "Описание");
        Subtask subtask = new Subtask(2, "Подтаска", "Описание подтаски", TaskStatus.NEW, 1);

        epic.addSubtask(subtask);
        assertEquals(1, epic.getSubtasks().size());

        epic.removeSubtask(subtask);
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    public void shouldUpdateEpicStatusBasedOnSubtasks() {
        Epic epic = new Epic(1, "Тестовой Эпик", "Описание");
        Subtask subtask1 = new Subtask(2, "Подтаска 1", "Описание", TaskStatus.NEW, 1);
        Subtask subtask2 = new Subtask(3, "Подтаска 2", "Описание", TaskStatus.DONE, 1);

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.updateStatus();

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }
}
