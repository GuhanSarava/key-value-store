# Key-Value Data Store Service

# Key-Value Data Store Service

A high-performance, scalable key-value data store service built using Java and Spring Boot. It supports CRUD operations, TTL, batch processing, and multi-tenancy with PostgreSQL as the backend.

## Table of Contents:
1. [Setup Instructions](#setup-instructions)
2. [How to Run](#how-to-run)
3. [Testing the Solution](#testing-the-solution)
4. [Design Decisions](#design-decisions)
5. [System-Specific Dependencies](#system-specific-dependencies)
6. [License](#license)

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
- **CRUD API**: Create, Read, Delete key-value pairs.
- **TTL**: Automatically expire keys after a defined time.
- **Batch API**: Handle multiple key-value pairs in a single request.
- **Multi-Tenancy**: Separate data storage for different tenants.
- **Error Handling & Security**: Proper error responses and tenant data isolation.

## API Endpoints

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
