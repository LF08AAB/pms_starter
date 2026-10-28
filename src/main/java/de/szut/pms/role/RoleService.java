package de.szut.pms.role;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;


    public RoleDto create(String name) {
        if (roleRepository.existsByName(name)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Role already exists"
            );
        }

        RoleEntity role = roleRepository.save(
                new RoleEntity(name)
        );

        return roleMapper.toDto(role);
    }

    public List<RoleDto> findAll() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toDto)
                .toList();
    }

    public RoleDto findById(Long id) {
        RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role not found"
                ));

        return toDto(role);
    }

    public RoleDto findByName(String name) {
        RoleEntity role = roleRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role not found"
                ));

        return toDto(role);
    }

    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Role not found"
            );
        }

        roleRepository.deleteById(id);
    }

    private RoleDto toDto(RoleEntity role) {
        return new RoleDto(role.getId(), role.getName());
    }
}
