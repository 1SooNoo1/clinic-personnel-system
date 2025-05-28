package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.models.Position;
import com.clinic.clinic_personnel_system.repositories.PositionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionService {
    private final PositionRepository repo;

    public PositionService(PositionRepository repo) {
        this.repo = repo;
    }

    public List<Position> getAll() {
        return repo.findAll();
    }

    public Position save(Position p) {
        return repo.save(p);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Position findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Должность не найдена"));
    }
}