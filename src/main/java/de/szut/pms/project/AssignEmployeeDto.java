package de.szut.pms.project;

import jakarta.validation.constraints.NotBlank;

public record AssignEmployeeDto(@NotBlank String qualification) {
}
