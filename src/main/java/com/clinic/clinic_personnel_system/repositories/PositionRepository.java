package com.clinic.clinic_personnel_system.repositories;

import com.clinic.clinic_personnel_system.models.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
