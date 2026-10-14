package de.szut.pms.role;

import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleDto toDto(RoleEntity entity) {
        if (entity == null) {
            return null;
        }

        return new RoleDto(
                entity.getId(),
                entity.getName()
        );
    }

    public RoleEntity toEntity(CreateRoleDto dto) {
        if (dto == null) {
            return null;
        }

        return new RoleEntity(dto.name());
    }
}
