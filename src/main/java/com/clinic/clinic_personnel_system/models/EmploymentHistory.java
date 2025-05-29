package com.clinic.clinic_personnel_system.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "employment_history")
public class EmploymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee employee;

    @ManyToOne
    private Department department;

    @ManyToOne
    private Position position;

    private LocalDate startDate;
    private LocalDate endDate;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
