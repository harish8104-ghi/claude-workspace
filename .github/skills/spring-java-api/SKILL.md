---
name: spring-java-api
description: 'Build, extend, test, or troubleshoot this Java 25 Spring Boot REST API. Use when changing endpoints, request or response DTOs, controller behavior, or API tests.'
---

# Spring Java API

Use this workflow for changes to the REST API in this project.

## Project conventions

- Keep Java sources under `src/main/java/com/example/demo`.
- Put HTTP controllers in `com.example.demo.web` and response DTOs in `com.example.demo.web.dto`.
- The current API uses Spring MVC annotations, `ResponseEntity`, and Java DTOs with bean accessors.
- Keep API behavior covered by tests under `src/test/java`; controller tests currently use standalone `MockMvc`.
- Preserve the Java 25 compiler target and existing Spring Boot, Surefire, and class-format compatibility configuration in `pom.xml`.

## Workflow

1. Read the relevant controller, DTO, and tests before changing behavior.
2. Make the smallest coherent change that follows the existing package and Spring MVC patterns.
3. Add or update tests for the endpoint's success behavior, defaults, and any changed input behavior.
4. Run `mvn test` with JDK 25. If packaging or launch configuration changed, also run `mvn clean package`.
5. Report the changed behavior and validation results; surface any build or test failures without masking them.

## Existing endpoint

`GET /api/greet?name=YourName` returns a JSON object with a `message` field. When `name` is omitted, it defaults to `World`.
