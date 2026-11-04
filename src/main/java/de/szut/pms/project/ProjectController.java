package de.szut.pms.project;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @ApiResponse(
            responseCode = "201",
            description = "Projekt erfolgreich erstellt",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProjectDto.class)
            )
    )

    @ApiResponse(
            responseCode = "400",
            description = "Ungültige Projektdaten",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Nicht authentifiziert",
            content = @Content
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
            summary = "Projekt anhand der ID abrufen",
            description = "Gibt ein einzelnes Projekt anhand seiner Projekt-ID zurück."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Projekt erfolgreich gefunden",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProjectDto.class)
            )
    )

    @ApiResponse(
            responseCode = "401",
            description = "Nicht authentifiziert",
            content = @Content
    )
    @ApiResponse(
            responseCode = "404",
            description = "Projekt nicht gefunden",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> getById(@PathVariable Long projectId) {
        ProjectDto project = service.getById(projectId);
        return ResponseEntity.ok(project);
    }

    @Operation(
            summary = "Projekt aktualisieren",
            description = "Aktualisiert ein bestehendes Projekt anhand seiner Projekt-ID."
    )

  @ApiResponse(
          responseCode = "200",
          description = "Projekt erfolgreich aktualisiert",
          content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ProjectDto.class)
          )
  )
  @ApiResponse(
          responseCode = "400",
          description = "Ungültige Projektdaten",
          content = @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = ProblemDetail.class)
          )
  )
  @ApiResponse(
          responseCode = "401",
          description = "Nicht authentifiziert",
          content = @Content
  )
  @ApiResponse(
          responseCode = "404",
          description = "Projekt nicht gefunden",
          content = @Content(
                  mediaType = "application/problem+json",
                  schema = @Schema(implementation = ProblemDetail.class)
          )
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

    @ApiResponse(
            responseCode = "200",
            description = "Projektliste erfolgreich abgerufen",
            content = @Content(
                    mediaType = "application/json",
                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(
                            schema = @Schema(implementation = ProjectDto.class)
                    )
            )
    )

    @ApiResponse(
            responseCode = "401",
            description = "Nicht authentifiziert",
            content = @Content
    )

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        List<ProjectDto> projects = service.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @Operation(
            summary = "Projekte eines Mitarbeiters abrufen",
            description = "Prueft den Mitarbeiter im Employee-Service und liefert alle zugeordneten Projekte, "
                    + "einschliesslich beendeter Projekte. Geloeschte Projekte sind ausgeschlossen. "
                    + "Die Antwort enthaelt Mitarbeiter-ID, Projektdaten, Einsatzqualifikation und Projektleiter-ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Projektliste; ohne Zuordnungen ist projects leer",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeProjectsDto.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Authentifizierung fehlt oder Employee-Service lehnt Token ab"),
            @ApiResponse(responseCode = "404", description = "Mitarbeiter existiert nicht im Employee-Service"),
            @ApiResponse(responseCode = "503", description = "Employee-Service ist nicht verfuegbar")
    })
    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<EmployeeProjectsDto> getEmployeeProjects(@PathVariable Long employeeId) {
        return ResponseEntity.ok(service.getEmployeeProjects(employeeId));
    }

    @Operation(
            summary = "Mitarbeiter eines Projekts abrufen",
            description = "Gibt die Mitarbeiterzuordnungen und den Abdeckungsstatus benoetigter Projektqualifikationen zurueck."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projektmitarbeiter und Qualifikationsabdeckung"),
            @ApiResponse(responseCode = "401", description = "Authentifizierung fehlt"),
            @ApiResponse(responseCode = "404", description = "Projekt oder zugeordneter Mitarbeiter nicht gefunden"),
            @ApiResponse(responseCode = "503", description = "Employee-Service ist nicht verfuegbar")
    })
    @GetMapping("/{projectId}/employees")
    public ResponseEntity<ProjectEmployeesDto> getProjectEmployees(@PathVariable Long projectId) {
        return ResponseEntity.ok(service.getProjectEmployees(projectId));
    }

    @Operation(
            summary = "Mitarbeiter einem Projekt zuordnen",
            description = "Ordnet einen Mitarbeiter einem Projekt zu und antwortet mit HTTP 201 und der URI der Zuordnung."
    )

    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Mitarbeiter erfolgreich zugeordnet",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Ungültige Eingabedaten"),
            @ApiResponse(responseCode = "401", description = "Nicht authentifiziert"),
            @ApiResponse(responseCode = "404", description = "Projekt oder Mitarbeiter nicht gefunden"),
            @ApiResponse(responseCode = "409", description = "Mitarbeiter bereits zugeordnet oder zeitlicher Konflikt"),
            @ApiResponse(responseCode = "422", description = "Qualifikation nicht vorhanden"),
            @ApiResponse(responseCode = "503", description = "Employee-Service nicht verfügbar")
    })

    @PostMapping("/{projectId}/employees/{employeeId}")
    public ResponseEntity<ProjectDto> assignEmployee(
            @PathVariable Long projectId,
            @PathVariable Long employeeId,
            @RequestBody @Valid AssignEmployeeDto dto) {
        ProjectDto updated = service.assignEmployee(projectId, employeeId, dto.qualification());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .build()
                .toUri();
        return ResponseEntity.created(location).body(updated);
    }

    @Operation(
            summary = "Mitarbeiter aus einem Projekt entfernen",
            description = "Entfernt die Mitarbeiterzuordnung anhand der Projekt- und Mitarbeiter-ID."
    )


    @ApiResponse(
            responseCode = "200",
            description = "Mitarbeiter erfolgreich entfernt",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProjectDto.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Nicht authentifiziert",
            content = @Content
    )
    @ApiResponse(
            responseCode = "404",
            description = "Projekt oder Mitarbeiterzuordnung nicht gefunden",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )

    @DeleteMapping("/{projectId}/employees/{employeeId}")
    public ResponseEntity<ProjectDto> removeEmployee(
            @PathVariable Long projectId,
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(service.removeEmployee(projectId, employeeId));
    }


    @Operation(
            summary = "Projekt löschen",
            description = "Löscht ein bestehendes Projekt anhand seiner Projekt-ID."
    )

    @ApiResponse(
            responseCode = "204",
            description = "Projekt erfolgreich gelöscht",
            content = @Content
    )

    @ApiResponse(
            responseCode = "401",
            description = "Nicht authentifiziert",
            content = @Content
    )
    @ApiResponse(
            responseCode = "404",
            description = "Projekt nicht gefunden",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        service.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
