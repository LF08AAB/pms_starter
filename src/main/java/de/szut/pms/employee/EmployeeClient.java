package de.szut.pms.employee;

import java.util.Optional;

/** Zugriff auf den Employee-Service. */
public interface EmployeeClient {

    void verifyExists(Long employeeId);

    Optional<EmployeeDetails> findById(Long employeeId);
}
