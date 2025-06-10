package com.example.projekt_ti.controller;

import com.example.projekt_ti.model.Task;
import com.example.projekt_ti.service.TaskService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public String index(Model model, @RequestParam(defaultValue = "created") String sort) {

        var tasks = taskService.getAllTasks(sort);
        model.addAttribute("tasks", taskService.getAllTasks(sort));

        long total = taskService.countAll();
        long completed = taskService.countCompleted();
        long missed = taskService.countMissed();

        double completedPercent = total > 0 ? (100.0 * completed) / total : 0;
        double overduePercent = total > 0 ? (100.0 * missed) / total : 0;

        model.addAttribute("completedPercent", completedPercent);
        model.addAttribute("overduePercent", overduePercent);

        return "index";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("task", new Task());
        return "form";
    }

    @PostMapping("/add")
    public String saveTask(@Valid @ModelAttribute Task task, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "form";
        }
        taskService.saveTask(task);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("task", taskService.getTask(id));
        return "form";
    }

    @PostMapping("/edit")
    public String updateTask(@Valid @ModelAttribute Task task, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "form";
        }
        taskService.saveTask(task);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/";
    }

    @GetMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id) {
        taskService.toggleCompletion(id);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/report")
    public void reportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=report.csv");

        List<Task> tasks = taskService.getAllTasks("created");
        long total = taskService.countAll();
        long completed = taskService.countCompleted();
        long missed = taskService.countMissed();

        double percentage = total > 0 ? (100.0 * completed) / total : 0;
        double percentage_missed = total > 0 ? (100.0 * missed) / total : 0;

        PrintWriter writer = response.getWriter();
        writer.println("Tytuł,Priorytet,Wykonane,Data utworzenia,Deadline");
        for (Task task : tasks) {
            writer.printf("\"%s\",%d,%s,%s,%s\n",
                    task.getTitle(),
                    task.getPriority(),
                    task.isCompleted(),
                    task.getCreatedAt(),
                    task.getDeadline());
        }
        writer.printf("\nProcent ukonczonych zadan: %.2f%%\n", percentage);
        writer.printf("\nProcent nieudanych zadan: %.2f%%\n", percentage_missed);
        writer.flush();
    }
}
