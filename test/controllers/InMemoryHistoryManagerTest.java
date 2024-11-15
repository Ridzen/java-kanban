package controllers;

import controllers.InMemoryHistoryManager;
import controllers.HistoryManager;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private final HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    public void shouldAddTaskToHistory() {
        Task task = new Task(1, "Тестовая Таска", "Описание", TaskStatus.NEW);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    public void shouldLimitHistorySizeToTen() {
        for (int i = 1; i <= 12; i++) {
            historyManager.add(new Task(i, "Таска " + i, "Описание", TaskStatus.NEW));
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size());
        assertEquals(3, history.get(0).getId());
    }
}
