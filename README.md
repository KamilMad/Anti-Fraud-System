# Anti-Fraud-System
- [Introduction](#introduction)
- [Features](#features)
- [Technologies](#technologies)
- [Setup](#setup)

## Introduction
This project demonstrates (in a simplified form) the principles of anti-fraud systems in the financial sector. This project contain a system with an expanded role model, a set of REST endpoints responsible for interacting with users, and an internal transaction validation logic based on a set of heuristic rules.

## Features

1. User Management System
2. REST Endpoints
3. Role-Based Access Control
4. Transaction Validation Logic
5. Error handling

## Technologies
* Spring Boot
* Spring Security
* Spring Data JPA
* Lombok
* Junit
* Mockito
* Docker

## Setup

Step 1: Clone project
git clone https://github.com/KamilMad/Anti-Fraud-System.git

Step 2: Open cloned directory
cd Anti-Fraud-System

Step 3: Build project
mvn clean install -DskipTests

Step 4: Build docker image
docker run -t anti-fraud-system

Step 5: Run using docker-compose
docker-compose up -d
