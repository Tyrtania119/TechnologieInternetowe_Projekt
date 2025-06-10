package com.example.projekt_ti.service;

import com.example.projekt_ti.model.Task;
import com.example.projekt_ti.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks(String sortBy) {
        List<Task> tasks = taskRepository.findAll();
        return switch (sortBy) {
            case "priority" -> tasks.stream().sorted(Comparator.comparing(Task::getPriority)).toList();
            case "created" -> tasks.stream().sorted(Comparator.comparing(Task::getCreatedAt)).toList();
            case "title" -> tasks.stream().sorted(Comparator.comparing(Task::getTitle)).toList();
            case "completed" -> tasks.stream().sorted(Comparator.comparing(Task::isCompleted)).toList();
            default -> tasks;
        };
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public void toggleCompletion(Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setCompleted(!task.isCompleted());
        taskRepository.save(task);
    }

    public Task getTask(Long id) {
        return taskRepository.findById(id).orElseThrow();
    }

    public long countAll() {
        return taskRepository.count();
    }

    public long countCompleted() {
        return taskRepository.findAll().stream().filter(Task::isCompleted).count();
    }

    public long countMissed() {
        return taskRepository.findAll().stream()
                .filter(task -> !task.isCompleted())
                .filter(task -> task.getDeadline() != null && task.getDeadline().isBefore(java.time.LocalDateTime.now()))
                .count();
    }
}