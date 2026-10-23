# Technische Tasks & Testfälle (Arbeitsnotizen für die Umsetzung)

> Detaillierte Umsetzungsschritte zu den Stories in `user-stories.md`.


## US-01 Projekt anlegen

**Tasks**
- T_1: `hello`-Beispiel durch Package `project` ersetzen (Entity, DTOs, Mapper, Repository)
- T_2: `EmployeeClient` mit RestClient erstellen: prüft `GET /employees/{id}` inkl. Weitergabe des JWT
- T_3: `ProjectService.create` mit allen Prüfungen (Mitarbeiter, Kunde, Datum)
- T_4: `POST /projects` im Controller, Security-Config und Swagger anpassen
- T_5: eigene Exceptions + Handler im `ApiExceptionHandler`
- T_6: Integrationstests (Employee-Service im Test gemockt)

**Testfälle (Auszug)**
- TF_1: gültige Daten → 201, Projekt in DB
- TF_2: fehlende Bezeichnung → 400
- TF_3: Enddatum vor Startdatum → 400
- TF_4: unbekannter verantwortlicher Mitarbeiter → 404
- TF_5: ohne Token → 401


## US-02 Projekte einsehen

**Tasks**
- T_1: `GET /projects` und `GET /projects/{id}` implementieren
- T_2: `ProjectNotFoundException` + Handler
- T_3: Integrationstests


## US-03 Projektdaten ändern

**Tasks**
- T_1: `PUT /projects/{id}` + Service-Methode
- T_2: Prüfungen aus US-01 wiederverwenden
- T_3: Integrationstests


## US-04 Projekt löschen

**Tasks**
- T_1: `DELETE /projects/{id}` (Cascade für Zuordnungen)
- T_2: Integrationstests


## US-05 Mitarbeiter mit Qualifikation einem Projekt zuweisen

**Tasks**
- T_1: Entity für die Zuordnung (Projekt ↔ Mitarbeiter-Id + Qualifikation)
- T_2: `EmployeeClient` um Abfrage der Qualifikationen erweitern
- T_3: `POST /projects/{id}/employees` + Service-Methode
- T_4: Exceptions + Handler
- T_5: Integrationstests


## US-06 Doppelte Verplanung eines Mitarbeiters verhindern

**Tasks**
- T_1: Repository-Abfrage „Projekte eines Mitarbeiters im Zeitraum"
- T_2: Prüfung in die Zuweisung einbauen
- T_3: Integrationstests (überlappend, angrenzend, nicht überlappend)


## US-07 Mitarbeiter aus einem Projekt entfernen

**Tasks**
- T_1: `DELETE /projects/{id}/employees/{employeeId}` + Service
- T_2: Integrationstests


## US-08 Mitarbeiter eines Projekts abrufen

**Tasks**
- T_1: eigenes Antwort-DTO + `GET /projects/{id}/employees`
- T_2: Integrationstests


## US-09 Projekte eines Mitarbeiters abrufen

**Tasks**
- T_1: Repository-Abfrage + eigenes Antwort-DTO
- T_2: Endpunkt (Pfad im Team festlegen)
- T_3: Integrationstests
