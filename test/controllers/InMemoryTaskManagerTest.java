package controllers;

import controllers.InMemoryTaskManager;
import controllers.TaskManager;
import models.Task;
import models.Epic;
import models.Subtask;
import models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private TaskManager manager;

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    public void shouldCreateAndRetrieveTask() {
        Task task = new Task(0, "Таск 1", "Описание", TaskStatus.NEW);
        manager.createTask(task);
        Task retrievedTask = manager.getTask(task.getId());

        assertNotNull(retrievedTask);
        assertEquals(task, retrievedTask);
    }

    @Test
    public void shouldCreateAndRetrieveEpic() {
        Epic epic = new Epic(0, "Эпик 1", "Описание");
        manager.createEpic(epic);
        Epic retrievedEpic = manager.getEpic(epic.getId());

        assertNotNull(retrievedEpic);
        assertEquals(epic, retrievedEpic);
    }

    @Test
    public void shouldCreateAndRetrieveSubtask() {
        Epic epic = new Epic(1, "Эпик 1", "Описание");
        manager.createEpic(epic);

        Subtask subtask = new Subtask(0, "Подтаска 1", "Описание", TaskStatus.NEW, epic.getId());
        manager.createSubtask(subtask);

        Subtask retrievedSubtask = manager.getSubtask(subtask.getId());
        assertNotNull(retrievedSubtask);
        assertEquals(subtask, retrievedSubtask);
    }

    @Test
    public void shouldReturnCorrectHistory() {
        Task task1 = new Task(0, "Таска 1", "Описание", TaskStatus.NEW);
        manager.createTask(task1);
        manager.getTask(task1.getId());

        Epic epic1 = new Epic(1, "Эпик 1", "Описание");
        manager.createEpic(epic1);
        manager.getEpic(epic1.getId());

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(epic1, history.get(1));
    }
}
