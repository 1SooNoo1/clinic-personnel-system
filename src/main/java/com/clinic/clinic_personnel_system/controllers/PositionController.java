package com.clinic.clinic_personnel_system.controllers;

import com.clinic.clinic_personnel_system.dto.PositionDTO;
import com.clinic.clinic_personnel_system.mapper.PositionMapper;
import com.clinic.clinic_personnel_system.models.Position;
import com.clinic.clinic_personnel_system.services.PositionService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;
    private final PositionMapper positionMapper;

    @GetMapping
    @Operation(summary = "Получить список всех")
    public ResponseEntity<List<PositionDTO>> getAllPositions() {
        List<PositionDTO> positions = positionService.getAllPositions()
                .stream()
                .map(positionMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(positions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Найти должность по ID")
    public ResponseEntity<PositionDTO> getPositionById(@PathVariable Long id) {
        Position position = positionService.getPositionById(id);
        return ResponseEntity.ok(positionMapper.toDto(position));
    }

    @PostMapping
    @Operation(summary = "Создать новую должность")
    public ResponseEntity<PositionDTO> createPosition(@Valid @RequestBody PositionDTO positionDTO) {
        Position position = positionMapper.toEntity(positionDTO);
        Position saved = positionService.savePosition(position);
        return ResponseEntity.ok(positionMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные о должности")
    public ResponseEntity<PositionDTO> updatePosition(@PathVariable Long id, @Valid @RequestBody PositionDTO positionDTO) {
        Position updated = positionService.updatePosition(id, positionDTO);
        return ResponseEntity.ok(positionMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "обновить должность")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        positionService.deletePosition(id);
        return ResponseEntity.noContent().build();
    }
}