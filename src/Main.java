import controllers.FileBackedTaskManager;
import controllers.TaskManager;
import models.Task;
import models.Epic;
import models.Subtask;
import models.TaskStatus;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new FileBackedTaskManager(Paths.get("tasks.csv"));

        // Добавляем задачи с duration и startTime
        Task task1 = new Task(0, "Task 1.2", "Description 1", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 6, 22, 10, 0));
        Task task2 = new Task(0, "Task 2б", "Description 2", TaskStatus.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2024, 6, 22, 12, 0));
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic(0, "Epic 1", "Epic with subtasks");
        manager.createEpic(epic1);
        // Добавляем subtasks с duration и startTime
        Subtask sub1 = new Subtask(0, "Subtask 1", "For Epic 1", TaskStatus.NEW,
                Duration.ofMinutes(20), LocalDateTime.of(2024, 6, 22, 14, 0), epic1.getId());
        Subtask sub2 = new Subtask(0, "Subtask 2", "For Epic 1", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 6, 22, 15, 0), epic1.getId());
        Subtask sub3 = new Subtask(0, "Subtaskk 3", "For Epic 1", TaskStatus.NEW,
                Duration.ofMinutes(40), LocalDateTime.of(2024, 6, 22, 16, 0), epic1.getId());
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.createSubtask(sub3);

        Epic epic2 = new Epic(0, "Epic 2 ", "No subtasks");
        manager.createEpic(epic2);

        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(sub1.getId());
        manager.getTask(task2.getId());
        manager.getEpic(epic2.getId());
        manager.getSubtask(sub2.getId());
        manager.getSubtask(sub3.getId());
        manager.getTask(task1.getId());

        printHistory(manager, "История после запросов:");

        manager.deleteTaskById(task1.getId());
        printHistory(manager, "История после удаления task 1:");

        manager.deleteEpicById(epic1.getId());
        printHistory(manager, "История после удаления epic 1 (и его subtask-ов):");
    }

    private static void printHistory(TaskManager manager, String header) {
        System.out.println("\n" + header);
        manager.getHistory().forEach(System.out::println);
    }
}
