package de.szut.pms.employee;

import java.util.Optional;

public interface EmployeeClient {

    Optional<EmployeeDetails> findById(Long employeeId);
}
