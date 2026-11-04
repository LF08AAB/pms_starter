package de.szut.pms.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    @EntityGraph(attributePaths = "employees")
    Optional<ProjectEntity> findProjectWithEmployeesByProjectId(Long projectId);

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
    @EntityGraph(attributePaths = "employees")
    @Query("""
            select distinct project from ProjectEntity project
            join project.employees assignment
            where assignment.employeeId = :employeeId
            order by project.projectId
            """)
    List<ProjectEntity> findProjectsByEmployeeId(@Param("employeeId") Long employeeId);
}
