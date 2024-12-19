# Key-Value Data Store Service

A high-performance, scalable key-value data store service that supports CRUD operations, TTL, and batch processing. The service uses an RDBMS for data storage and ensures multi-tenancy, security, and concurrency.

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
### 1. Batch Create Key-Value Pairs
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
