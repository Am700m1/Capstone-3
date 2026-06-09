# Smart Rental Recommendation Platform

## Project Overview

The Smart Rental Recommendation Platform is a Spring Boot backend that supports the complete apartment rental lifecycle for renters and property owners.

Renters can create profiles and housing preferences, resolve a workplace name into coordinates, receive ranked apartment recommendations, reserve apartments, accept contracts, communicate with owners, submit maintenance requests, write reviews, and find compatible roommates.

Owners can manage buildings and apartments, process reservations and contracts, communicate with renters, handle maintenance requests, review apartment performance, and access AI-generated summaries.

The recommendation engine keeps apartment selection under backend control. It filters and scores apartments using user preferences, rent, family suitability, nearby amenities, and commute information. OpenAI receives the final ranked results only to generate a readable explanation.

## Main Business Flow

1. Owners create buildings and apartments.
2. Users create profiles and rental preferences.
3. Nominatim converts workplace names into coordinates.
4. The backend ranks suitable apartments using preferences, Overpass amenity data, and OSRM commute data.
5. Users submit reservations for available apartments.
6. Owners approve or reject pending reservations.
7. A contract is created for an approved reservation.
8. The user accepts the contract and the apartment becomes rented.
9. Users and owners communicate through apartment conversations.
10. Active tenants can submit AI-classified maintenance requests.
11. When a contract ends, the reservation is completed and the apartment enters maintenance mode.
12. After maintenance, the owner returns the apartment to available status.
13. Users can review completed rentals.

## Main Technologies

- Java and Spring Boot
- Spring Web
- Spring Data JPA
- Jakarta Bean Validation
- MySQL
- Lombok
- RestTemplate
- Jackson ObjectMapper
- OpenAI API for AI explanations and analysis
- Overpass API for nearby services
- OSRM API for commute distance and duration
- Nominatim API for workplace geocoding
- Spring Mail for contract PDF email delivery
- UltraMsg for WhatsApp workflow notifications

## Apartment and Rental Status Flow

```text
Apartment:   AVAILABLE -> RESERVED -> RENTED -> UNDER_MAINTENANCE -> AVAILABLE
Reservation: PENDING -> APPROVED -> COMPLETED
Contract:    PENDING -> ACTIVE -> ENDED
Maintenance: PENDING -> IN_PROGRESS -> COMPLETED
```

Reservations and contracts may also be rejected, cancelled, expired, or terminated according to the current business rules.

## Extra Business Endpoints

The table below excludes basic get-all, get-by-ID, create, update, and delete endpoints. It contains the custom business, relationship, status-changing, AI, availability, messaging, and integration endpoints currently exposed by the controllers.

### ApartmentController

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/apartment/dashboard/{ownerId}` | Returns apartment performance information for an owner dashboard. |
| GET | `/api/v1/apartment/search` | Searches available apartments using optional rent, bedroom, district, and furnished filters. |


### AiApartmentController

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/ai/apartments/review-summary/{apartmentId}` | Generates an AI summary of an apartment's reviews. |
| GET | `/api/v1/ai/apartments/neighborhood-summary/{apartmentId}` | Generates an AI neighborhood summary using apartment and nearby-service data. |

AI endpoints accept the optional `language` query parameter with `EN` as the default and `AR` for Arabic output.

### ReservationController

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/reservation/get/pending` | Returns all pending reservations. |
| GET | `/api/v1/reservation/get/owner/{ownerId}/pending` | Returns pending reservations for apartments belonging to an owner. |
| GET | `/api/v1/reservation/get/user/{userId}` | Returns the reservations submitted by a user. |
| PUT | `/api/v1/reservation/accept/{ownerId}/{reservationId}` | Approves a pending reservation and reserves the apartment. |
| PUT | `/api/v1/reservation/reject/{ownerId}/{reservationId}` | Rejects a pending reservation and synchronizes related state. |
| PUT | `/api/v1/reservation/end/{userId}/{reservationId}` | Cancels a user's pending or approved reservation when allowed. |

### ContractController

| Method | Path | Purpose                                                                       |
|---|---|-------------------------------------------------------------------------------|
| PUT | `/api/v1/contract/renew/{contractId}/{userId}` | Send a request contract renwal to the owner.                                  |
| PUT | `/api/v1/contract/renew/approve/{contractId}/{ownerId}` | Approve a pending renew contract request and cancels the related rental flow. |
| PUT | `/api/v1/contract/renew/reject/{contractId}/{ownerId}` | Rejects a pending renew contract request and cancels the related rental flow. |
| GET | `/api/v1/contract/analyze/{contractId}/{userId}` | Generates an AI analysis of a contract. Supports `language`.                  |

### RoommateController

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/roommates/matches/{userId}` | Uses backend eligibility rules and OpenAI ranking to return compatible roommate matches. |


## External Integration Summary

| Integration | Use in the system |
|---|---|
| OpenAI | Recommendation explanations, apartment comparison, review summaries, owner analysis, contract analysis, maintenance classification, and roommate ranking. |
| Overpass | Counts nearby services such as schools, hospitals, supermarkets, pharmacies, gyms, and restaurants. |
| OSRM | Calculates driving duration and distance for recommendation commute scoring. |
| Nominatim | Converts a workplace name into latitude and longitude. |
| Spring Mail | Sends generated contract PDFs by email. |
| UltraMsg | Sends WhatsApp notifications during supported rental workflows. |

Google Places and Google Routes are not used by the current project.
