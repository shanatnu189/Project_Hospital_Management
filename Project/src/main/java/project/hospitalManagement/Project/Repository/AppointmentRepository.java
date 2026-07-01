package project.hospitalManagement.Project.Repository;

import project.hospitalManagement.Project.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
