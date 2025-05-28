package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.models.Department;
import com.clinic.clinic_personnel_system.models.Employee;
import com.clinic.clinic_personnel_system.models.Position;
import com.clinic.clinic_personnel_system.repositories.EmployeeRepository;
import com.clinic.clinic_personnel_system.services.DepartmentService;
import com.clinic.clinic_personnel_system.services.EmployeeService;
import com.clinic.clinic_personnel_system.services.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final PositionService positionService;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               DepartmentService departmentService,
                               PositionService positionService) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
        this.positionService = positionService;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Long id, Employee employee) {
        Optional<Employee> existing = employeeRepository.findById(id);
        if (existing.isEmpty()) return null;

        Employee emp = existing.get();
        emp.setFullName(employee.getFullName());
        emp.setEmail(employee.getEmail());
        emp.setPhone(employee.getPhone());
        emp.setBirthDate(employee.getBirthDate());
        emp.setDepartment(employee.getDepartment());
        emp.setPosition(employee.getPosition());
        emp.setEmploymentDate(employee.getEmploymentDate());
        emp.setDismissalDate(employee.getDismissalDate());
        emp.setActive(employee.getActive());

        return employeeRepository.save(emp);
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    // ➤ Перевод сотрудника на другое отделение и должность
    @Override
    public Employee transferEmployee(Long id, Long departmentId, Long positionId) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        Department department = departmentService.findById(departmentId);
        Position position = positionService.findById(positionId);

        employee.setDepartment(department);
        employee.setPosition(position);

        return employeeRepository.save(employee);
    }

    // ➤ Увольнение: установка даты увольнения и отключение статуса
    @Override
    public Employee dismissEmployee(Long id, LocalDate dismissalDate) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        employee.setDismissalDate(dismissalDate);
        employee.setActive(false);

        return employeeRepository.save(employee);
    }
}