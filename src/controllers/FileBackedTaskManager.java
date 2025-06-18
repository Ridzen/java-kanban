package controllers;

import models.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();

    private final Map<Integer, Epic> epics = new HashMap<>();

    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private final Path file;

    private int currentId = 1;

    public FileBackedTaskManager(Path file) {
        this.file = file;
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

    private int generateId() {
        return currentId++;
    }

    @Override
    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic parent = epics.get(subtask.getEpicId());
        if (parent != null) {
            parent.addSubtask(subtask);
        }
        save();
    }

    @Override
    public Task getTask(int id) {
        Task t = tasks.get(id);
        historyManager.add(t);
        save();
        return t;
    }

    @Override
    public Epic getEpic(int id) {
        Epic e = epics.get(id);
        historyManager.add(e);
        save();
        return e;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask s = subtasks.get(id);
        historyManager.add(s);
        save();
        return s;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic parent = epics.get(subtask.getEpicId());
        if (parent != null) {
            parent.updateStatus();
        }
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        historyManager.remove(id);
        if (epic != null) {
            for (Subtask s : epic.getSubtasks()) {
                subtasks.remove(s.getId());
                historyManager.remove(s.getId());
            }
        }
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask s = subtasks.remove(id);
        historyManager.remove(id);
        if (s != null) {
            Epic parent = epics.get(s.getEpicId());
            if (parent != null) {
                parent.removeSubtask(s);
            }
        }
        save();
    }

    @Override
    public void clearTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
        save();
    }

    @Override
    public void clearEpics() {
        epics.values().forEach(e -> {
            historyManager.remove(e.getId());
            e.getSubtasks().forEach(st -> historyManager.remove(st.getId()));
        });
        epics.clear();
        subtasks.clear();
        save();
    }

    @Override
    public void clearSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();
        epics.values().forEach(Epic::updateStatus);
        save();
    }

    private void save() {
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            w.write("type,id,name,status,description,epic");
            w.newLine();

            for (Task t : getAllTasks()) {
                w.write(taskToString(t));
                w.newLine();
            }
            for (Epic e : getAllEpics()) {
                w.write(taskToString(e));
                w.newLine();
            }
            for (Subtask s : getAllSubtasks()) {
                w.write(taskToString(s));
                w.newLine();
            }

            w.newLine();
            w.write(historyToString());
        } catch (IOException ex) {
            throw new RuntimeException("Ошибка сохранения файла " + file, ex);
        }
    }

    private void load() {
        try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line = r.readLine();
            while ((line = r.readLine()) != null && !line.isBlank()) {
                Task t = taskFromString(line);
                int id = t.getId();
                currentId = Math.max(currentId, id + 1);
                if (t instanceof Epic e) {
                    epics.put(id, e);
                } else if (t instanceof Subtask s) {
                    subtasks.put(id, s);
                } else {
                    tasks.put(id, t);
                }
            }
            for (Subtask s : subtasks.values()) {
                Epic parent = epics.get(s.getEpicId());
                if (parent != null) {
                    parent.addSubtask(s);
                }
            }

            String historyLine = r.readLine();
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
        } catch (IOException ex) {
            throw new RuntimeException("Ошибка чтения файла " + file, ex);
        }
    }

    private String taskToString(Task t) {
        String type = (t instanceof Epic) ? "EPIC" : (t instanceof Subtask) ? "SUBTASK" : "TASK";
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(',')
                .append(t.getId()).append(',')
                .append(escape(t.getName())).append(',')
                .append(t.getStatus()).append(',')
                .append(escape(t.getDescription())).append(',');
        if (t instanceof Subtask s) {
            sb.append(s.getEpicId());
        }
        return sb.toString();
    }

    private Task taskFromString(String line) {
        String[] p = split(line);
        String type = p[0];
        int id = Integer.parseInt(p[1]);
        String name = unescape(p[2]);
        TaskStatus status = TaskStatus.valueOf(p[3]);
        String desc = unescape(p[4]);
        return switch (type) {
            case "TASK" -> new Task(id, name, desc, status);
            case "EPIC" -> new Epic(id, name, desc);
            case "SUBTASK" -> new Subtask(id, name, desc, status, Integer.parseInt(p[5]));
            default -> throw new IllegalStateException("Неизвестный type: " + type);
        };
    }

    private String historyToString() {
        StringBuilder sb = new StringBuilder();
        for (Task t : historyManager.getHistory()) {
            sb.append(t.getId()).append(',');
        }
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

    private static String[] split(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean esc = false;
        for (char c : line.toCharArray()) {
            if (esc) {
                cur.append(c);
                esc = false;
            } else if (c == '\\') {
                esc = true;
            } else if (c == ',') {
                parts.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }
}
