package models;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    public void epicDurationAndStartEndTimeShouldBeCalculatedFromSubtasks() {
        Epic epic = new Epic(1, "Epic", "Epic Desc");
        assertEquals(Duration.ZERO, epic.getDuration());
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());

        LocalDateTime t1 = LocalDateTime.of(2024, 6, 21, 9, 0);
        LocalDateTime t2 = LocalDateTime.of(2024, 6, 21, 13, 0);
        Subtask sub1 = new Subtask(2, "S1", "S1 Desc", TaskStatus.DONE, Duration.ofMinutes(60), t1, 1);
        Subtask sub2 = new Subtask(3, "S2", "S2 Desc", TaskStatus.DONE, Duration.ofMinutes(90), t2, 1);

        epic.addSubtask(sub1);
        epic.addSubtask(sub2);
        epic.updateStatusAndTime();

        assertEquals(Duration.ofMinutes(150), epic.getDuration());
        assertEquals(t1, epic.getStartTime());
        assertEquals(t2.plusMinutes(90), epic.getEndTime());
    }
}
