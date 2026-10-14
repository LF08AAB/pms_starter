package de.szut.pms.project;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService service;


    public ProjectController(ProjectService service) {this.service = service;}

    @PostMapping
    public ResponseEntity<ProjectDto> create(@RequestBody @Valid CreateProjectDto dto){
        ProjectDto created = service.create(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.projectId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }
}
