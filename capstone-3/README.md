# Smart Rental Recommendation Platform

## Project Overview

The Smart Rental Recommendation Platform is a Spring Boot backend that supports the complete apartment rental lifecycle for renters and property owners.

Renters can create profiles and housing preferences, resolve a workplace name into coordinates, receive ranked apartment recommendations, reserve apartments, accept contracts, communicate with owners, submit maintenance requests, write reviews, and find compatible roommates.

Owners can manage buildings and apartments, process reservations and contracts, communicate with renters, handle maintenance requests, review apartment performance, and access AI-generated summaries.

The recommendation engine keeps apartment selection under backend control. It filters and scores apartments using user preferences, rent, family suitability, nearby amenities, and commute information. Gemini receives the final ranked results only to generate a readable explanation.

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
- Gemini API for AI explanations and analysis
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
| GET | `/api/v1/apartment/get/underpriced` | Returns apartments identified as underpriced. |
| GET | `/api/v1/apartment/low-rated/{ownerId}/{buildingId}` | Returns low-rated apartments in an owner's building with an AI-generated issue summary. |
| GET | `/api/v1/apartment/dashboard/{ownerId}` | Returns apartment performance information for an owner dashboard. |
| GET | `/api/v1/apartment/search` | Searches available apartments using optional rent, bedroom, district, and furnished filters. |
| PUT | `/api/v1/apartment/available/{ownerId}/{apartmentId}` | Returns an apartment from maintenance status to available status. |
| PUT | `/api/v1/apartment/toggle-maintenance/{ownerId}/{apartmentId}` | Moves an available apartment into maintenance status. |
| GET | `/api/v1/apartment/next-available/{apartmentId}` | Explains when an apartment is expected to become available. |
| GET | `/api/v1/apartment/available-on/{apartmentId}/{date}` | Checks whether an apartment is available on a specific date. |
| GET | `/api/v1/apartment/flagged/{ownerId}` | Returns an owner's apartments flagged for unusual cancellation activity. |

### RecommendationController

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/recommendation/recommend/{userId}` | Filters, scores, and ranks apartments, then uses Gemini to explain the backend ranking. Supports `radiusMetres` and `language`. |

### AiApartmentController

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/ai/apartments/review-summary/{apartmentId}` | Generates an AI summary of an apartment's reviews. |
| GET | `/api/v1/ai/apartments/neighborhood-summary/{apartmentId}` | Generates an AI neighborhood summary using apartment and nearby-service data. |
| GET | `/api/v1/ai/owners/reputation-summary/{ownerId}` | Generates an AI summary of an owner's reputation from tenant reviews. |
| GET | `/api/v1/ai/apartments/compare/{id1}/{id2}` | Generates a human-readable comparison of two different apartments. |

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

| Method | Path | Purpose |
|---|---|---|
| PUT | `/api/v1/contract/accept/{contractId}/{userId}` | Accepts a pending contract and activates the rental. |
| PUT | `/api/v1/contract/reject/{contractId}/{userId}` | Rejects a pending contract and cancels the related rental flow. |
| GET | `/api/v1/contract/user/{userId}` | Returns contracts belonging to a user. |
| GET | `/api/v1/contract/owner/{ownerId}` | Returns contracts for apartments belonging to an owner. |
| PUT | `/api/v1/contract/end/{contractId}/{ownerId}` | Officially ends an active contract after rental completion. |
| PUT | `/api/v1/contract/terminate/{contractId}/{ownerId}` | Terminates an active contract early. |
| PUT | `/api/v1/contract/renew/{contractId}/{userId}` | Extends an active contract using the required `extraMonths` query parameter. |
| GET | `/api/v1/contract/analyze/{contractId}/{userId}` | Generates an AI analysis of a contract. Supports `language`. |
| POST | `/api/v1/contract/{contractId}/send-pdf` | Generates the contract PDF and sends it by email. |

