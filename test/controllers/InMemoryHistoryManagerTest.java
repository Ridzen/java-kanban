package controllers;

import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldAddTaskToHistory() {
        Task task = new Task(1, "Тестовая Таска", "Описание", TaskStatus.NEW);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    public void shouldNotAddNullTaskToHistory() {
        historyManager.add(null);
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
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

    @Test
    public void shouldRemoveTaskFromHistoryById() {
        Task task1 = new Task(1, "Task 1", "Desc", TaskStatus.NEW);
        Task task2 = new Task(2, "Task 2", "Desc", TaskStatus.NEW);
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    public void shouldRemoveHeadTailAndMiddle() {
        Task t1 = new Task(1, "T1", "D", TaskStatus.NEW);
        Task t2 = new Task(2, "T2", "D", TaskStatus.NEW);
        Task t3 = new Task(3, "T3", "D", TaskStatus.NEW);

        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.add(t3);

        historyManager.remove(1);
        List<Task> afterHeadRemoved = historyManager.getHistory();
        assertEquals(2, afterHeadRemoved.size());
        assertEquals(t2, afterHeadRemoved.get(0));

        historyManager.remove(3);
        List<Task> afterTailRemoved = historyManager.getHistory();
        assertEquals(1, afterTailRemoved.size());
        assertEquals(t2, afterTailRemoved.get(0));

        historyManager.remove(2);
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    public void shouldKeepOnlyLastOccurrenceOfTask() {
        Task task = new Task(1, "Duplicate", "Desc", TaskStatus.NEW);
        historyManager.add(task);
        historyManager.add(task); // повторный просмотр

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }
}
