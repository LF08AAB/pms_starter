package de.szut.pms.project;

/** Wird geworfen, wenn es zu einer Projekt-Id kein Projekt gibt → HTTP 404. */
public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(Long id) {
        super("Es gibt kein Projekt mit der Id " + id + ".");
    }
}
