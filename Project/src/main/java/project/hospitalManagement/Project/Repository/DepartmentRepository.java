package project.hospitalManagement.Project.Repository;

import project.hospitalManagement.Project.Entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
