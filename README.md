# Plateforme de réservation de coworking — Microservices

Architecture microservices Spring Boot / Spring Cloud pour la gestion de réservations de salles de coworking.

## Architecture

```
                        API Gateway (8080)
                              │
            ┌─────────────────┼──────────────────┐
            │                 │                  │
     Room Service (8081)  Member Service (8082)  Reservation Service (8083)
            │                 │                  │
            └─────────────────┼──────────────────┘
                              │
                       Apache Kafka (9092)

Infrastructure :
- Config Server  (8888)  — configuration centralisée (Spring Cloud Config, native)
- Discovery Server (8761) — registre de services (Eureka)
```

## Prérequis

- Java 21
- Maven 3.9+
- Apache Kafka (avec ZooKeeper)
- [Optionnel] Docker

## Démarrage de Kafka

```bash
# Avec Docker Compose (recommandé)
docker run -d --name zookeeper -p 2181:2181 zookeeper:3.8
docker run -d --name kafka -p 9092:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=host.docker.internal:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  confluentinc/cp-kafka:7.5.0
```

## Ordre de démarrage des services

Les services **doivent** être démarrés dans cet ordre :

1. **Config Server** (`config-server/`)
2. **Discovery Server** (`discovery-server/`)
3. **API Gateway** (`api-gateway/`)
4. **Room Service** (`room-service/`)
5. **Member Service** (`member-service/`)
6. **Reservation Service** (`reservation-service/`)

## Lancement

Dans chaque répertoire de service :

```bash
mvn spring-boot:run
```

Ou depuis la racine pour compiler tous les modules :

```bash
mvn clean package -DskipTests
```

## URLs utiles

| Service | URL | Swagger UI | H2 Console |
|---------|-----|-----------|------------|
| Config Server | http://localhost:8888 | — | — |
| Discovery Server (Eureka) | http://localhost:8761 | — | — |
| API Gateway | http://localhost:8080 | — | — |
| Room Service | http://localhost:8081 | http://localhost:8081/swagger-ui.html | http://localhost:8081/h2-console |
| Member Service | http://localhost:8082 | http://localhost:8082/swagger-ui.html | http://localhost:8082/h2-console |
| Reservation Service | http://localhost:8083 | http://localhost:8083/swagger-ui.html | http://localhost:8083/h2-console |

## Topics Kafka

| Topic | Producteur | Consommateur | Description |
|-------|-----------|-------------|-------------|
| `room-deleted` | room-service | reservation-service | Annule toutes les réservations CONFIRMED de la salle |
| `member-deleted` | member-service | reservation-service | Supprime toutes les réservations du membre |
| `member-suspended` | reservation-service | member-service | Met `suspended=true` sur le membre |
| `member-unsuspended` | reservation-service | member-service | Met `suspended=false` sur le membre |

## API — Exemples Postman

### Room Service
```
POST http://localhost:8080/rooms
{
  "name": "Salle Einstein",
  "city": "Paris",
  "capacity": 10,
  "type": "MEETING_ROOM",
  "hourlyRate": 25.00
}
```

### Member Service
```
POST http://localhost:8080/members
{
  "fullName": "Alice Dupont",
  "email": "alice@example.com",
  "subscriptionType": "BASIC"
}
```

### Reservation Service
```
POST http://localhost:8080/reservations
{
  "roomId": 1,
  "memberId": 1,
  "startDateTime": "2026-04-01T09:00:00",
  "endDateTime": "2026-04-01T11:00:00"
}
```

## Design Pattern

Voir [DESIGN_PATTERN.md](DESIGN_PATTERN.md) — State Pattern appliqué au cycle de vie des réservations.
