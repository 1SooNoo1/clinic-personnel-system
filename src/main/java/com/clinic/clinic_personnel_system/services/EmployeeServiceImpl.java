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
        if (employee.getDepartment() != null && departmentService.findById(employee.getDepartment().getId()) == null) {
            throw new RuntimeException("Указанного отделения не существует");
        }
        if (employee.getPosition() != null && positionService.findById(employee.getPosition().getId()) == null) {
            throw new RuntimeException("Указанной должности не существует");
        }
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Long id, Employee employee) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        existing.setFullName(employee.getFullName());
        existing.setEmail(employee.getEmail());
        existing.setPhone(employee.getPhone());
        existing.setBirthDate(employee.getBirthDate());
        existing.setEmploymentDate(employee.getEmploymentDate());
        existing.setDismissalDate(employee.getDismissalDate());
        existing.setActive(employee.getActive());

        if (employee.getDepartment() != null) {
            Department department = departmentService.findById(employee.getDepartment().getId());
            existing.setDepartment(department);
        }

        if (employee.getPosition() != null) {
            Position position = positionService.findById(employee.getPosition().getId());
            existing.setPosition(position);
        }

        return employeeRepository.save(existing);
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

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

    @Override
    public Employee dismissEmployee(Long id, LocalDate dismissalDate) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        employee.setDismissalDate(dismissalDate);
        employee.setActive(false);

        return employeeRepository.save(employee);
    }
}