# Starter für das LF08-Projekt: Project-Management-Service

## Requirements
* Docker https://docs.docker.com/get-docker/
* Docker Compose (bei Windows und Mac schon in Docker enthalten) https://docs.docker.com/compose/install/

## Endpunkt
```
http://localhost:8080
```

## Swagger
```
http://localhost:8080/swagger
```

# Abhängigkeiten starten

`docker compose up` startet alles, was dieser Service braucht:

| Dienst | Wofür | Port |
|---|---|---|
| `postgres-pms` | eure eigene Datenbank | `15432` (extern) |
| `postgres-employee` + `employee` | der bestehende Employee-Service | `5432` / `8089` |
| `postgres-authentik` + `redis` + `authentik-server` + `authentik-worker` | der zentrale Identity-Provider | `9000` / `9443` |

```bash
docker compose up -d
```

Achtung: Die Container laufen dauerhaft! Wenn sie nicht mehr benötigt werden, sollten sie gestoppt werden.

### Stoppen
```bash
docker compose down
```

### Datenbank wipen, z. B. bei Problemen
```bash
docker compose down
docker volume rm starter_2026_pms_postgres_data
docker compose up
```
(Der genaue Volume-Name hängt vom Ordnernamen ab — `docker volume ls` zeigt ihn an.)

### Intellij-Ansicht für die eigene Postgres-Datenbank einrichten
```
1. Container laufen lassen (docker compose up)
2. im Ordner resources die Datei application.properties öffnen und die URL der Datenbank kopieren
3. rechts im Fenster den Reiter Database öffnen
4. In der Database-Symbolleiste auf das Datenbanksymbol mit dem Schlüssel klicken
5. auf das Pluszeichen klicken
6. Datasource from URL auswählen
7. URL der DB einfügen und PostgreSQL-Treiber auswählen, mit OK bestätigen
8. Username pms und Passwort secret eintragen (siehe application.properties), mit Apply bestätigen
9. im Reiter Schemas alle Häkchen entfernen und lediglich vor pms_db und public Häkchen setzen
10. mit Apply und OK bestätigen
```

# Employee-Service

Der bestehende Employee-Service läuft lokal mit, als fertiges Docker-Image. Seine eigene
Swagger-Dokumentation:
```
http://localhost:8089/swagger
```

Ruft ihn über `employee-service.base-url` aus `application.properties` auf (`http://localhost:8089`),
nicht über eine feste Adresse im Code — dann bleibt die Adresse an einer Stelle änderbar.

# Kunden-Service

Der Kunden-Service existiert noch nicht — er wird parallel von einem anderen Team gebaut. Das
Package `customer` enthält dafür schon eine Schnittstelle (`CustomerClient`) mit einer
Platzhalter-Implementierung (`DummyCustomerClient`), die jede Kennung als gültig behandelt.
Tauscht die Implementierung aus, sobald der echte Service bereitsteht — an den Aufrufstellen
ändert sich dadurch nichts.

# Authentik

Authentik läuft ebenfalls lokal mit. Beim ersten Start richtet ein Blueprint
(`authentik_blueprints/zzz_employee_setup.yaml`) automatisch einen Benutzer `john`, den
OIDC-Provider `employee_api` und ein App-Passwort für `john` ein — denselben Provider, gegen
den auch der Employee-Service prüft. Ein einziger Login genügt für alle Backoffice-Services
(Single Sign-on).

### Admin-Oberfläche
```
http://localhost:9000
```
Login: `a@b.com` / `secret` (nur nötig, wenn ihr selbst in Authentik nachschauen wollt —
für den JWT-Token unten nicht erforderlich).

### JWT-Token holen
Kein manueller Schritt in Authentik nötig — das App-Passwort für `john` ist im Blueprint fest
vorgegeben (`teachme123`) und steht schon in [GetBearerToken.http](GetBearerToken.http).
Einfach den Request darin ausführen und `access_token` aus der Antwort kopieren.

Ausführliche Beispiel-Anfragen stehen in [GetBearerToken.http](GetBearerToken.http) und
[SampleRequests.http](SampleRequests.http).

# Wo ihr weiterarbeitet

* `hello` — Beispiel-Ressource, die das Muster zeigt (Entity → Record-DTOs → Mapper →
  Repository → Service → Controller → Exception → `ApiExceptionHandler`). Fachlich bedeutungslos
  — ersetzt sie durch eure eigenen, in der Sprint-Planung entworfenen Ressourcen.
* `security/AuthentikSecurityConfig` — trägt eure Endpunkt-Pfade in `authorizeHttpRequests()` ein,
  sobald sie existieren. Der Rest (JWT-Prüfung, CORS) steht bereits.
* `config/OpenApiConfiguration` — hier passt ihr Titel/Beschreibung der Swagger-Dokumentation an.
* `customer/CustomerClient` — Dummy, siehe oben.
