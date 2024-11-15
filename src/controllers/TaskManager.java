package controllers;

import models.Task;
import models.Epic;
import models.Subtask;
import java.util.List;

public interface TaskManager {
    void createTask(Task task);
    void createEpic(Epic epic);
    void createSubtask(Subtask subtask);

    Task getTask(int id);
    Epic getEpic(int id);
    Subtask getSubtask(int id);

    List<Task> getAllTasks(); // Метод для получения всех задач
    List<Epic> getAllEpics(); // Метод для получения всех эпиков
    List<Subtask> getAllSubtasks(); // Метод для получения всех подзадач

    List<Task> getHistory(); // Метод для получения истории

    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);

    void deleteTaskById(int id);
    void deleteEpicById(int id);
    void deleteSubtaskById(int id);

    void clearTasks();
    void clearEpics();
    void clearSubtasks();
}
