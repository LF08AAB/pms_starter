package de.szut.pms.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Zuordnung eines Mitarbeiters zu einem Projekt, inklusive der Qualifikation,
 * mit der er dort eingesetzt ist. Der Mitarbeiter selbst lebt im
 * Employee-Service – hier speichern wir nur seine Id.
 */
@Entity
@Table(name = "project_employee", uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "employee_id"}))
@Getter
@Setter
@NoArgsConstructor
public class ProjectEmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(nullable = false)
    private String qualification;

    public ProjectEmployeeEntity(Long employeeId, String qualification) {
        this.employeeId = employeeId;
        this.qualification = qualification;
    }
}
