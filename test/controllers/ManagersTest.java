package controllers;

import controllers.Managers;
import controllers.TaskManager;
import controllers.HistoryManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    public void shouldReturnNonNullTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager ничего не содержит");
    }

    @Test
    public void shouldReturnNonNullHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager ничего не содержит");
    }

    @Test
    public void shouldReturnDifferentInstances() {
        TaskManager taskManager1 = Managers.getDefault();
        TaskManager taskManager2 = Managers.getDefault();
        assertNotSame(taskManager1, taskManager2, "Возвращаем новый экземляр");
    }
}
