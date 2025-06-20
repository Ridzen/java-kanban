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
}
