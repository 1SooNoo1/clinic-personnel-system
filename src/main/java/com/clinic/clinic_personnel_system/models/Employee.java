package com.clinic.clinic_personnel_system.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @Column(name = "employment_date")
    private LocalDate employmentDate;

    @Column(name = "dismissal_date")
    private LocalDate dismissalDate;

    private Boolean active = true;

    // Геттеры и сеттеры (или используй Lombok: @Getter @Setter)
    public Long getId() { return id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public LocalDate getEmploymentDate() { return employmentDate; }
    public void setEmploymentDate(LocalDate employmentDate) { this.employmentDate = employmentDate; }

    public LocalDate getDismissalDate() { return dismissalDate; }
    public void setDismissalDate(LocalDate dismissalDate) { this.dismissalDate = dismissalDate; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    
}
