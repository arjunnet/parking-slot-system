# Parking Slot System

Spring Boot REST API for booking parking slots. A reservation that is not checked in within 10 minutes is released automatically.

## Features
- Add slots, list free slots
- Book, check in, exit
- Auto-release of expired reservations (scheduled job, runs every 60 s)
- Booking history per slot (ACTIVE, CHECKED_IN, COMPLETED, EXPIRED)
- Clean errors: 404 slot not found, 409 slot not available, 400 bad input

## Tech
Java 21, Spring Boot, Spring Data JPA, H2 (in-memory), Maven

## Run
1. Open the project in Eclipse (or any IDE)
2. Run `ParkingsystemApplication`
3. API at `http://localhost:8081`
4. H2 console at `http://localhost:8081/h2-console` (JDBC URL `jdbc:h2:mem:parkingdb`, user `sa`, empty password)

## Endpoints
| Method | URL | Body | Purpose |
|---|---|---|---|
| POST | /slots | `{"slotNo":"A1"}` | Add slot |
| GET | /slots/free | none | List free slots |
| POST | /slots/{id}/book | `{"vehicleNo":"KA01AB1234"}` | Reserve slot for 10 min |
| POST | /slots/{id}/check-in | none | RESERVED to OCCUPIED |
| POST | /slots/{id}/exit | none | OCCUPIED to FREE |
| GET | /slots/{id}/history | none | Booking history, newest first |

## Slot lifecycle
FREE, then book, RESERVED, then check-in, OCCUPIED, then exit, FREE.
No check-in within 10 minutes: slot returns to FREE and the booking is marked EXPIRED.

## Notes
Data is in memory and resets on restart. Switching to MySQL needs only datasource properties and the MySQL driver.