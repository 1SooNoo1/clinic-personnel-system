package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.models.Department;
import com.clinic.clinic_personnel_system.models.Employee;
import com.clinic.clinic_personnel_system.models.EmploymentHistory;
import com.clinic.clinic_personnel_system.models.Position;
import com.clinic.clinic_personnel_system.repositories.EmployeeRepository;
import com.clinic.clinic_personnel_system.repositories.EmploymentHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final PositionService positionService;
    private final EmploymentHistoryRepository employmentHistoryRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               DepartmentService departmentService,
                               PositionService positionService,
                               EmploymentHistoryRepository employmentHistoryRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
        this.positionService = positionService;
        this.employmentHistoryRepository = employmentHistoryRepository;
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
        if (employee.getPosition() != null && positionService.getPositionById(employee.getPosition().getId()) == null) {
            throw new RuntimeException("Указанной должности не существует");
        }

        // Новый сотрудник — создаём историю
        Employee saved = employeeRepository.save(employee);

        EmploymentHistory newRecord = new EmploymentHistory();
        newRecord.setEmployee(saved);
        newRecord.setDepartment(saved.getDepartment());
        newRecord.setPosition(saved.getPosition());
        newRecord.setStartDate(saved.getEmploymentDate() != null ? saved.getEmploymentDate() : LocalDate.now());

        employmentHistoryRepository.save(newRecord);

        return saved;
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
            Position position = positionService.getPositionById(employee.getPosition().getId());
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
        Position position = positionService.getPositionById(positionId);

        // Закрываем текущую историю
        List<EmploymentHistory> history = employmentHistoryRepository.findByEmployeeIdOrderByStartDateDesc(id);
        if (!history.isEmpty()) {
            EmploymentHistory current = history.get(0);
            if (current.getEndDate() == null) {
                current.setEndDate(LocalDate.now());
                employmentHistoryRepository.save(current);
            }
        }

        // Создаём новую запись истории
        EmploymentHistory newRecord = new EmploymentHistory();
        newRecord.setEmployee(employee);
        newRecord.setDepartment(department);
        newRecord.setPosition(position);
        newRecord.setStartDate(LocalDate.now());
        employmentHistoryRepository.save(newRecord);

        // Обновляем активные поля сотрудника
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

        // Завершаем текущую историю
        List<EmploymentHistory> history = employmentHistoryRepository.findByEmployeeIdOrderByStartDateDesc(id);
        if (!history.isEmpty()) {
            EmploymentHistory current = history.get(0);
            if (current.getEndDate() == null) {
                current.setEndDate(dismissalDate != null ? dismissalDate : LocalDate.now());
                employmentHistoryRepository.save(current);
            }
        }

        return employeeRepository.save(employee);
    }
}
