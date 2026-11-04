package de.szut.pms.project;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.List;
import java.net.URI;

@Tag(
        name = "Projektverwaltung",
        description = "REST-Schnittstellen zur Verwaltung von Projekten"
)

@RestController
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService service;


    public ProjectController(ProjectService service) {this.service = service;}


    @Operation(
            summary = "Projekt erstellen",
            description = "Erstellt ein neues Projekt und gibt die gespeicherten Projektdaten zurück."
    )

    @PostMapping
    public ResponseEntity<ProjectDto> create(@RequestBody @Valid CreateProjectDto dto){
        ProjectDto created = service.create(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.projectId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }


    @Operation(
            summary = "Projekt aktualisieren",
            description = "Aktualisiert ein bestehendes Projekt anhand seiner Projekt-ID."
    )

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> update(@PathVariable Long id, @RequestBody @Valid PutProjectDto dto) {
        ProjectDto updated = service.update(id, dto);

        return ResponseEntity.ok(updated);
    }


    @Operation(
            summary = "Alle Projekte abrufen",
            description = "Gibt alle gespeicherten Projekte als JSON-Liste zurück."
    )

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        List<ProjectDto> projects = service.getAllProjects();
        return ResponseEntity.ok(projects);
    }


    @Operation(
            summary = "Mitarbeiter einem Projekt zuordnen",
            description = "Platzhalter für die Mitarbeiterzuordnung. Der Endpunkt ist noch nicht implementiert und antwortet mit HTTP 501."
    )

    @PostMapping("/{projectId}/employees/{employeeId}")
    public ResponseEntity<Void> assignEmployee(
            @PathVariable Long projectId,
            @PathVariable Long employeeId,
            @RequestBody AssignEmployeeDto dto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Operation(
            summary = "Projekt löschen",
            description = "Löscht ein bestehendes Projekt anhand seiner Projekt-ID."
    )

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        service.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
