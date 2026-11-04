package de.szut.pms.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    @EntityGraph(attributePaths = "employees")
    @Query("""
            select distinct project from ProjectEntity project
            join project.employees assignment
            where assignment.employeeId = :employeeId
            order by project.projectId
            """)
    List<ProjectEntity> findProjectsByEmployeeId(@Param("employeeId") Long employeeId);
}
