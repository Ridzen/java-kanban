package controllers;

import models.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private Path tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    public void init() throws IOException {
        tempFile = Files.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    public void cleanUp() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void shouldCreateAndRetrieveEntities() {
        Task task = new Task(0, "Задача", "Desc", TaskStatus.NEW);
        Epic epic = new Epic(0, "Эпик", "Desc");
        Subtask sub = new Subtask(0, "Подзадача", "Desc", TaskStatus.NEW, epic.getId());

        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(sub);

        assertEquals(task,  manager.getTask(task.getId()));
        assertEquals(epic,  manager.getEpic(epic.getId()));
        assertEquals(sub,   manager.getSubtask(sub.getId()));
    }

    @Test
    public void shouldSaveAndLoadData() {
        Task t = new Task(0, "T", "D", TaskStatus.NEW);
        manager.createTask(t);
        manager.getTask(t.getId());              // в историю

        Epic e = new Epic(0, "E", "D");
        manager.createEpic(e);
        Subtask s = new Subtask(0, "S", "D", TaskStatus.NEW, e.getId());
        manager.createSubtask(s);
        manager.getSubtask(s.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(manager.getAllTasks(),      loaded.getAllTasks());
        assertEquals(manager.getAllEpics(),      loaded.getAllEpics());
        assertEquals(manager.getAllSubtasks(),   loaded.getAllSubtasks());
        assertEquals(ids(manager.getHistory()),  ids(loaded.getHistory()));
    }

    @Test
    public void shouldAutoSaveOnEachMutation() throws IOException {
        Task task = new Task(0, "Auto", "Save", TaskStatus.NEW);
        manager.createTask(task);

        long sizeAfterCreate = Files.size(tempFile);

        task.setStatus(TaskStatus.DONE);
        manager.updateTask(task);

        long sizeAfterUpdate = Files.size(tempFile);
        assertTrue(sizeAfterUpdate > sizeAfterCreate, "Файл не изменился после update()");

        manager.deleteTaskById(task.getId());
        long sizeAfterDelete = Files.size(tempFile);
        assertTrue(sizeAfterDelete > 0, "Файл должен содержать хоть что-то после delete()");
    }

    private static String ids(List<Task> list) {
        StringBuilder sb = new StringBuilder();
        for (Task t : list) sb.append(t.getId()).append(",");
        return sb.toString();
    }
}
