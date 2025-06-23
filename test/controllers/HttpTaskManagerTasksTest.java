package controllers;

import com.google.gson.Gson;
import models.*;
import org.junit.jupiter.api.*;
import utils.GsonFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {
    private HttpTaskServer taskServer;
    private TaskManager manager;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = GsonFactory.build(); // используем только адаптер!
        taskServer.start();
        manager.clearTasks();
        manager.clearEpics();
        manager.clearSubtasks();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    @Test
    public void shouldAddTaskViaHttp() throws IOException, InterruptedException {
        Task task = new Task(0, "TestTask", "desc", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        String body = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllTasks().size());
        assertEquals("TestTask", manager.getAllTasks().get(0).getName());
    }

    @Test
    public void shouldGetTaskByIdViaHttp() throws IOException, InterruptedException {
        Task task = new Task(0, "TestTask", "desc", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        manager.createTask(task);
        int id = task.getId();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=" + id))
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Task returned = gson.fromJson(response.body(), Task.class);
        assertEquals(id, returned.getId());
    }

    @Test
    public void shouldDeleteTaskByIdViaHttp() throws IOException, InterruptedException {
        Task task = new Task(0, "ToDelete", "desc", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        manager.createTask(task);
        int id = task.getId();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=" + id))
                .DELETE()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllTasks().isEmpty());
    }
}
