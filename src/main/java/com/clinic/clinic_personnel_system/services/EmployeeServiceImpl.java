package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.models.Department;
import com.clinic.clinic_personnel_system.models.Employee;
import com.clinic.clinic_personnel_system.models.EmploymentHistory;
import com.clinic.clinic_personnel_system.models.Position;
import com.clinic.clinic_personnel_system.repositories.EmployeeRepository;
import com.clinic.clinic_personnel_system.repositories.EmploymentHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
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
            log.warn("Попытка сохранить сотрудника с несуществующим отделением ID={}", employee.getDepartment().getId());
            throw new RuntimeException("Указанного отделения не существует");
        }
        if (employee.getPosition() != null && positionService.getPositionById(employee.getPosition().getId()) == null) {
            log.warn("Попытка сохранить сотрудника с несуществующей должностью ID={}", employee.getPosition().getId());
            throw new RuntimeException("Указанной должности не существует");
        }

        Employee saved = employeeRepository.save(employee);

        EmploymentHistory newRecord = new EmploymentHistory();
        newRecord.setEmployee(saved);
        newRecord.setDepartment(saved.getDepartment());
        newRecord.setPosition(saved.getPosition());
        newRecord.setStartDate(saved.getEmploymentDate() != null ? saved.getEmploymentDate() : LocalDate.now());
        employmentHistoryRepository.save(newRecord);

        log.info("Создан сотрудник: {} (ID={})", saved.getFullName(), saved.getId());
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

        log.info("Обновлены данные сотрудника ID={}", id);
        return employeeRepository.save(existing);
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
        log.warn("Удалён сотрудник ID={}", id);
    }

    @Override
    public Employee transferEmployee(Long id, Long departmentId, Long positionId) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        Department department = departmentService.findById(departmentId);
        Position position = positionService.getPositionById(positionId);

        // Завершить последнюю активную запись
        EmploymentHistory current = employmentHistoryRepository
                .findFirstByEmployeeIdAndEndDateIsNullOrderByStartDateDesc(id);

        if (current != null) {
            current.setEndDate(LocalDate.now());
            employmentHistoryRepository.save(current);
        }

        // Создать новую запись
        EmploymentHistory newRecord = new EmploymentHistory();
        newRecord.setEmployee(employee);
        newRecord.setDepartment(department);
        newRecord.setPosition(position);
        newRecord.setStartDate(LocalDate.now());
        employmentHistoryRepository.save(newRecord);

        // Обновить основные данные сотрудника
        employee.setDepartment(department);
        employee.setPosition(position);

        log.info("Сотрудник ID={} переведён в отделение ID={}, на должность ID={}", id, departmentId, positionId);
        return employeeRepository.save(employee);
    }


    @Override
    public Employee dismissEmployee(Long id, LocalDate dismissalDate) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        // Обновляем статус сотрудника
        employee.setDismissalDate(dismissalDate);
        employee.setActive(false);

        // Завершаем предыдущую активную запись
        EmploymentHistory current = employmentHistoryRepository
                .findFirstByEmployeeIdAndEndDateIsNullOrderByStartDateDesc(id);

        if (current != null) {
            current.setEndDate(dismissalDate != null ? dismissalDate : LocalDate.now());
            employmentHistoryRepository.save(current);
        }

        // Создаём новую запись об увольнении
        EmploymentHistory dismissalRecord = new EmploymentHistory();
        dismissalRecord.setEmployee(employee);
        dismissalRecord.setDepartment(employee.getDepartment());
        dismissalRecord.setPosition(employee.getPosition());
        dismissalRecord.setStartDate(dismissalDate != null ? dismissalDate : LocalDate.now());
        dismissalRecord.setEndDate(dismissalDate != null ? dismissalDate : LocalDate.now());

        employmentHistoryRepository.save(dismissalRecord);

        log.info("Сотрудник ID={} уволен с датой {}", id, dismissalDate);
        return employeeRepository.save(employee);
    }



    @Override
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

}
