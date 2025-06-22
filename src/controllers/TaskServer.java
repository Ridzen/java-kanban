package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;


public class TaskServer {
    private final int port;
    private final TaskManager manager;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private HttpServer server;

    public TaskServer(TaskManager manager, int port) {
        this.manager = manager;
        this.port = port;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);

            server.createContext("/tasks/task", this::handleTask);
            server.createContext("/tasks/epic", this::handleEpic);
            server.createContext("/tasks/subtask", this::handleSubtask);
            server.createContext("/tasks/history", this::handleHistory);
            server.createContext("/tasks", this::handlePrioritized);

            server.setExecutor(null);
            server.start();
            System.out.println("HTTP server started at port " + port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleTask(HttpExchange exchange) throws IOException {
        handleEntity(
                exchange,
                Task.class,
                manager::getTask,
                manager::createTask,
                manager::updateTask,
                manager::deleteTaskById
        );
    }

    private void handleEpic(HttpExchange exchange) throws IOException {
        handleEntity(
                exchange,
                Epic.class,
                manager::getEpic,
                manager::createEpic,
                manager::updateEpic,
                manager::deleteEpicById
        );
    }

    private void handleSubtask(HttpExchange exchange) throws IOException {
        handleEntity(
                exchange,
                Subtask.class,
                manager::getSubtask,
                manager::createSubtask,
                manager::updateSubtask,
                manager::deleteSubtaskById
        );
    }

    private <T extends Task> void handleEntity(
            HttpExchange exchange,
            Class<T> clazz,
            java.util.function.Function<Integer, T> getter,
            java.util.function.Consumer<T> creator,
            java.util.function.Consumer<T> updater,
            java.util.function.IntConsumer deleter
    ) throws IOException {
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        String response = "";
        int code = 200;

        try {
            if ("GET".equals(method)) {
                int id = getIdFromQuery(query);
                if (id != 0) {
                    T entity = getter.apply(id);
                    if (entity != null) {
                        response = gson.toJson(entity);
                    } else {
                        code = 404; response = "Not found";
                    }
                } else { // GET без id — получить список
                    response = gson.toJson(getAllOfType(clazz));
                }
            } else if ("POST".equals(method)) {
                T obj = readTask(exchange, clazz);
                if (manager instanceof InMemoryTaskManager imtm &&
                        (obj.getStartTime() != null && obj.getDuration() != null) &&
                        imtm.hasIntersections(obj)) {
                    code = 406;
                    response = "Task time overlaps";
                } else if (obj.getId() == 0) {
                    creator.accept(obj);
                    code = 201;
                    response = "Created";
                } else {
                    updater.accept(obj);
                    response = "Updated";
                }
            } else if ("DELETE".equals(method)) {
                int id = getIdFromQuery(query);
                if (id != 0) {
                    deleter.accept(id);
                    response = "Deleted";
                } else {
                    code = 400;
                    response = "ID required";
                }
            } else {
                code = 405;
                response = "Method Not Allowed";
            }
        } catch (Exception e) {
            code = 500;
            response = "Internal Server Error: " + e.getMessage();
        }
        sendResponse(exchange, response, code);
    }

    private <T extends Task> List<T> getAllOfType(Class<T> clazz) {
        if (clazz.equals(Task.class)) {
            return (List<T>) manager.getAllTasks();
        } else if (clazz.equals(Epic.class)) {
            return (List<T>) manager.getAllEpics();
        } else if (clazz.equals(Subtask.class)) {
            return (List<T>) manager.getAllSubtasks();
        }
        return List.of();
    }

    private void handleHistory(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            String response = gson.toJson(manager.getHistory());
            sendResponse(exchange, response, 200);
        } else {
            sendResponse(exchange, "Method Not Allowed", 405);
        }
    }

    private void handlePrioritized(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            List<Task> prioritized = (manager instanceof InMemoryTaskManager imtm)
                    ? imtm.getPrioritizedTasks()
                    : manager.getAllTasks();
            String response = gson.toJson(prioritized);
            sendResponse(exchange, response, 200);
        } else {
            sendResponse(exchange, "Method Not Allowed", 405);
        }
    }

    private <T extends Task> T readTask(HttpExchange exchange, Class<T> clazz) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, clazz);
    }

    private int getIdFromQuery(String query) {
        if (query == null) return 0;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if ("id".equals(kv[0])) return Integer.parseInt(kv[1]);
        }
        return 0;
    }

    private void sendResponse(HttpExchange exchange, String response, int code) throws IOException {
        exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
