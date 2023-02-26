# Support Aggregator Hub
## **Build (with tests)**
    mvn clean install

## **Execution**
    mvn spring-boot:run  


I populate the db with some crm/cases on each startup to make the endpoints useful for testing

## **Endpoints**
(by default all on localhost:8080)
- GET - http://localhost:8080/aggregated  - Get aggregated results

- GET - http://localhost:8080/searchByStatus?status=Close - Search by status

- GET - http://localhost:8080/searchByError?error=1 - Search by error

- GET - http://localhost:8080/searchByProvider?provider=1 Search by provider

- **PUT** - http://localhost:8080/refresh      -Trigger cases refresh





## **Requirements**

**Functional requirements:**

- Refresh CRM cases every X minutes (configurable)
- Aggregation support
- Online refresh button
- Search support
- Restart handling
- Handle of 'refresh' requests bombing from users and concurrent calls/

**Non functional requirements:**

- Low scale
- Possibly peaks when critical issues rise (many users entering the system concurrently)
- Read heavy (relatively to the writes at least)

## **Implementation details**

- The service was implemented using Spring Boot
- JPA is used for entity persisting and loading
- Tests are done using Spring and Mockito
- Database - RDBMS - the data scale is low, the data is structured in a way that fit more RDBS when considering also future use cases.
- The service is divided to the following services/modules -
  - CRM - responsible to handle the CRM systems metadata
  - Fetch - responsible to refresh support cases from the CRM systems
  - Aggregate - responsible to aggregate the cases
  - Refresh - allows to coordinate the process of Fetch and then Aggregate the         cases
  - Search - responsible to allow search functionally
- Each service is broken to client package which should be compiled (if those were real services) and be used by clients and to internal package - in fashion similar to https://www.baeldung.com/java-microservices-share-dto

## **Design decisions:**

1. As the major point of the system is to provide visibility to currently handled cases to avoid having the same issue handled by multiple engineers - I performed the aggregation by error code and project and not as shown in the task document.

2. Because of the same reason, the system currently support only Search for closed cases (they aren't part of the aggregations) - but supports adding it, was considered during the design.
