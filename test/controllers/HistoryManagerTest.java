package controllers;

import controllers.HistoryManager;
import controllers.InMemoryHistoryManager;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {

    private final HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    public void shouldAddTaskToHistoryAndRetrieveIt() {
        Task task = new Task(1, "Тестовая таска", "Описание", TaskStatus.NEW);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    public void shouldNotAddNullTaskToHistory() {
        historyManager.add(null);
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }
}

