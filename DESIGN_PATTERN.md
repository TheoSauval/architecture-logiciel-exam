# Design Pattern : State Pattern

## Choix du Pattern

Je suis parti sur Refactoring Guru pour check le patterne le plus adapter et j'ai vu State Pattern pour gérer le cycle de vie des réservations

## Pourquoi ?

Sur [Refactoring.Guru](https://refactoring.guru/design-patterns/state) :

> "State is a behavioral design pattern that lets an object alter its behavior when its internal state changes. It appears as if the object changed its class."

Une réservation passe par plusieurs états au cours de son cycle de vie :

- **CONFIRMED** : État initial après création
- **COMPLETED** : Une fois la réservation terminée
- **CANCELLED** : Si la réservation est annulée par l'utilisateur ou suite à la suppression d'une salle

Le State Pattern est adapté ici car, selon la définition du patterne on a :

1. **Encapsulation des transitions** : "The State pattern suggests that you create new classes for all possible states of an object and extract all state-specific behaviors into these classes."
2. **Simplification du code** : On évite les structures conditionnelles complexes (`if/else` ou `switch`) qui deviennent difficile à maintenir. Comme indiqué sur le site : "The State pattern is a solution to the problem of how to make an object change its behavior without using massive conditionals."
3. **Extensibilité** : Si un nouvel état doit être ajouté (ex: `PENDING_PAYMENT`), il suffit de créer une nouvelle classe implémentant l'interface sans modifier le code existant.

## Implémentation

- `ReservationState` : Interface définissant les actions possibles (`complete`, `cancel`)
- `ConfirmedState`, `CompletedState`, `CancelledState` : Implémentations concrètes des états
- `ReservationService` : Utilise l'état actuel de la réservation pour déléguer les actions

### Structure des transitions

```
CONFIRMED ──► CANCELLED
CONFIRMED ──► COMPLETED
CANCELLED ──► ✗ (état terminal)
COMPLETED ──► ✗ (état terminal)
```

Sans ce pattern, on aurais eu des `if/else` partout dans le service ce qui rend le code tres difficile à faire évoluer :

```java
// Sans State Pattern
if (reservation.getStatus() == CONFIRMED) {
    reservation.setStatus(CANCELLED);
} else if (reservation.getStatus() == CANCELLED) {
    throw new IllegalStateException("Déjà annulée");
}
// ...
```

Avec le State Pattern, le service délègue simplement à l'état courant :

```java
ReservationState state = ReservationStateFactory.getState(reservation.getStatus());
state.cancel(reservation); // chaque état gère lui même ce qui est autorisé ou pas
```

## Alternatives que j'ai envisagé

- **Strategy** : moins adapter car les transitions dépendent de l'état courant de l'objet, pas d'un algorithme interchangeable.
- **Builder** : intéressant pour la construction d'une réservation mais ca résoud pas le problème des transitions d'états.
