package com.clinic.clinic_personnel_system.controllers;

import com.clinic.clinic_personnel_system.dto.EmployeeDTO;
import com.clinic.clinic_personnel_system.mapper.EmployeeMapper;
import com.clinic.clinic_personnel_system.models.Department;
import com.clinic.clinic_personnel_system.models.Employee;
import com.clinic.clinic_personnel_system.models.Position;
import com.clinic.clinic_personnel_system.repositories.EmployeeRepository;
import com.clinic.clinic_personnel_system.services.DepartmentService;
import com.clinic.clinic_personnel_system.services.EmployeeService;
import com.clinic.clinic_personnel_system.services.PositionService;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
@Tag(name = "Сотрудники", description = "CRUD операции для сотрудников клиники")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final PositionService positionService;
    private final EmployeeMapper employeeMapper;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeController(
            EmployeeService employeeService,
            DepartmentService departmentService,
            PositionService positionService,
            EmployeeRepository employeeRepository,
            EmployeeMapper employeeMapper) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.positionService = positionService;
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @GetMapping
    @Operation(summary = "Получить всех сотрудников")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        log.info("Получение списка всех сотрудников");
        List<EmployeeDTO> list = employeeService.getAllEmployees().stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить сотрудника по ID")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));
        log.info("Получен сотрудник с ID={}", id);
        return ResponseEntity.ok(employeeMapper.toDto(employee));
    }

    @PostMapping
    @Operation(summary = "Добавить нового сотрудника")
    @ApiResponse(responseCode = "201", description = "Сотрудник успешно создан")
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody EmployeeDTO dto) {
        Employee employee = employeeMapper.toEntity(dto);

        if (dto.getDepartmentId() != null) {
            Department department = departmentService.findById(dto.getDepartmentId());
            employee.setDepartment(department);
        }
        if (dto.getPositionId() != null) {
            Position position = positionService.getPositionById(dto.getPositionId());
            employee.setPosition(position);
        }

        Employee saved = employeeService.saveEmployee(employee);
        log.info("Создан новый сотрудник: {} (ID={})", saved.getFullName(), saved.getId());
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные сотрудника")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeDTO dto) {

        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        employeeMapper.updateFromDto(dto, existing);

        if (dto.getDepartmentId() != null) {
            existing.setDepartment(departmentService.findById(dto.getDepartmentId()));
        }

        if (dto.getPositionId() != null) {
            existing.setPosition(positionService.getPositionById(dto.getPositionId()));
        }

        Employee updated = employeeService.saveEmployee(existing);
        log.info("Обновлены данные сотрудника ID={}", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить сотрудника")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        log.warn("Сотрудник с ID={} удалён", id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/transfer")
    @Operation(summary = "Перевести сотрудника в другое отделение и/или должность")
    public ResponseEntity<Employee> transferEmployee(
            @PathVariable Long id,
            @RequestParam Long departmentId,
            @RequestParam Long positionId) {
        Employee transferred = employeeService.transferEmployee(id, departmentId, positionId);
        log.info("Сотрудник ID={} переведён в отделение ID={}, должность ID={}", id, departmentId, positionId);
        return ResponseEntity.ok(transferred);
    }

    @PutMapping("/{id}/dismiss")
    @Operation(summary = "Уволить сотрудника")
    public ResponseEntity<Employee> dismissEmployee(
            @PathVariable Long id,
            @RequestParam LocalDate dismissalDate) {
        Employee dismissed = employeeService.dismissEmployee(id, dismissalDate);
        log.info("Сотрудник ID={} уволен с датой {}", id, dismissalDate);
        return ResponseEntity.ok(dismissed);
    }

    @GetMapping("/export/pdf")
    @Operation(summary = "Экспорт списка сотрудников в PDF")
    public ResponseEntity<ByteArrayResource> exportToPdf() {
        List<Employee> employees = employeeService.getAllEmployees();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Список сотрудников клиники"));

        for (Employee emp : employees) {
            String line = String.format("ID: %d, Имя: %s, Email: %s, Отделение: %s, Должность: %s",
                    emp.getId(),
                    emp.getFullName(),
                    emp.getEmail(),
                    emp.getDepartment() != null ? emp.getDepartment().getName() : "N/A",
                    emp.getPosition() != null ? emp.getPosition().getTitle() : "N/A");
            document.add(new Paragraph(line));
        }

        document.close();

        log.info("Выполнен экспорт сотрудников в PDF ({} записей)", employees.size());

        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=employees.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(resource.contentLength())
                .body(resource);
    }
}
