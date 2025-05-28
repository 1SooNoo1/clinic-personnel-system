package com.clinic.clinic_personnel_system.controllers;

import com.clinic.clinic_personnel_system.dto.PositionDTO;
import com.clinic.clinic_personnel_system.models.Position;
import com.clinic.clinic_personnel_system.services.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@CrossOrigin(origins = "*")
@Tag(name = "Должности", description = "CRUD операции для должностей клиники")
public class PositionController {

    private final PositionService positionService;

    @Autowired
    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping
    @Operation(summary = "Получить все должности")
    public List<Position> getAllPositions() {
        return positionService.getAll();
    }

    @PostMapping
    @Operation(summary = "Добавить новую должность")
    @ApiResponse(responseCode = "201", description = "Должность успешно создана")
    public ResponseEntity<Position> createPosition(@Valid @RequestBody PositionDTO dto) {
        Position position = new Position();
        position.setTitle(dto.getTitle());
        Position saved = positionService.save(position);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить должность по ID")
    public ResponseEntity<Position> getPositionById(
            @Parameter(description = "ID должности") @PathVariable Long id) {
        return ResponseEntity.ok(positionService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные должности")
    public ResponseEntity<Position> updatePosition(
            @Parameter(description = "ID должности") @PathVariable Long id,
            @Valid @RequestBody PositionDTO dto) {
        Position existing = positionService.findById(id);
        existing.setTitle(dto.getTitle());
        Position updated = positionService.save(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить должность по ID")
    public ResponseEntity<Void> deletePosition(
            @Parameter(description = "ID должности") @PathVariable Long id) {
        positionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}