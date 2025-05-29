package com.clinic.clinic_personnel_system.controllers;

import com.clinic.clinic_personnel_system.dto.EmploymentHistoryDTO;
import com.clinic.clinic_personnel_system.mapper.EmploymentHistoryMapper;
import com.clinic.clinic_personnel_system.models.EmploymentHistory;
import com.clinic.clinic_personnel_system.services.EmployeeService;
import com.clinic.clinic_personnel_system.repositories.EmploymentHistoryRepository;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmploymentHistoryController {

    private final EmploymentHistoryRepository historyRepository;
    private final EmploymentHistoryMapper historyMapper;
    private final EmployeeService employeeService;

    @Autowired
    public EmploymentHistoryController(
            EmploymentHistoryRepository historyRepository,
            EmploymentHistoryMapper historyMapper,
            EmployeeService employeeService) {
        this.historyRepository = historyRepository;
        this.historyMapper = historyMapper;
        this.employeeService = employeeService;
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<EmploymentHistoryDTO>> getEmploymentHistory(@PathVariable Long id) {
        List<EmploymentHistoryDTO> history = historyRepository.findByEmployeeIdOrderByStartDateDesc(id)
                .stream()
                .map(historyMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{id}/workbook/pdf")
    public ResponseEntity<ByteArrayResource> exportEmploymentHistoryToPdf(@PathVariable Long id) {
        var employee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        List<EmploymentHistory> historyList = historyRepository.findByEmployeeIdOrderByStartDateDesc(id);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Трудовая книжка сотрудника"));
        document.add(new Paragraph("Имя: " + employee.getFullName()));
        document.add(new Paragraph("Email: " + employee.getEmail()));
        document.add(new Paragraph("Телефон: " + employee.getPhone()));
        document.add(new Paragraph("Дата рождения: " + employee.getBirthDate()));
        document.add(new Paragraph(" "));

        float[] columnWidths = {200F, 200F, 150F, 150F};
        Table table = new Table(columnWidths);
        table.addCell("Отделение");
        table.addCell("Должность");
        table.addCell("Начало работы");
        table.addCell("Окончание");

        for (EmploymentHistory eh : historyList) {
            table.addCell(eh.getDepartment().getName());
            table.addCell(eh.getPosition().getTitle());
            table.addCell(eh.getStartDate().toString());
            table.addCell(eh.getEndDate() != null ? eh.getEndDate().toString() : "По настоящее время");
        }

        document.add(table);
        document.close();

        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=workbook_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(resource.contentLength())
                .body(resource);
    }
}
