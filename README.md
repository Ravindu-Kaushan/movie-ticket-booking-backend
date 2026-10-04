# Movie Ticket Booking System - Backend

IJSE CMJD 114/115 Final Coursework - Assignment 1 (Spring Boot + MySQL)

## Entity Relationship Diagram (ERD)

mermaid
erDiagram
    USERS ||--o{ BOOKINGS : makes
    SHOWS ||--o{ BOOKINGS : has
    BOOKINGS ||--o| PAYMENTS : paid_by
    BOOKINGS ||--|{ BOOKING_SEATS : contains
    MOVIES ||--o{ SHOWS : screened_in
    THEATRES ||--o{ SHOWS : hosts

    USERS {
        bigint id PK
        varchar full_name
        varchar email UK
        varchar password
        varchar role
    }

    MOVIES {
        bigint id PK
        varchar title
        varchar description
        int duration
        varchar language
        varchar genre
        date release_date
        varchar status
    }

    THEATRES {
        bigint id PK
        varchar theatre_name
        varchar location
        int capacity
        varchar status
    }

    SHOWS {
        bigint id PK
        bigint movie_id FK
        bigint theatre_id FK
        date show_date
        time show_time
        decimal ticket_price
        varchar status
    }

    BOOKINGS {
        bigint id PK
        bigint user_id FK
        bigint show_id FK
        int number_of_tickets
        decimal total_amount
        datetime booking_date
        varchar status
    }

    BOOKING_SEATS {
        bigint booking_id FK
        varchar seat_number
    }

    PAYMENTS {
        bigint id PK
        bigint booking_id FK
        decimal amount
        datetime payment_date
        varchar payment_method
        varchar status
    }
