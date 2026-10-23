# User-Stories – Project-Management-Service (Entwurf für die Sprintplanung)

> Vorschläge zum Übertragen in sprint.heidelab.de. Punkte sind **Vorschläge** –
> die echte Schätzung macht das Team per Schätzpoker (Fibonacci: 1, 2, 3, 5, 8, 13).
> Referenz-Story für relative Schätzung: **US-04 Projekt löschen = 1 Punkt**.

## Sprint-Ziel (Vorschlag)

Am Ende des Sprints können angemeldete Mitarbeiter über eine dokumentierte REST-Schnittstelle
Projekte vollständig verwalten und ihnen Mitarbeiter mit passender Qualifikation zuweisen,
ohne dass ein Mitarbeiter doppelt verplant wird.

## Definition of Ready (Vorschlag)

- Story ist im Format „Als … möchte ich … um …" formuliert
- hat prüfbare Akzeptanzkriterien
- ist geschätzt und klein genug für den Sprint
- offene Fragen sind geklärt

## Definition of Done (Vorschlag)

- alle Akzeptanzkriterien erfüllt
- Integrationstests: Happy-Path + je ein Test pro Negativfall, alle grün (`./mvnw test`)
- Endpunkt ist per JWT abgesichert (ohne Token → 401)
- Endpunkt ist in Swagger dokumentiert
- Code ist committet, gepusht und von einem Teammitglied angesehen

---

## Übersicht (priorisiert)

| Prio | Story | Punkte (Vorschlag) |
|---|---|---|
| 1 | US-01 Projekt anlegen | 5 |
| 2 | US-02 Projekte einsehen (alle / eines) | 2 |
| 3 | US-05 Mitarbeiter mit Qualifikation einem Projekt zuweisen | 5 |
| 4 | US-06 Doppelte Verplanung eines Mitarbeiters verhindern | 3 |
| 5 | US-03 Projektdaten ändern | 3 |
| 6 | US-08 Mitarbeiter eines Projekts abrufen | 2 |
| 7 | US-07 Mitarbeiter aus einem Projekt entfernen | 2 |
| 8 | US-09 Projekte eines Mitarbeiters abrufen | 3 |
| 9 | US-04 Projekt löschen | 1 |

---

## US-01 Projekt anlegen

**Als** Mitarbeiter der HiTec GmbH **möchte ich** ein neues Kundenprojekt anlegen können,
**um** alle Projektdaten zentral an einer Stelle zu erfassen.

**Akzeptanzkriterien**
- A_1: Ein Projekt besteht aus Id, Bezeichnung, Id des verantwortlichen Mitarbeiters, Kunden-Id, Name des Ansprechpartners beim Kunden, Kommentar (Projektziel), Startdatum und Enddatum.
- A_2: Die Id wird vom System vergeben.
- A_3: Bezeichnung, verantwortlicher Mitarbeiter, Kunden-Id und Startdatum sind Pflichtfelder; fehlt eines, erhält der User 400 mit einer Fehlermeldung je Feld.
- A_4: Liegt das Enddatum vor dem Startdatum, erhält der User 400.
- A_5: Ist der verantwortliche Mitarbeiter im Employee-Service unbekannt, erhält der User 404 mit Fehlermeldung.
- A_6: Ist der Kunde unbekannt (Kunden-Service, vorerst Dummy), erhält der User 404.
- A_7: Bei Erfolg erhält der User 201, das angelegte Projekt als JSON und einen Location-Header.
- A_8: Ohne gültigen JWT erhält der User 401.

**Tasks**
- T_1: Datenmodell für Projekte erstellen
- T_2: Projekt anlegen umsetzen
- T_3: Prüfung des verantwortlichen Mitarbeiters über den Employee-Service umsetzen
- T_4: Prüfung des Kunden über den Kunden-Service (Dummy) umsetzen
- T_5: Fehlermeldungen für ungültige Eingaben umsetzen
- T_6: Integrationstests schreiben

---

## US-02 Projekte einsehen

**Als** Mitarbeiter **möchte ich** alle Projekte sowie ein bestimmtes Projekt mit allen Daten abrufen können,
**um** mir einen Überblick über laufende Projekte zu verschaffen.

**Akzeptanzkriterien**
- A_1: Alle Projekte werden mit allen Daten (inkl. zugeordneter Mitarbeiter) als JSON-Liste geliefert (200); gibt es keine, ist die Liste leer.
- A_2: Ein einzelnes Projekt kann über seine Id abgerufen werden (200).
- A_3: Existiert die Id nicht, erhält der User 404.

**Tasks**
- T_1: Abruf aller Projekte umsetzen
- T_2: Abruf eines einzelnen Projekts umsetzen
- T_3: Fehlermeldung für unbekanntes Projekt umsetzen
- T_4: Integrationstests schreiben

---

## US-03 Projektdaten ändern

**Als** Mitarbeiter **möchte ich** die Daten eines Projekts ändern können,
**um** auf geänderte Rahmenbedingungen (z. B. neuer Termin, anderer Verantwortlicher) zu reagieren.

**Akzeptanzkriterien**
- A_1: Alle Projektdaten außer der Id können geändert werden; Antwort 200 mit dem geänderten Projekt.
- A_2: Existiert das Projekt nicht → 404.
- A_3: Es gelten dieselben Prüfungen wie beim Anlegen (Pflichtfelder, Datum, verantwortlicher Mitarbeiter, Kunde).

