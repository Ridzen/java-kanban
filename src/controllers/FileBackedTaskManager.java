package controllers;

import models.*;
import exceptions.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path file;

    public FileBackedTaskManager(Path file) {
        this.file = file;
        this.historyManager = Managers.getDefaultHistory();
        if (Files.exists(file)) {
            load();
        }
    }

    public static FileBackedTaskManager loadFromFile(Path file) {
        return new FileBackedTaskManager(file);
    }

    public static TaskManager defaultManager(Path file) {
        return new FileBackedTaskManager(file);
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("type,id,name,status,description,epic");
            writer.newLine();

            List<Task> all = new ArrayList<>();
            all.addAll(getAllTasks());
            all.addAll(getAllEpics());
            all.addAll(getAllSubtasks());
            all.sort(Comparator.comparingInt(Task::getId));

            for (Task task : all) {
                writer.write(taskToString(task));
                writer.newLine();
            }

            writer.newLine();
            writer.write(historyToString(getHistory()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    private void load() {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null && !line.isBlank()) {
                Task task = taskFromString(line);
                switch (task.getType()) {
                    case TASK -> tasks.put(task.getId(), task);
                    case EPIC -> epics.put(task.getId(), (Epic) task);
                    case SUBTASK -> subtasks.put(task.getId(), (Subtask) task);
                }

                if (task.getId() >= currentId) {
                    currentId = task.getId() + 1;
                }
            }

            for (Subtask sub : subtasks.values()) {
                Epic parent = epics.get(sub.getEpicId());
                if (parent != null) {
                    parent.addSubtask(sub);
                }
            }

            String historyLine = reader.readLine();
            if (historyLine != null && !historyLine.isBlank()) {
                for (String idStr : historyLine.split(",")) {
                    int id = Integer.parseInt(idStr.trim());
                    if (tasks.containsKey(id)) {
                        historyManager.add(tasks.get(id));
                    } else if (epics.containsKey(id)) {
                        historyManager.add(epics.get(id));
                    } else if (subtasks.containsKey(id)) {
                        historyManager.add(subtasks.get(id));
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    private static String taskToString(Task task) {
        String epicId = task.getType() == TaskType.SUBTASK ? String.valueOf(((Subtask) task).getEpicId()) : "";
        return String.join(",",
                task.getType().name(),
                String.valueOf(task.getId()),
                escape(task.getName()),
                task.getStatus().name(),
                escape(task.getDescription()),
                epicId);
    }

    private static Task taskFromString(String line) {
        String[] p = line.split(",", -1);
        TaskType type = TaskType.valueOf(p[0]);
        int id = Integer.parseInt(p[1]);
        String name = unescape(p[2]);
        TaskStatus status = TaskStatus.valueOf(p[3]);
        String desc = unescape(p[4]);
        return switch (type) {
            case TASK -> new Task(id, name, desc, status);
            case EPIC -> new Epic(id, name, desc);
            case SUBTASK -> new Subtask(id, name, desc, status, Integer.parseInt(p[5]));
        };
    }

    private static String historyToString(List<Task> history) {
        StringBuilder sb = new StringBuilder();
        history.forEach(t -> sb.append(t.getId()).append(','));
        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    private static String escape(String v) {
        return v.replace("\\", "\\\\").replace(",", "\\,");
    }

    private static String unescape(String v) {
        return v.replace("\\,", ",").replace("\\\\", "\\");
    }

}
