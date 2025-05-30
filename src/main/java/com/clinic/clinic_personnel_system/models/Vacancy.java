package com.clinic.clinic_personnel_system.models;

import jakarta.persistence.*;

@Entity
@Table(name = "vacancies")
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Department department;

    @ManyToOne
    private Position position;

    @Column(length = 1000)
    private String description;

    private Boolean isOpen = true;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsOpen() { return isOpen; }
    public void setIsOpen(Boolean open) { isOpen = open; }
}
