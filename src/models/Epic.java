package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW, Duration.ZERO, null);
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        updateStatusAndTime();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateStatusAndTime();
    }

    public void updateStatusAndTime() {
        if (subtasks.isEmpty()) {
            this.status = TaskStatus.NEW;
        } else {
            boolean allNew = true;
            boolean allDone = true;
            for (Subtask st : subtasks) {
                if (st.getStatus() != TaskStatus.NEW) allNew = false;
                if (st.getStatus() != TaskStatus.DONE) allDone = false;
            }
            if (allNew) this.status = TaskStatus.NEW;
            else if (allDone) this.status = TaskStatus.DONE;
            else this.status = TaskStatus.IN_PROGRESS;
        }

        // --- Время и длительность
        Duration total = Duration.ZERO;
        LocalDateTime minStart = null;
        LocalDateTime maxEnd = null;
        for (Subtask st : subtasks) {
            if (st.getDuration() != null) total = total.plus(st.getDuration());
            if (st.getStartTime() != null) {
                if (minStart == null || st.getStartTime().isBefore(minStart)) minStart = st.getStartTime();
                LocalDateTime stEnd = st.getEndTime();
                if (stEnd != null && (maxEnd == null || stEnd.isAfter(maxEnd))) maxEnd = stEnd;
            }
        }
        this.duration = total;
        this.startTime = minStart;
        this.endTime = maxEnd;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", subtasks=" + subtasks +
                '}';
    }
}
