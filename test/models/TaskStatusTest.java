package models;

import models.TaskStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskStatusTest {

    @Test
    public void shouldReturnCorrectValues() {
        assertEquals(TaskStatus.NEW, TaskStatus.valueOf("NEW"));
        assertEquals(TaskStatus.IN_PROGRESS, TaskStatus.valueOf("IN_PROGRESS"));
        assertEquals(TaskStatus.DONE, TaskStatus.valueOf("DONE"));
    }

    @Test
    public void shouldContainAllStatuses() {
        TaskStatus[] statuses = TaskStatus.values();
        assertEquals(3, statuses.length);
    }
}
