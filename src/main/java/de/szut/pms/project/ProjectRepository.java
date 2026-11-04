package de.szut.pms.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    @Query("""
            select case when count(project) > 0 then true else false end
            from ProjectEntity project
            join project.employees assigned
            where assigned.employeeId = :employeeId
              and project.projectId <> :projectId
              and project.startDate <= :endDate
              and project.endDate >= :startDate
            """)
    boolean existsOverlappingAssignment(
            @Param("projectId") Long projectId,
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
