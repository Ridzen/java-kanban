import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nМеню:");
            System.out.println("1. Создать задачу");
            System.out.println("2. Создать эпик");
            System.out.println("3. Создать подзадачу");
            System.out.println("4. Показать все задачи");
            System.out.println("5. Показать все эпики");
            System.out.println("6. Показать все подзадачи");
            System.out.println("7. Обновить задачу");
            System.out.println("8. Обновить эпик");
            System.out.println("9. Обновить подзадачу");
            System.out.println("10. Обновить статус задачи");
            System.out.println("11. Обновить статус подзадачи");
            System.out.println("12. Удалить задачу");
            System.out.println("13. Очистить все задачи");
            System.out.println("14. Выйти");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> createTask(manager, scanner);
                case 2 -> createEpic(manager, scanner);
                case 3 -> createSubtask(manager, scanner);
                case 4 -> showAllTasks(manager);
                case 5 -> showAllEpics(manager);
                case 6 -> showAllSubtasks(manager);
                case 7 -> updateTask(manager, scanner);
                case 8 -> updateEpic(manager, scanner);
                case 9 -> updateSubtask(manager, scanner);
                case 10 -> updateTaskStatus(manager, scanner);
                case 11 -> updateSubtaskStatus(manager, scanner);
                case 12 -> deleteTask(manager, scanner);
                case 13 -> clearAllTasks(manager);
                case 14 -> {
                    System.out.println("Выход из программы.");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Некорректный ввод. Попробуйте снова.");
            }
        }
    }

    private static void createTask(TaskManager manager, Scanner scanner) {
        System.out.print("Введите название задачи: ");
        String name = scanner.nextLine();
        System.out.print("Введите описание задачи: ");
        String description = scanner.nextLine();
        Task task = new Task(0, name, description, TaskStatus.NEW);
        manager.createTask(task);
        System.out.println("Задача создана: " + task);
    }

    private static void createEpic(TaskManager manager, Scanner scanner) {
        System.out.print("Введите название эпика: ");
        String name = scanner.nextLine();
        System.out.print("Введите описание эпика: ");
        String description = scanner.nextLine();
        Epic epic = new Epic(0, name, description);
        manager.createEpic(epic);
        System.out.println("Эпик создан: " + epic);
    }

    private static void createSubtask(TaskManager manager, Scanner scanner) {
        System.out.print("Введите название подзадачи: ");
        String name = scanner.nextLine();
        System.out.print("Введите описание подзадачи: ");
        String description = scanner.nextLine();
        System.out.print("Введите ID эпика для этой подзадачи: ");
        int epicId = scanner.nextInt();
        scanner.nextLine();  // Очистка буфера
        Subtask subtask = new Subtask(0, name, description, TaskStatus.NEW, epicId);
        manager.createSubtask(subtask);
        System.out.println("Подзадача создана: " + subtask);
    }

    private static void showAllTasks(TaskManager manager) {
        System.out.println("Все задачи:");
        manager.getAllTasks().values().forEach(System.out::println);
    }

    private static void showAllEpics(TaskManager manager) {
        System.out.println("Все эпики:");
        manager.getAllEpics().values().forEach(System.out::println);
    }

    private static void showAllSubtasks(TaskManager manager) {
        System.out.println("Все подзадачи:");
        manager.getAllSubtasks().values().forEach(System.out::println);
    }

    private static void updateTask(TaskManager manager, Scanner scanner) {
        System.out.print("Введите ID задачи для обновления: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Очистка буфера
        Task task = manager.getTaskById(id);
        if (task == null) {
            System.out.println("Задача с таким ID не найдена.");
            return;
        }
        System.out.print("Введите новое название задачи: ");
        String name = scanner.nextLine();
        System.out.print("Введите новое описание задачи: ");
        String description = scanner.nextLine();
        task.setName(name);
        task.setDescription(description);
        manager.updateTask(task);
        System.out.println("Задача обновлена: " + task);
    }

    private static void updateEpic(TaskManager manager, Scanner scanner) {
        System.out.print("Введите ID эпика для обновления: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Очистка буфера
        Epic epic = manager.getEpicById(id);
        if (epic == null) {
            System.out.println("Эпик с таким ID не найден.");
            return;
        }
        System.out.print("Введите новое название эпика: ");
        String name = scanner.nextLine();
        System.out.print("Введите новое описание эпика: ");
        String description = scanner.nextLine();
        epic.setName(name);
        epic.setDescription(description);
        manager.updateEpic(epic);
        System.out.println("Эпик обновлён: " + epic);
    }

    private static void updateSubtask(TaskManager manager, Scanner scanner) {
        System.out.print("Введите ID подзадачи для обновления: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Очистка буфера
        Subtask subtask = manager.getSubtaskById(id);
        if (subtask == null) {
            System.out.println("Подзадача с таким ID не найдена.");
            return;
        }
        System.out.print("Введите новое название подзадачи: ");
        String name = scanner.nextLine();
        System.out.print("Введите новое описание подзадачи: ");
        String description = scanner.nextLine();
        subtask.setName(name);
        subtask.setDescription(description);
        manager.updateSubtask(subtask);

        Epic epic = manager.getEpicById(subtask.getEpicId());
        if (epic != null) {
            epic.updateStatus();
        }

        System.out.println("Подзадача обновлена: " + subtask);
    }

    private static void updateTaskStatus(TaskManager manager, Scanner scanner) {
        System.out.print("Введите ID задачи для обновления статуса: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Очистка буфера
        Task task = manager.getTaskById(id);
        if (task == null) {
            System.out.println("Задача с таким ID не найдена.");
            return;
        }
        System.out.println("Выберите новый статус для задачи: ");
        System.out.println("1. NEW");
        System.out.println("2. IN_PROGRESS");
        System.out.println("3. DONE");
        int statusChoice = scanner.nextInt();
        scanner.nextLine();  // Очистка буфера

        switch (statusChoice) {
            case 1 -> task.setStatus(TaskStatus.NEW);
            case 2 -> task.setStatus(TaskStatus.IN_PROGRESS);
            case 3 -> task.setStatus(TaskStatus.DONE);
            default -> {
                System.out.println("Некорректный ввод статуса.");
                return;
            }
        }
        manager.updateTask(task);
        System.out.println("Статус задачи обновлён: " + task);
    }

    private static void updateSubtaskStatus(TaskManager manager, Scanner scanner) {
        System.out.print("Введите ID подзадачи для обновления статуса: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Очистка буфера
        Subtask subtask = manager.getSubtaskById(id);
        if (subtask == null) {
            System.out.println("Подзадача с таким ID не найдена.");
            return;
        }

        System.out.println("Выберите новый статус для подзадачи: ");
        System.out.println("1. NEW");
        System.out.println("2. IN_PROGRESS");
        System.out.println("3. DONE");
        int statusChoice = scanner.nextInt();
        scanner.nextLine();  // Очистка буфера

        switch (statusChoice) {
            case 1 -> subtask.setStatus(TaskStatus.NEW);
            case 2 -> subtask.setStatus(TaskStatus.IN_PROGRESS);
            case 3 -> subtask.setStatus(TaskStatus.DONE);
            default -> {
                System.out.println("Некорректный ввод статуса.");
                return;
            }
        }

        manager.updateSubtask(subtask);

        Epic epic = manager.getEpicById(subtask.getEpicId());
        if (epic != null) {
            epic.updateStatus();
        }

        System.out.println("Статус подзадачи обновлён: " + subtask);
        System.out.println("Статус эпика обновлён: " + epic);
    }

    private static void deleteTask(TaskManager manager, Scanner scanner) {
        System.out.print("Введите ID задачи для удаления: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Очистка буфера
        manager.deleteTaskById(id);
        System.out.println("Задача удалена.");
    }

    private static void clearAllTasks(TaskManager manager) {
        manager.clearTasks();
        manager.clearEpics();
        manager.clearSubtasks();
        System.out.println("Все задачи, эпики и подзадачи были удалены.");
    }
}