# Clustered Data Warehouse (CDW) - FX Deals Importer

This project implements a **Data Warehouse** to process FX deals for Bloomberg. It validates and persists deals, prevents duplicates, and provides both CLI and web interface for imports.

---

## Features

- CSV import of FX deals
- Validation of required fields and formats
- Duplicate detection based on `dealId`
- No rollback: all valid rows are persisted
- REST endpoints & web frontend
- Unit testing with coverage
- Dockerized deployment with PostgreSQL

---

## Tech Stack

- Java 17, Spring Boot
- Maven
- PostgreSQL
- Docker & Docker Compose
- Thymeleaf frontend
- JUnit & Mockito
- SLF4J / Logback for logging

---

## Prerequisites

- JDK 17+
- Maven
- Docker & Docker Compose
- Git
