package com.clinic.clinic_personnel_system.controllers;

import com.clinic.clinic_personnel_system.dto.EmploymentHistoryDTO;
import com.clinic.clinic_personnel_system.mapper.EmploymentHistoryMapper;
import com.clinic.clinic_personnel_system.models.EmploymentHistory;
import com.clinic.clinic_personnel_system.models.User;
import com.clinic.clinic_personnel_system.services.EmployeeService;
import com.clinic.clinic_personnel_system.repositories.EmploymentHistoryRepository;
import com.clinic.clinic_personnel_system.repositories.UserRepository;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmploymentHistoryController {

    private final EmploymentHistoryRepository historyRepository;
    private final EmploymentHistoryMapper historyMapper;
    private final EmployeeService employeeService;
    private final UserRepository userRepository;

    @Autowired
    public EmploymentHistoryController(
            EmploymentHistoryRepository historyRepository,
            EmploymentHistoryMapper historyMapper,
            EmployeeService employeeService,
            UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.historyMapper = historyMapper;
        this.employeeService = employeeService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<EmploymentHistoryDTO>> getEmploymentHistory(@PathVariable Long id) {
        List<EmploymentHistoryDTO> history = historyRepository.findByEmployeeIdOrderByStartDate(id)
                .stream()
                .map(historyMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{id}/workbook/pdf")
    public ResponseEntity<ByteArrayResource> exportEmploymentHistoryToPdf(@PathVariable Long id) {
        var employee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        List<EmploymentHistory> historyList = historyRepository.findByEmployeeIdOrderByStartDate(id);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        try {
            PdfFont font = PdfFontFactory.createFont("src/main/resources/fonts/Roboto-VariableFont_wdth,wght.ttf", PdfEncodings.IDENTITY_H);
            document.setFont(font);
        } catch (IOException e) {
            e.printStackTrace();
        }

        document.add(new Paragraph("Трудовая книжка сотрудника"));
        document.add(new Paragraph("Место работы: 'ЫЫЫ' Клиника имени Крюковка"));
        document.add(new Paragraph("Работодатель: Крюков Алексей Игоревич"));
        document.add(new Paragraph("ФИО: " + employee.getFullName()));
        document.add(new Paragraph("Email: " + employee.getEmail()));
        document.add(new Paragraph("Телефон: " + employee.getPhone()));
        document.add(new Paragraph("Дата рождения: " + employee.getBirthDate()));
        document.add(new Paragraph(" "));

        float[] columnWidths = {30F, 100F, 100F, 150F, 150F, 100F};
        Table table = new Table(columnWidths);
        table.addHeaderCell("№");
        table.addHeaderCell("Дата");
        table.addHeaderCell("Сведения");
        table.addHeaderCell("Должность");
        table.addHeaderCell("Отделение");
        table.addHeaderCell("Документ");

        int i = 1;
        for (int j = 0; j < historyList.size(); j++) {
            EmploymentHistory eh = historyList.get(j);
            String info;

            if (j == 0) {
                info = "Принят";
            } else if (
                j == historyList.size() - 1 &&
                employee.getDismissalDate() != null &&
                eh.getEndDate() != null &&
                eh.getEndDate().equals(employee.getDismissalDate())
            ) {
                info = "Уволен";
            } else {
                info = "Переведен";
            }

            table.addCell(String.valueOf(i++));
            table.addCell(eh.getStartDate().toString());
            table.addCell(info);
            table.addCell(eh.getPosition().getTitle());
            table.addCell(eh.getDepartment().getName());
            table.addCell("Приказ");
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



    @PutMapping("/my/workbook/pdf")
    public ResponseEntity<ByteArrayResource> exportMyEmploymentHistoryToPdf(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByPhone(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (user.getEmployee() == null) {
            throw new RuntimeException("Вы не являетесь сотрудником");
        }

        Long employeeId = user.getEmployee().getId();
        return exportEmploymentHistoryToPdf(employeeId); // вызвать существующий метод
    }

}
