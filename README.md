# Key-Value Data Store

A high-performance, scalable key-value data store service built using Java and Spring Boot. It supports CRUD operations, TTL, batch processing, and multi-tenancy with PostgreSQL as the backend.

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

## Database Setup for First-Time Use

The Key-Value Store service uses Spring Boot's default table creation mechanism to automatically set up the database schema. This eliminates the need for manual database initialization scripts.

### How It Works
- **Spring JPA and Hibernate Integration**: The application leverages Hibernate, an ORM (Object-Relational Mapping) framework, to map Java entity classes to database tables.
- **Automatic Table Creation**: Spring Boot uses the spring.jpa.hibernate.ddl-auto property in the application.properties file to control the database schema creation and update process.

### Configuration
- **The following properties in application.properties manage the database setup**:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/keyValueStore
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## Features:
- **Multi-Tenancy**: Supports multiple tenants, ensuring data isolation.
- **Generic Key-Value Storage**: Capable of storing various types of data beyond user information.
- **Data Size Limits**: Enforces limits on both keys and values to maintain performance and reliability.
- **CRD API**: Create, Read, Delete key-value pairs.
- **TTL**: Automatically expire keys after a defined time.
- **Batch API**: Allows batch creation of key-value pairs with error handling for individual failures.
- **Concurrency Handling**:Manages concurrent access to ensure data consistency.
- **Error Handling & Security**: Proper error responses.

## API Endpoints:

### 1. Create a Key-Value Pair
**POST** `/api/object/{tenantId}`

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
**POST** `/api/batch/object/{tenantId}`

**Request Body:**
```json
[
    { 
        "key": "username1",
        "data": { "field": "value" },
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
**GET** `/api/object/{tenantId}/{key}`

**Response Body:**
```json
{
    "key": "username",
    "data": {
      "field":"value"
    }
}
```
### 4. Delete a Key-Value Pair
**DELETE** `/api/object/{tenantId}/{key}`

## Testing
Unit tests are included to ensure functionality and reliability. Tests cover various scenarios including
### Successful creation of key-value pairs:
```java
@Test
void testCreateKeyValue_Success() throws JsonProcessingException {
    // Setup and assertions for successful creation
}
```
### Handling duplicate keys:
```java
@Test
void testCreateKeyValue_DuplicateKey() {
    // Test to ensure duplicate keys throw an exception
}
```
### Batch creation with partial success:
```java
@Test
void testCreateKeyValueBatch_PartialSuccess() throws JsonProcessingException {
        // Tests batch creation where some keys succeed while others fail
        }
```
### Fetching Non-Existent Keys:
```java
@Test
void testGetKeyValue_NotFound() {
    // Ensures fetching a non-existent key throws an exception
}
```
### Exceeding Value Size Limit:
```java
@Test
void testCreateKeyValue_Exceeds16KB() throws JsonProcessingException {
    // Tests rejection of values exceeding 16KB
}
```
### Expiration Handling:
```java
@Test
void testGetKeyValue_KeyExpired() {
    // Verifies that fetching an expired key results in an exception
}

@Test
void testGetKeyValue_KeyNotExpired() throws JsonProcessingException {
    // Checks retrieval of a valid key within its TTL
}
```
### Creation with TTL:
```java
@Test
void testCreateKeyValue_WithTTL() throws JsonProcessingException {
    // Tests creation of a key-value pair with a specified TTL
}
```



## Design Decisions:
- **Spring Boot:** Chosen for rapid development, ease of integration, and built-in support for REST APIs.
- **PostgreSQL:** Selected for its scalability and support for transactional consistency.
- **TTL Handling:** Implemented using Spring's scheduling tasks to clean expired keys periodically.
- **Batch Processing:** Leveraged Spring Data JPA for batch inserts to improve performance.

## System-Specific Dependencies:
- **Windows:** To run this project on Windows, you need to have Java 17 and PostgreSQL installed. Make sure PostgreSQL is correctly configured.

## Note:
The project took 5 hours to complete, and I put in my best effort. Thank you for taking the time to review this project. Your feedback is highly appreciated.
