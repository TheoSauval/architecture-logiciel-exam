# Design Pattern — State Pattern dans le Reservation Service

## Choix du pattern

**Pattern utilisé : State (Comportemental)**

## Problème résolu

Le cycle de vie d'une réservation suit des transitions strictes :

```
CONFIRMED ──► CANCELLED
CONFIRMED ──► COMPLETED
CANCELLED ──► ✗ (état terminal)
COMPLETED ──► ✗ (état terminal)
```

Sans pattern, ce comportement serait géré par des `if/else` ou `switch` dans le service :

```java
// Sans State Pattern — code fragile et difficile à maintenir
if (reservation.getStatus() == CONFIRMED) {
    reservation.setStatus(CANCELLED);
} else if (reservation.getStatus() == CANCELLED) {
    throw new IllegalStateException("Déjà annulée");
}
// ...
```

## Solution appliquée

Chaque statut est représenté par une classe d'état qui encapsule les transitions autorisées.

### Structure

```
ReservationState (interface)
├── cancel(Reservation) : Reservation
└── complete(Reservation) : Reservation

ConfirmedState    → cancel() ✓  |  complete() ✓
CancelledState    → cancel() ✗  |  complete() ✗  (état terminal)
CompletedState    → cancel() ✗  |  complete() ✗  (état terminal)

ReservationStateFactory → retourne l'état correspondant au statut courant
```

### Utilisation dans le service

```java
// Dans ReservationService
Reservation reservation = reservationRepository.findById(id)...;
ReservationState state = ReservationStateFactory.getState(reservation.getStatus());
Reservation updated = state.cancel(reservation);  // lance exception si non autorisé
reservationRepository.save(updated);
```

## Avantages

1. **Principe Ouvert/Fermé** : ajouter un nouveau statut (ex. `PENDING`) ne nécessite que d'ajouter une classe, sans modifier le service.
2. **Transitions explicites** : les règles métier sont encapsulées dans chaque état, pas disséminées dans le service.
3. **Erreurs claires** : chaque état terminal lance une `IllegalStateException` explicite.

## Alternatives envisagées

- **Builder** : pertinent pour la construction d'une réservation avec validations multiples, mais la logique de validation est déjà dans `ReservationService.createReservation()`.
- **Strategy** : moins adapté car les transitions dépendent de l'état courant de l'objet, pas d'un algorithme interchangeable.
