package controllers;

import models.*;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private Path tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = Files.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void shouldSaveAndLoadTaskWithNewFields() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Task task = new Task(0, "t", "d", TaskStatus.NEW, Duration.ofMinutes(20), now);
        manager.createTask(task);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        Task restored = loaded.getTask(task.getId());

        assertEquals(task.getName(), restored.getName());
        assertEquals(task.getDuration(), restored.getDuration());
        assertEquals(task.getStartTime(), restored.getStartTime());
    }

    @Test
    public void shouldSaveAndRestoreHistory() {
        Task t1 = new Task(0, "t1", "d1", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().withNano(0));
        manager.createTask(t1);
        manager.getTask(t1.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> history = loaded.getHistory();

        assertFalse(history.isEmpty());
        assertEquals(t1.getId(), history.get(0).getId());
    }
}
