package com.example.projekt_ti.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tytuł zadania jest wymagany")
    private String title;

    private boolean completed;

    private int priority;

    private LocalDateTime createdAt;


    @NotNull(message = "Deadline zadania jest wymagany")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime deadline;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

}