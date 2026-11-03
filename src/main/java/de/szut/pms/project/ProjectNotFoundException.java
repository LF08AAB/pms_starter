package de.szut.pms.project;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(Long projectId) {
        super("Projekt mit der ID " + projectId + " wurde nicht gefunden.");
    }
}