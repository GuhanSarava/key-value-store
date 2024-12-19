# Key-Value Data Store Service

A high-performance, scalable key-value data store service built using Java and Spring Boot. It supports CRUD operations, TTL, batch processing, and multi-tenancy with PostgreSQL as the backend.

## Table of Contents:
1. [Setup Instructions](#setup-instructions)
2. [Testing the Solution](#testing-the-solution)
3. [Design Decisions](#design-decisions)
4. [System-Specific Dependencies](#system-specific-dependencies)

## Setup Instructions:

### Prerequisites:
- **Java 17** or later
- **Maven** (for building the project)
- **PostgreSQL** (for storing key-value pairs)

Clone the repository:
```bash
git clone https://github.com/username/key-value-store.git
cd key-value-store
```

Modify application.properties
```
spring.datasource.url = jdbc:postgresql://localhost:5432/keyValueStore
spring.datasource.username = your_username
spring.datasource.password = your_password
```

## Features:
- **CRD API**: Create, Read, Delete key-value pairs.
- **TTL**: Automatically expire keys after a defined time.
- **Batch API**: Handle multiple key-value pairs in a single request.
- **Multi-Tenancy**: Separate data storage for different tenants.
- **Error Handling & Security**: Proper error responses and tenant data isolation.

## Testing the Solution:

### 1. Create a Key-Value Pair
**POST** `http://localhost:8080/api/object`

**Request Body:**
```json
{ 
    "key": "username", 
    "data": {
        "name": "name",
        "email": "samplemail@domain.com"
    },
    "ttl": 300 
}
```
### 2. Batch Create Key-Value Pairs
**POST** `http://localhost:8080/api/batch/object`

**Request Body:**
```json
[
    { 
        "key": "username1",
        "data": {
            "name": "name1",
            "email": "samplemail@domain.com"
        },
        "ttl": 300
    },
    {
        "key": "username2",
        "data": {
            "name": "name2",
            "email": "samplemail@domain.com"
        }, 
        "ttl": 300
    }
]
```
### 3. Get a Key-Value Pair
**GET** `http://localhost:8080/api/object/{key}`

**Response Body:**
```json
{
    "key": "username",
    "data": {
        "name": "name",
        "email": "samplemail@domain.com"
    }
}
```
### 4. Delete a Key-Value Pair
**DELETE** `http://localhost:8080/api/object/{key}`

## Design Decisions:
- **Spring Boot:** Chosen for rapid development, ease of integration, and built-in support for REST APIs.
- **PostgreSQL:** Selected for its scalability and support for transactional consistency.
- **TTL Handling:** Implemented using Spring's scheduling tasks to clean expired keys periodically.
- **Batch Processing:** Leveraged Spring Data JPA for batch inserts to improve performance.

## System-Specific Dependencies:
- **Windows:** To run this project on Windows, you need to have Java 17 and PostgreSQL installed. Make sure PostgreSQL is correctly configured.

## Note:
The project took 4 hours to complete, and I put in my best effort. Thank you for taking the time to review this project. Your feedback is highly appreciated.
