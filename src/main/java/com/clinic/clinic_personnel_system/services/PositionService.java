package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.dto.PositionDTO;
import com.clinic.clinic_personnel_system.models.Position;
import com.clinic.clinic_personnel_system.repositories.PositionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository repo;

    public List<Position> getAllPositions() {
        return repo.findAll();
    }

    public Position getPositionById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Должность не найдена с ID: " + id));
    }

    public Position savePosition(Position position) {
        return repo.save(position);
    }

    @Transactional
    public Position updatePosition(Long id, PositionDTO dto) {
        Position existing = getPositionById(id);
        existing.setTitle(dto.getTitle());
        return existing; 
    }

    public void deletePosition(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Невозможно удалить: должность не найдена с ID: " + id);
        }
        repo.deleteById(id);
    }
}
