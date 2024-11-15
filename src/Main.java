import controllers.InMemoryTaskManager;
import controllers.TaskManager;
import controllers.Managers;
import models.Task;
import models.Epic;
import models.Subtask;
import models.TaskStatus;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task(0, "Task 1", "Description 1", TaskStatus.NEW);
        manager.createTask(task1);
        System.out.println("Tasks: " + manager.getAllTasks());

        Epic epic1 = new Epic(0, "Epic 1", "Epic Description");
        manager.createEpic(epic1);
        System.out.println("Epics: " + manager.getAllEpics());

        System.out.println("History: " + manager.getHistory());
    }
}