### MaintenanceRequestController

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/maintenance-request/user/{userId}` | Returns maintenance requests submitted by a user. |
| GET | `/api/v1/maintenance-request/apartment/{apartmentId}` | Returns maintenance history for an apartment. |
| GET | `/api/v1/maintenance-request/building-summary/{buildingId}` | Generates a concise AI summary of maintenance activity in a building. |
| POST | `/api/v1/maintenance-request/add/{userId}/{apartmentId}` | Creates a tenant maintenance request and uses AI to classify its category and priority. |
| PUT | `/api/v1/maintenance-request/start/{ownerId}/{requestId}` | Moves a pending maintenance request to in-progress status. |
| PUT | `/api/v1/maintenance-request/complete/{ownerId}/{requestId}` | Completes an in-progress maintenance request. |

### ReviewController

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/reviews/get/apartment/{apartmentId}` | Returns reviews for an apartment. |
| GET | `/api/v1/reviews/get/user/{userId}` | Returns reviews written by a user. |
| GET | `/api/v1/reviews/get/owner/{ownerId}` | Returns reviews across an owner's apartments. |
| GET | `/api/v1/reviews/get/owner-analysis/{ownerId}` | Generates an AI analysis of reviews across an owner's apartments. |

### ConversationController

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/conversation/user/{userId}` | Returns the conversation inbox for a user. |
| GET | `/api/v1/conversation/owner/{ownerId}` | Returns the conversation inbox for an owner. |

### MessageController

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/message/conversation/{conversationId}` | Returns the complete message thread for a conversation. |
| POST | `/api/v1/message/add/user/{userId}/{ownerId}/{apartmentId}` | Sends a message as a user and creates or reuses the apartment conversation. |
| POST | `/api/v1/message/add/owner/{ownerId}/{userId}/{apartmentId}` | Sends a message as an owner and creates or reuses the apartment conversation. |

Message update and delete operations require `senderId` and `senderRole` query parameters so only the original sender can modify the message.

### UserPreferenceController

Both `/api/v1/user-preference` and `/api/v1/user-preferences` are valid controller base paths.

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/user-preferences/user/{userId}` | Returns the preference record associated with a user. |
| PUT | `/api/v1/user-preferences/add-workplace/{userId}` | Uses Nominatim to resolve a workplace name and saves its coordinates in the user's preferences. |

### RoommateController

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/roommates/matches/{userId}` | Uses backend eligibility rules and Gemini ranking to return compatible roommate matches. |
| GET | `/api/v1/roommates/requests/{userId}` | Returns all sent and received roommate requests for the selected user. |
| POST | `/api/v1/roommates/request/{senderId}/{receiverId}` | Sends a roommate request while preventing duplicate and reverse-duplicate pending requests. |
| PUT | `/api/v1/roommates/accept/{receiverId}/{requestId}` | Accepts a request, links both users, and removes them from the roommate search pool. |
| PUT | `/api/v1/roommates/reject/{receiverId}/{requestId}` | Rejects a pending roommate request. |
| PUT | `/api/v1/roommates/dissolve/{userId}` | Dissolves the current roommate relationship and restores roommate availability. |

### LocationAnalysisController

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/location/analyze` | Uses Overpass to count nearby services around `latitude` and `longitude`; accepts an optional `radiusMetres`. |

## Person 1 Extra Endpoint Count

| Controller | Extra endpoints |
|---|---:|
| ApartmentController | 7 |
| RecommendationController | 1 |
| AiApartmentController | 4 |
| UserPreferenceController | 2 |
| LocationAnalysisController | 1 |
| **Person 1 Total** | **15** |

## External Integration Summary

| Integration | Use in the system |
|---|---|
| Gemini | Recommendation explanations, apartment comparison, review summaries, owner analysis, contract analysis, maintenance classification, and roommate ranking. |
| Overpass | Counts nearby services such as schools, hospitals, supermarkets, pharmacies, gyms, and restaurants. |
| OSRM | Calculates driving duration and distance for recommendation commute scoring. |
| Nominatim | Converts a workplace name into latitude and longitude. |
| Spring Mail | Sends generated contract PDFs by email. |
| UltraMsg | Sends WhatsApp notifications during supported rental workflows. |

Google Places and Google Routes are not used by the current project.
