# Movie Ticket Booking Backend System

This project is a **backend system for booking movie tickets**, designed to handle failure scenarios using **microservices**, **event-driven architecture**, **Saga**, and **CQRS**.

The goal of this project is to build a **real-world booking system** that behave under retries, timeouts, and partial failures.

---

## Services Overview

### 1. Booking Service

Responsible for managing the booking lifecycle.

**Responsibilities**

- Create bookings
- Maintain booking status
- Orchestrate the booking flow
- Handle booking timeouts

**Databases**

- PostgreSQL → Write model
- MongoDB → Read model

**Booking States**

- `PENDING` – booking created
- `PAYMENT_PENDING` – seats reserved, waiting for payment
- `CONFIRMED` – payment successful
- `CANCELLED` – timeout or explicit cancellation

---

### 2. Seat Inventory Service

Responsible for seat availability and locking.

**Responsibilities**

- Prevent double booking
- Reserve seats temporarily
- Finalize seats after payment
- Release seats on timeout

**Seat States**

- `AVAILABLE`
- `RESERVED`
- `BOOKED`

Seats are NOT released immediately on payment failure. Seats are released only after timeout.

This allows users to retry payment without losing seats.

---

### 3. Payment Service

Responsible for payment processing and tracking.

**Responsibilities**

- Create payment requests
- Emit payment result events

**Guarantees**

- One payment per booking
- No duplicate charges

---

## Booking Flow (Step by Step)

### Step 1: Create Booking

- Booking is created with status `PENDING`
- A unique booking ID is generated
- Event is sent to Seat Inventory to reserve seats

---

### Step 2: Reserve Seat

- Checks seat availability
- Reserves seats (`RESERVED`)
- Sets `reservedUpto` timestamp (e.g. 10 minutes)
- Sends reservation result event

---

### Step 3: Payment

As of now, the Payment Service simulates payment processing.

- A payment request is created and stored
- A mock function returns **SUCCESS** or **FAILURE**
- Corresponding payment events are published

---

### Step 4: Final State Update

| Payment Result | Booking Status  | Seat Status |
| -------------- | --------------- | ----------- |
| Success        | CONFIRMED       | BOOKED      |
| Failure        | PAYMENT_PENDING | RESERVED    |

User is allowed to retry payment.

---

## CQRS Design

### Write Model (PostgreSQL)

- Strong consistency
- All business rules enforced here

### Read Model (MongoDB)

- Optimized for queries
- Updated asynchronously via events
- Eventually consistent

This allows:

- Fast reads
- Independent scaling
- Clean separation of concerns

---

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Kafka
- PostgreSQL
- MongoDB
- Redis
- Kafka
- Docker Compose

---

## Future Enhancements

### Stripe Payment Integration

- Replace simulated payment logic with Stripe PaymentIntent
- Handle payment success/failure via Stripe webhooks
- Support payment retries within the reservation timeout window

---

### Theatre Service

Introduce a dedicated Theatre Service to manage:

- Theatres
- Screens
- Shows
- Seat layouts

This service will act as the source of truth for theatre-related metadata and allow:

- Dynamic show creation
- Multiple screens per theatre
- Independent evolution of theatre management

---

### Dynamic Ticket Pricing

Support ticket price variations based on:

- Seat category (Regular, Premium, Recliner)
- Show timing (morning / evening / weekend)
- Seasonal demand (festivals, holidays)

This can be implemented using:

- Pricing configuration tables
- Cached price lookups

---

### Notification Service

- Send booking confirmations
- Payment reminders before timeout
- Booking cancellation notifications

Notifications can be delivered via:

- Email
- SMS
- Push notifications

---

This is intentionally excluded from the current scope to keep the focus on booking complexity.

---
