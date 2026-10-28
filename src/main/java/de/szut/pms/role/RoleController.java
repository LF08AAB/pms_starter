package de.szut.pms.role;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleDto> create(
            @RequestBody @Valid CreateRoleDto dto) {

        RoleDto createdRole = roleService.create(dto.name());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdRole);
    }
    @GetMapping
    public ResponseEntity<List<RoleDto>> findAll() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.findById(id));
    }
    @GetMapping("/{name}")
    public ResponseEntity<RoleDto> findByName(@PathVariable String name) {
        return ResponseEntity.ok(roleService.findByName(name));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);

        return ResponseEntity.noContent().build();
    }

    public record CreateRoleRequest(String name) {}
}
