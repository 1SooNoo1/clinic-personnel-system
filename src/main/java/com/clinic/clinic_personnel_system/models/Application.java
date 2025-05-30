package com.clinic.clinic_personnel_system.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "vacancy_id"})
})
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Vacancy vacancy;

    private String message;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Vacancy getVacancy() { return vacancy; }
    public void setVacancy(Vacancy vacancy) { this.vacancy = vacancy; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
