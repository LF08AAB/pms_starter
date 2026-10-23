package de.szut.pms.project;

import jakarta.persistence.ElementCollection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long responsibleEmployeeId;

    @Column(nullable = false)
    private Long customerId;

    private String customerContactName;

    @Column(length = 1000)
    private String comment;
    // klären, ob benötigte Qualifikationen als Namen oder Skill-IDs gespeichert werden sollen
    @ElementCollection
    private Set<String> requiredQualifications = new HashSet<>();

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

<<<<<<< HEAD
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectEmployeeEntity> employees = new ArrayList<>();

    /** Fügt einen Mitarbeiter hinzu und hält beide Seiten der Beziehung synchron. */
    public void addEmployee(ProjectEmployeeEntity employee) {
        employees.add(employee);
        employee.setProject(this);
    }

    /** Entfernt einen Mitarbeiter; dank orphanRemoval wird die Zeile in der DB gelöscht. */
    public void removeEmployee(ProjectEmployeeEntity employee) {
        employees.remove(employee);
        employee.setProject(null);
    }
}
=======
    public ProjectEntity(String name, Long aLong, Long aLong1, String s, String comment, Set<String> strings, LocalDate localDate, LocalDate localDate1) {
    }
}
>>>>>>> feature-s01-t05
