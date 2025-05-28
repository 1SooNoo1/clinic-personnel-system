package com.clinic.clinic_personnel_system.controllers;

import com.clinic.clinic_personnel_system.dto.EmployeeDTO;
import com.clinic.clinic_personnel_system.models.Department;
import com.clinic.clinic_personnel_system.models.Employee;
import com.clinic.clinic_personnel_system.models.Position;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    @Autowired
    public EmployeeController(EmployeeService employeeService,
                              DepartmentService departmentService,
                              PositionService positionService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.positionService = positionService;
    }

    // Получить всех сотрудников
    @GetMapping
    @Operation(summary = "Получить всех сотрудников")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // Получить сотрудника по ID
    @GetMapping("/{id}")
    @Operation(summary = "Получить сотрудника по ID")
    @ApiResponse(responseCode = "200", description = "Сотрудник найден", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))
    })
    public ResponseEntity<Employee> getEmployeeById(
            @Parameter(description = "ID сотрудника") @PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден")));
    }

    // Добавить нового сотрудника
    @PostMapping
    @Operation(summary = "Добавить нового сотрудника")
    @ApiResponse(responseCode = "201", description = "Сотрудник успешно создан")
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setFullName(dto.getFullName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setBirthDate(dto.getBirthDate());
        employee.setEmploymentDate(dto.getEmploymentDate());
        employee.setDismissalDate(dto.getDismissalDate());
        employee.setActive(dto.getActive());

        if (dto.getDepartmentId() != null) {
            Department department = departmentService.findById(dto.getDepartmentId());
            employee.setDepartment(department);
        }

        if (dto.getPositionId() != null) {
            Position position = positionService.findById(dto.getPositionId());
            employee.setPosition(position);
        }

        Employee saved = employeeService.saveEmployee(employee);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // Обновить данные о сотруднике
    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные сотрудника")
    public ResponseEntity<Employee> updateEmployee(
            @Parameter(description = "ID сотрудника") @PathVariable Long id,
            @Valid @RequestBody EmployeeDTO dto) {
        Employee existing = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        existing.setFullName(dto.getFullName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setBirthDate(dto.getBirthDate());
        existing.setEmploymentDate(dto.getEmploymentDate());
        existing.setDismissalDate(dto.getDismissalDate());
        existing.setActive(dto.getActive());

        if (dto.getDepartmentId() != null) {
            Department department = departmentService.findById(dto.getDepartmentId());
            existing.setDepartment(department);
        }

        if (dto.getPositionId() != null) {
            Position position = positionService.findById(dto.getPositionId());
            existing.setPosition(position);
        }

        Employee updated = employeeService.saveEmployee(existing);
        return ResponseEntity.ok(updated);
    }

    // Удалить сотрудника
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить сотрудника по ID")
    public ResponseEntity<Void> deleteEmployee(
            @Parameter(description = "ID сотрудника") @PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // ➤ Перевод сотрудника на другое отделение и должность
    @PutMapping("/{id}/transfer")
    @Operation(summary = "Перевести сотрудника на другое отделение и должность")
    public ResponseEntity<Employee> transferEmployee(
            @Parameter(description = "ID сотрудника") @PathVariable Long id,
            @RequestParam Long departmentId,
            @RequestParam Long positionId) {
        Employee updated = employeeService.transferEmployee(id, departmentId, positionId);
        return ResponseEntity.ok(updated);
    }

    // ➤ Увольнение сотрудника
    @PutMapping("/{id}/dismiss")
    @Operation(summary = "Уволить сотрудника")
    public ResponseEntity<Employee> dismissEmployee(
            @Parameter(description = "ID сотрудника") @PathVariable Long id,
            @RequestParam LocalDate dismissalDate) {
        Employee updated = employeeService.dismissEmployee(id, dismissalDate);
        return ResponseEntity.ok(updated);
    }

    // ➤ Экспорт трудовой книжки в PDF
    @GetMapping("/{id}/labor-book")
    @Operation(summary = "Экспортировать трудовую книжку сотрудника в формате PDF")
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

    // ➤ Генерация PDF с информацией о сотруднике
    private byte[] generateLaborBookPdf(Employee employee) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(output);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Трудовая книжка сотрудника").setBold().setFontSize(18));
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