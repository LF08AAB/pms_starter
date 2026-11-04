package de.szut.pms.project;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectEmployeeRepository extends JpaRepository<ProjectEmployeeEntity, Long> {

    boolean existsByProject_ProjectIdAndEmployeeId(Long projectId, Long employeeId);
}
