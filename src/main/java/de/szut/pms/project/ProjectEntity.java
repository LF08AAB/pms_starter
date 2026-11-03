package de.szut.pms.project;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    private String name;

    private Long responsibleEmployeeId;

    private Long customerId;

    private String contactPerson;

    private String comment;
    // klären, ob benötigte Qualifikationen als Namen oder Skill-IDs gespeichert werden sollen
    @ElementCollection
    private Set<String> requiredQualifications = new HashSet<>();

    private LocalDate startDate;

    private LocalDate endDate;

    public ProjectEntity(String name, Long aLong, Long aLong1, String s, String comment, Set<String> strings, LocalDate localDate, LocalDate localDate1) {
    }
}