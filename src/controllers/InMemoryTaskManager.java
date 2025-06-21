package controllers;

import models.*;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    protected int currentId = 1;

    protected final Set<Task> prioritizedTasks = new TreeSet<>((a, b) -> {
        LocalDateTime at = a.getStartTime();
        LocalDateTime bt = b.getStartTime();
        if (at == null && bt == null) return Integer.compare(a.getId(), b.getId());
        if (at == null) return 1;
        if (bt == null) return -1;
        int cmp = at.compareTo(bt);
        if (cmp != 0) return cmp;
        return Integer.compare(a.getId(), b.getId());
    });

    protected int generateId() {
        return currentId++;
    }

    @Override
    public void createTask(Task task) {
        task.setId(generateId());
        if (canBeAdded(task)) {
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null && task.getDuration() != null) {
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(generateId());
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null && canBeAdded(subtask)) {
            subtasks.put(subtask.getId(), subtask);
            epic.addSubtask(subtask);
            if (subtask.getStartTime() != null && subtask.getDuration() != null) {
                prioritizedTasks.add(subtask);
            }
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) return;

        if (canBeAdded(task)) {
            prioritizedTasks.remove(tasks.get(task.getId()));
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null && task.getDuration() != null) {
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) return;
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) return;

        if (canBeAdded(subtask)) {
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.updateStatusAndTime();
            }
            if (subtask.getStartTime() != null && subtask.getDuration() != null) {
                prioritizedTasks.add(subtask);
            }
        }
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        historyManager.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        historyManager.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
            }
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        historyManager.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask);
            }
            prioritizedTasks.remove(subtask);
        }
    }

    @Override
    public void clearTasks() {
        for (Task t : tasks.values()) {
            prioritizedTasks.remove(t);
            historyManager.remove(t.getId());
        }
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            for (Subtask sub : epic.getSubtasks()) {
                historyManager.remove(sub.getId());
                prioritizedTasks.remove(sub);
            }
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Subtask s : subtasks.values()) {
            historyManager.remove(s.getId());
            prioritizedTasks.remove(s);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.updateStatusAndTime();
        }
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

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected boolean isTimeIntersect(Task a, Task b) {
        if (a.getStartTime() == null || a.getEndTime() == null || b.getStartTime() == null || b.getEndTime() == null)
            return false;
        return !(a.getEndTime().isBefore(b.getStartTime()) || b.getEndTime().isBefore(a.getStartTime()));
    }

    private boolean hasIntersections(Task task) {
        return prioritizedTasks.stream()
                .filter(t -> t.getId() != task.getId())
                .anyMatch(t -> isTimeIntersect(t, task));
    }

    private boolean canBeAdded(Task task) {
        if (task.getStartTime() != null && task.getDuration() != null) {
            if (hasIntersections(task)) {
                return false;
            }
        }
        return true;
    }
}
