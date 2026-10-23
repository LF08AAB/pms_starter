package de.szut.pms.project;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Datenbankzugriff für Projekte. save, findAll, findById, deleteById, existsById …
 * erbt das Interface von JpaRepository – Spring erzeugt die Implementierung.
 */
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
}