**Tasks**
- T_1: Ändern der Projektdaten umsetzen
- T_2: Prüfungen aus „Projekt anlegen“ wiederverwenden
- T_3: Integrationstests schreiben

---

## US-04 Projekt löschen

**Als** Mitarbeiter **möchte ich** ein Projekt löschen können,
**um** fälschlich angelegte oder nicht mehr benötigte Projekte zu entfernen.

**Akzeptanzkriterien**
- A_1: Ein Projekt wird über seine Id gelöscht, inkl. seiner Mitarbeiterzuordnungen; Antwort 204.
- A_2: Existiert das Projekt nicht → 404.

**Tasks**
- T_1: Löschen eines Projekts inkl. Mitarbeiterzuordnungen umsetzen
- T_2: Integrationstests schreiben

---

## US-05 Mitarbeiter mit Qualifikation einem Projekt zuweisen

**Als** Mitarbeiter **möchte ich** einem bestehenden Projekt Mitarbeiter mit einer bestimmten Qualifikation zuweisen können,
**um** das Projektteam passend zusammenzustellen.

**Akzeptanzkriterien**
- A_1: Eine Zuweisung besteht aus Mitarbeiter-Id und Qualifikation; Antwort 201 mit dem aktualisierten Projekt als JSON.
- A_2: Existiert das Projekt nicht → 404.
- A_3: Existiert der Mitarbeiter im Employee-Service nicht → 404.
- A_4: Besitzt der Mitarbeiter die angegebene Qualifikation laut Employee-Service nicht → 422 (bzw. vom Team festgelegter Code) mit Fehlermeldung.
- A_5: Ist der Mitarbeiter diesem Projekt bereits zugeordnet → 409.

**Tasks**
- T_1: Datenmodell für die Mitarbeiterzuordnung erstellen
- T_2: Zuweisen eines Mitarbeiters umsetzen
- T_3: Prüfung von Mitarbeiter und Qualifikation über den Employee-Service umsetzen
- T_4: Fehlermeldungen umsetzen
- T_5: Integrationstests schreiben

---

## US-06 Doppelte Verplanung eines Mitarbeiters verhindern

**Als** Mitarbeiter **möchte ich** beim Zuweisen eine Fehlermeldung erhalten, wenn der Mitarbeiter im selben Zeitraum schon in einem anderen Projekt eingeplant ist,
**um** Mitarbeiter nicht doppelt zu verplanen.

> Aus US-05 herausgeschnitten (Muster „Variation der Geschäftsregeln").

**Akzeptanzkriterien**
- A_1: Überschneidet sich der Zeitraum (Start- bis Enddatum) des Projekts mit einem anderen Projekt, dem der Mitarbeiter bereits zugeordnet ist → 409 mit Fehlermeldung, die das kollidierende Projekt nennt.
- A_2: Grenzen gelten als Überschneidung (Ende Projekt A = Start Projekt B → Konflikt).
- A_3: Ohne Überschneidung ist die Zuweisung erfolgreich.

**Tasks**
- T_1: Prüfung auf überschneidende Projektzeiträume umsetzen
- T_2: Prüfung in die Mitarbeiterzuweisung einbauen
- T_3: Integrationstests schreiben

---

## US-07 Mitarbeiter aus einem Projekt entfernen

**Als** Mitarbeiter **möchte ich** einen Mitarbeiter aus einem Projekt entfernen können,
**um** ihn für andere Projekte freizugeben.

**Akzeptanzkriterien**
- A_1: Antwort bei Erfolg: 200 mit dem aktualisierten Projekt (oder 204 – Team entscheidet).
- A_2: Existiert das Projekt nicht → 404.
- A_3: Ist der Mitarbeiter nicht am Projekt beteiligt → 404 mit Fehlermeldung.

**Tasks**
- T_1: Entfernen eines Mitarbeiters aus einem Projekt umsetzen
- T_2: Fehlermeldungen umsetzen
- T_3: Integrationstests schreiben

---

## US-08 Mitarbeiter eines Projekts abrufen

**Als** Mitarbeiter **möchte ich** alle Mitarbeiter eines Projekts abrufen können,
**um** zu sehen, wer mit welcher Qualifikation am Projekt arbeitet.

**Akzeptanzkriterien**
- A_1: Die Antwort enthält Projekt-Id, Projektbezeichnung und eine Liste der Mitarbeiter mit Id und Qualifikation (200).
- A_2: Existiert das Projekt nicht → 404.

**Tasks**
- T_1: Abruf der Mitarbeiter eines Projekts umsetzen
- T_2: Integrationstests schreiben

---

## US-09 Projekte eines Mitarbeiters abrufen

**Als** Mitarbeiter **möchte ich** alle Projekte abrufen können, an denen ein bestimmter Mitarbeiter mitwirkt oder mitgewirkt hat,
**um** seine Einsätze nachvollziehen zu können.

**Akzeptanzkriterien**
- A_1: Die Antwort enthält einmalig die Mitarbeiter-Id sowie eine Liste der Projekte mit Id, Bezeichnung, Start- und Enddatum und der Qualifikation im Projekt (200).
- A_2: Existiert der Mitarbeiter im Employee-Service nicht → 404.
- A_3: Ist der Mitarbeiter keinem Projekt zugeordnet, ist die Liste leer.

**Tasks**
- T_1: Abruf der Projekte eines Mitarbeiters umsetzen
- T_2: Prüfung des Mitarbeiters über den Employee-Service umsetzen
- T_3: Integrationstests schreiben
