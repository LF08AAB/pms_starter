package de.szut.pms.employee;

/** Zugriff auf den Employee-Service. */
public interface EmployeeClient {

    /**
     * Prüft, ob die Mitarbeiter-ID im Employee-Service existiert.
     *
     * @throws EmployeeNotFoundException     wenn der Mitarbeiter nicht existiert
     * @throws EmployeeServiceUnavailableException wenn der Service nicht erreichbar ist
     */
    void verifyExists(Long employeeId);
}
