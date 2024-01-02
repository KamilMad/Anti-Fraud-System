# Anti-Fraud-System

A brief description of your project.

## Table of Contents
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
1.Clone project
  git clone https://github.com/KamilMad/Anti-Fraud-System.git
2.Open cloned directory
  cd Anti-Fraud-System
3.Build projecy
  mvn clean install -DskipTests
4.Build docker image
  docker run -t anti-fraud-system
5.Run using docker-compose
  docker-compose up -d

<div align="center">
  <button onclick="copyToClipboard('#exampleCode')">Copy Code</button>
</div>

```java
// Replace this code with your actual code
public class Example {
  public static void main(String[] args) {
    System.out.println("Hello, World!");
  }
}

<script>
function copyToClipboard(elementId) {
  var copyText = document.getElementById(elementId);
  var textArea = document.createElement("textarea");
  textArea.value = copyText.innerText;
  document.body.appendChild(textArea);
  textArea.select();
  document.execCommand("copy");
  document.body.removeChild(textArea);
  alert("Code copied to clipboard!");
}
</script>
