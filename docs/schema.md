# Initial data model

```mermaid
erDiagram
  CLIENT ||--o{ PET : owns
  PET ||--o{ APPOINTMENT : attends
  GROOMER ||--o{ APPOINTMENT : performs
  GROOMING_SERVICE ||--o{ APPOINTMENT : provides
```

An appointment stores its scheduled start time and its expected end time. The end time is calculated from the selected service duration when a booking is created or rescheduled.

