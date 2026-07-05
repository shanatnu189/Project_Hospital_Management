package project.hospitalManagement.Project.Repository;

import project.hospitalManagement.Project.Entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Find doctor by user ID
    Optional<Doctor> findByUserId(Long userId);

    // Find doctor by email
    Optional<Doctor> findByEmail(String email);

    // Check if doctor exists by email
    boolean existsByEmail(String email);
}
