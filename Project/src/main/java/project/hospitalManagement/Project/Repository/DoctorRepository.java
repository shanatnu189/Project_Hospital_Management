package project.hospitalManagement.Project.Repository;

import project.hospitalManagement.Project.Entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
