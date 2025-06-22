package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.*;
import exceptions.NotFoundException;
import http.BaseHttpHandler;
import models.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/tasks", new TasksHandler());
        server.createContext("/subtasks", new SubtasksHandler());
        server.createContext("/epics", new EpicsHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritized", new PrioritizedHandler());
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }
    

    class TasksHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                String query = exchange.getRequestURI().getQuery();

                if ("GET".equals(method)) {
                    if (query != null && query.startsWith("id=")) {
                        int id = getIdFromQuery(query);
                        Task task = manager.getTask(id);
                        sendText(exchange, gson.toJson(task), 200);
                    } else {
                        sendText(exchange, gson.toJson(manager.getAllTasks()), 200);
                    }
                }
                else if ("POST".equals(method)) {
                    Task task = readTask(exchange, Task.class);
                    if (task.getId() == 0) {
                        if (manager instanceof InMemoryTaskManager imtm && imtm.hasIntersections(task)) {
                            sendHasIntersections(exchange);
                            return;
                        }
                        manager.createTask(task);
                        sendText(exchange, "Created", 201);
                    } else {
                        if (manager instanceof InMemoryTaskManager imtm && imtm.hasIntersections(task)) {
                            sendHasIntersections(exchange);
                            return;
                        }
                        manager.updateTask(task);
                        sendText(exchange, "Updated", 201);
                    }
                }
                else if ("DELETE".equals(method)) {
                    if (query != null && query.startsWith("id=")) {
                        int id = getIdFromQuery(query);
                        manager.deleteTaskById(id);
                        sendText(exchange, "Deleted", 200);
                    } else {
                        manager.clearTasks();
                        sendText(exchange, "All tasks deleted", 200);
                    }
                } else {
                    sendText(exchange, "Method Not Allowed", 405);
                }
            } catch (NotFoundException nfe) {
                sendNotFound(exchange, nfe.getMessage());
            } catch (Exception ex) {
                sendServerError(exchange, "Internal Server Error");
            }
        }
    }

    class SubtasksHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                String query = exchange.getRequestURI().getQuery();

                if ("GET".equals(method)) {
                    if (query != null && query.startsWith("id=")) {
                        int id = getIdFromQuery(query);
                        Subtask subtask = manager.getSubtask(id);
                        sendText(exchange, gson.toJson(subtask), 200);
                    } else {
                        sendText(exchange, gson.toJson(manager.getAllSubtasks()), 200);
                    }
                }
                else if ("POST".equals(method)) {
                    Subtask subtask = readTask(exchange, Subtask.class);
                    if (subtask.getId() == 0) {
                        if (manager instanceof InMemoryTaskManager imtm && imtm.hasIntersections(subtask)) {
                            sendHasIntersections(exchange);
                            return;
                        }
                        manager.createSubtask(subtask);
                        sendText(exchange, "Created", 201);
                    } else {
                        if (manager instanceof InMemoryTaskManager imtm && imtm.hasIntersections(subtask)) {
                            sendHasIntersections(exchange);
                            return;
                        }
                        manager.updateSubtask(subtask);
                        sendText(exchange, "Updated", 201);
                    }
                }
                else if ("DELETE".equals(method)) {
                    if (query != null && query.startsWith("id=")) {
                        int id = getIdFromQuery(query);
                        manager.deleteSubtaskById(id);
                        sendText(exchange, "Deleted", 200);
                    } else {
                        manager.clearSubtasks();
                        sendText(exchange, "All subtasks deleted", 200);
                    }
                } else {
                    sendText(exchange, "Method Not Allowed", 405);
                }
            } catch (NotFoundException nfe) {
                sendNotFound(exchange, nfe.getMessage());
            } catch (Exception ex) {
                sendServerError(exchange, "Internal Server Error");
            }
        }
    }

    class EpicsHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                String query = exchange.getRequestURI().getQuery();

                if ("GET".equals(method)) {
                    if (query != null && query.startsWith("id=")) {
                        int id = getIdFromQuery(query);
                        Epic epic = manager.getEpic(id);
                        sendText(exchange, gson.toJson(epic), 200);
                    } else {
                        sendText(exchange, gson.toJson(manager.getAllEpics()), 200);
                    }
                }
                else if ("POST".equals(method)) {
                    Epic epic = readTask(exchange, Epic.class);
                    if (epic.getId() == 0) {
                        manager.createEpic(epic);
                        sendText(exchange, "Created", 201);
                    } else {
                        manager.updateEpic(epic);
                        sendText(exchange, "Updated", 201);
                    }
                }
                else if ("DELETE".equals(method)) {
                    if (query != null && query.startsWith("id=")) {
                        int id = getIdFromQuery(query);
                        manager.deleteEpicById(id);
                        sendText(exchange, "Deleted", 200);
                    } else {
                        manager.clearEpics();
                        sendText(exchange, "All epics deleted", 200);
                    }
                } else {
                    sendText(exchange, "Method Not Allowed", 405);
                }
            } catch (NotFoundException nfe) {
                sendNotFound(exchange, nfe.getMessage());
            } catch (Exception ex) {
                sendServerError(exchange, "Internal Server Error");
            }
        }
    }

    class HistoryHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("GET".equals(exchange.getRequestMethod())) {
                    sendText(exchange, gson.toJson(manager.getHistory()), 200);
                } else {
                    sendText(exchange, "Method Not Allowed", 405);
                }
            } catch (Exception ex) {
                sendServerError(exchange, "Internal Server Error");
            }
        }
    }

    class PrioritizedHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("GET".equals(exchange.getRequestMethod())) {
                    List<Task> prioritized = (manager instanceof InMemoryTaskManager imtm)
                            ? imtm.getPrioritizedTasks()
                            : manager.getAllTasks();
                    sendText(exchange, gson.toJson(prioritized), 200);
                } else {
                    sendText(exchange, "Method Not Allowed", 405);
                }
            } catch (Exception ex) {
                sendServerError(exchange, "Internal Server Error");
            }
        }
    }

    private int getIdFromQuery(String query) {
        if (query == null) return 0;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if ("id".equals(kv[0]) && kv.length > 1) return Integer.parseInt(kv[1]);
        }
        return 0;
    }

    private <T extends Task> T readTask(HttpExchange exchange, Class<T> clazz) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, clazz);
    }

    public static Gson getGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}
