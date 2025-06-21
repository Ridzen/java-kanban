package controllers;

import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager manager;

    @BeforeEach
    public void setup() {
        manager = new InMemoryTaskManager();
    }

    @Test
    public void getPrioritizedTasksReturnsByStartTime() {
        LocalDateTime now = LocalDateTime.of(2024, 6, 21, 10, 0);
        Task t1 = new Task(0, "t1", "d1", TaskStatus.NEW, Duration.ofMinutes(30), now.plusHours(1));
        Task t2 = new Task(0, "t2", "d2", TaskStatus.NEW, Duration.ofMinutes(30), now);
        manager.createTask(t1);
        manager.createTask(t2);
        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
        assertEquals(t2, prioritized.get(0));
        assertEquals(t1, prioritized.get(1));
    }

    @Test
    public void intersectionShouldReturnTrueIfOverlaps() {
        LocalDateTime t1 = LocalDateTime.of(2024, 6, 21, 12, 0);
        Task a = new Task(0, "A", "d", TaskStatus.NEW, Duration.ofMinutes(60), t1);
        Task b = new Task(0, "B", "d", TaskStatus.NEW, Duration.ofMinutes(60), t1.plusMinutes(30));
        assertTrue(manager.isTimeIntersect(a, b));
    }

    @Test
    public void intersectionShouldReturnFalseIfNotOverlaps() {
        LocalDateTime t1 = LocalDateTime.of(2024, 6, 21, 12, 0);
        Task a = new Task(0, "A", "d", TaskStatus.NEW, Duration.ofMinutes(60), t1);
        Task b = new Task(0, "B", "d", TaskStatus.NEW, Duration.ofMinutes(60), t1.plusMinutes(61));
        assertFalse(manager.isTimeIntersect(a, b));
    }

    @Test
    public void shouldNotAddTaskIfTimeIntersects() {
        LocalDateTime now = LocalDateTime.of(2024, 6, 21, 10, 0);
        Task t1 = new Task(0, "Task 1", "Desc", TaskStatus.NEW, Duration.ofMinutes(60), now);
        Task t2 = new Task(0, "Task 2", "Desc", TaskStatus.NEW, Duration.ofMinutes(60), now.plusMinutes(30));

        manager.createTask(t1);
        manager.createTask(t2); // пересекаться - не добавляться

        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals(t1, tasks.get(0));
    }

    @Test
    public void shouldAddTaskWithoutStartTime() {
        Task t1 = new Task(0, "Task 1", "Desc", TaskStatus.NEW, Duration.ofMinutes(60), null);
        manager.createTask(t1);

        List<Task> tasks = manager.getAllTasks();
        List<Task> prioritized = manager.getPrioritizedTasks();

        assertEquals(1, tasks.size());
        assertTrue(prioritized.isEmpty());
    }

    @Test
    public void shouldNotAddSubtaskIfTimeIntersects() {
        Epic epic = new Epic(0, "Epic 1", "Desc");
        manager.createEpic(epic);

        LocalDateTime now = LocalDateTime.of(2024, 6, 21, 14, 0);
        Subtask s1 = new Subtask(0, "Subtask 1", "Desc", TaskStatus.NEW, Duration.ofMinutes(60), now, epic.getId());
        Subtask s2 = new Subtask(0, "Subtask 2", "Desc", TaskStatus.NEW, Duration.ofMinutes(60), now.plusMinutes(30), epic.getId());

        manager.createSubtask(s1);
        manager.createSubtask(s2); // пересекается поидее тут, тесты успешные

        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals(s1, subtasks.get(0));
    }

    @Test
    public void shouldAddSubtaskWithoutStartTime() {
        Epic epic = new Epic(0, "Epic 1", "Desc");
        manager.createEpic(epic);

        Subtask s1 = new Subtask(0, "Subtask 1", "Desc", TaskStatus.NEW, Duration.ofMinutes(60), null, epic.getId());
        manager.createSubtask(s1);

        List<Subtask> subtasks = manager.getAllSubtasks();
        List<Task> prioritized = manager.getPrioritizedTasks();

        assertEquals(1, subtasks.size());
        assertTrue(prioritized.isEmpty());
    }
}
