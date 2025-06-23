package controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import http.*;
import models.*;
import exceptions.NotFoundException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager;
    private static final Gson gson = GsonFactory.build();

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/tasks", new TasksHandler(manager, gson));
        server.createContext("/subtasks", new SubtasksHandler(manager, gson));
        server.createContext("/epics", new EpicsHandler(manager, gson));
        server.createContext("/history", new HistoryHandler(manager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(manager, gson));
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }

    static class TasksHandler extends BaseHttpHandler {
        public TasksHandler(TaskManager manager, Gson gson) {
            super(manager, gson);
        }

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
                } else if ("POST".equals(method)) {
                    String body = readBody(exchange);
                    Task task = gson.fromJson(body, Task.class);
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
                } else if ("DELETE".equals(method)) {
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

    static class SubtasksHandler extends BaseHttpHandler {
        public SubtasksHandler(TaskManager manager, Gson gson) {
            super(manager, gson);
        }

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
                } else if ("POST".equals(method)) {
                    String body = readBody(exchange);
                    Subtask subtask = gson.fromJson(body, Subtask.class);
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
                } else if ("DELETE".equals(method)) {
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

    static class EpicsHandler extends BaseHttpHandler {
        public EpicsHandler(TaskManager manager, Gson gson) {
            super(manager, gson);
        }

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
                } else if ("POST".equals(method)) {
                    String body = readBody(exchange);
                    Epic epic = gson.fromJson(body, Epic.class);
                    if (epic.getId() == 0) {
                        manager.createEpic(epic);
                        sendText(exchange, "Created", 201);
                    } else {
                        manager.updateEpic(epic);
                        sendText(exchange, "Updated", 201);
                    }
                } else if ("DELETE".equals(method)) {
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

    static class HistoryHandler extends BaseHttpHandler {
        public HistoryHandler(TaskManager manager, Gson gson) {
            super(manager, gson);
        }

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

    static class PrioritizedHandler extends BaseHttpHandler {
        public PrioritizedHandler(TaskManager manager, Gson gson) {
            super(manager, gson);
        }

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

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}
