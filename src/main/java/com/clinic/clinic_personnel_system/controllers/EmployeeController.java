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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить сотрудника по ID")
    public ResponseEntity<Employee> getEmployeeById(
            @Parameter(description = "ID сотрудника") @PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден")));
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
            Position position = positionService.findById(dto.getPositionId());
            employee.setPosition(position);
        }

        Employee saved = employeeService.saveEmployee(employee);
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
            existing.setPosition(positionService.findById(dto.getPositionId()));
        }

        return ResponseEntity.ok(employeeRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить сотрудника по ID")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // ➤ Перевод сотрудника
    @PutMapping("/{id}/transfer")
    public ResponseEntity<Employee> transferEmployee(
            @PathVariable Long id,
            @RequestParam Long departmentId,
            @RequestParam Long positionId) {
        Employee updated = employeeService.transferEmployee(id, departmentId, positionId);
        return ResponseEntity.ok(updated);
    }

    // ➤ Увольнение сотрудника
    @PutMapping("/{id}/dismiss")
    public ResponseEntity<Employee> dismissEmployee(
            @PathVariable Long id,
            @RequestParam LocalDate dismissalDate) {
        Employee updated = employeeService.dismissEmployee(id, dismissalDate);
        return ResponseEntity.ok(updated);
    }

    // ➤ Экспорт трудовой книжки
    @GetMapping("/{id}/labor-book")
    public ResponseEntity<ByteArrayResource> exportLaborBook(@PathVariable Long id) throws Exception {
        Employee employee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        byte[] pdfBytes = generateLaborBookPdf(employee);
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=labor_book_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(resource);
    }

    private byte[] generateLaborBookPdf(Employee employee) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(output);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Трудовая книжка").setBold().setFontSize(18));
        document.add(new Paragraph("ФИО: " + employee.getFullName()));
        document.add(new Paragraph("Email: " + employee.getEmail()));
        document.add(new Paragraph("Телефон: " + employee.getPhone()));
        document.add(new Paragraph("Дата рождения: " + employee.getBirthDate()));
        document.add(new Paragraph("Дата устройства: " + employee.getEmploymentDate()));

        if (employee.getDismissalDate() != null) {
            document.add(new Paragraph("Дата увольнения: " + employee.getDismissalDate()));
        } else {
            document.add(new Paragraph("Статус: действующий"));
        }

        document.close();
        return output.toByteArray();
    }
}