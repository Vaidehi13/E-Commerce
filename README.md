# E-Commerce Microservice Application

## Overview

This is an e-commerce microservice application 
designed to handle various aspects of online shopping, including user authentication, product management, 
inventory checks, and order processing. 
The architecture is based on microservices, enabling scalability, load balancing and flexibility.

## Architecture

The application consists of the following microservices:

- **User Service**: Handles user registration, authentication and issues JWT tokens for secure access.
- **Product Service**: Allows only admin users to add and manage products.
- **Inventory Service**: Checks product availability and updates stock after an order is placed successfully.
- **Order Service**: Facilitates order placement and interacts with the inventory service to verify stock.
- **Notification Service**: Sends email updates to users after an order is placed using asynchronous communication via Kafka and failed notifications are handled using Kafka fallback.

## Communication

- **Synchronous Communication**: The Order Service communicates with the Inventory Service to check stock levels and update inventory using **RestTemplate**.
- **Asynchronous Communication**: The Order Service sends notifications to users through the Notification Service using **Kafka** in an **Event Driven Architecture**.

## Features

- **JWT Authentication**: Secure user authentication using JSON Web Tokens (JWT).
- **API Gateway**: Acts as a single entry point for all microservices.
- **Circuit Breaker Pattern**: Implemented using **Resilience4j** to enhance system resilience and stability on Order Service and the API Gateway.
- **Service Discovery**: Allows microservices to discover each other dynamically and also enabled Load Balancing for multiple instances of Inventory Service for an Order Service.
- **Distributed Tracing**: Utilizes **Micrometer, Brave** and **Zipkin** for tracing requests across services.
- **Observability**: Monitors application performance and health using the **Grafana** stack.
- **API Documentation**: Provides Swagger UI for easy access to API documentation.
- **Database Migration**: Manages database schema changes using **Flyway**.
- **Docker**: Containerizes services for easier deployment and management.

## Technology Stack

- **Programming Language**: Java
- **Framework**: Spring Boot
- **Databases**: MySQL (for relational data) and MongoDB (for non-relational data)
- **Message Broker**: Kafka
- **Containerization**: Docker
- **Monitoring Tools**: Grafana, Loki, Tempo, Prometheus
- **API Documentation**: Swagger UI
- **Authentication**: JWT (JSON Web Tokens)
- **Circuit Breaker**: Resilience4j
- **Service Discovery**: Eureka
- **Distributed Tracing**: Micrometer and Zipkin
- **Observability**: Grafana stack
- **Database Migration**: Flyway
- **Monitoring Endpoints**:Actuator