# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

JSON tools is a university (PUT Poznań, "IO") team project: a tool for developers to reformat JSON (minify / prettify), filter JSON structures, and compare two JSON documents. It is meant to be exposed both through a GUI and as a remote REST API. The README and CI step names are in Polish.

## Commands

Maven is used directly (no wrapper). Java 11, Spring Boot 2.3.3.

```bash
mvn -B verify                                   # build + run all tests (what CI runs)
mvn test -Dtest=JsonLoaderTest                  # single test class
mvn test -Dtest=JsonLoaderTest#rejectsEmptyInput # single test method
mvn spring-boot:run                             # start the REST server (port 8080)
```

CI (`.github/workflows/ci.yml`) runs `mvn -B verify` on JDK 11 (Temurin) for pushes and PRs to `main`/`master`.

## Architecture

Package root `pl.put.poznan.tools`, split into layers that should stay separate — business logic must not live in the REST controller:

- `app` — `JsonToolsApplication`, the Spring Boot entry point. Note `scanBasePackages = {"pl.put.poznan.tools.rest"}`: only the `rest` package is component-scanned, so any Spring beans added elsewhere won't be picked up unless this is changed. Logic classes are currently plain Java objects instantiated directly.
- `rest` — `JsonToolsController`. Still the course template placeholder (`/{text}` GET/POST with a `transforms` parameter) and is expected to be replaced with real endpoints.
- `logic` — `JsonTools` is also a template placeholder (just uppercases text).
- `logic.model` — the real core so far:
  - `JsonLoader` parses JSON text / `InputStream` / `Path` with a strictly configured Jackson `ObjectMapper` (duplicate keys rejected, trailing tokens rejected, floats as `BigDecimal` to preserve precision). Empty input is an error.
  - `JsonDocument` is the internal data model that all operations (minify, prettify, filter, compare) are meant to work on. It wraps a Jackson `JsonNode`; the constructor is package-private so documents are created only via `JsonLoader`. The tree is shared, not copied — operations that mutate must use `copy()`. `equals` compares JSON values (object key order ignored).
  - `InvalidJsonException` (unchecked) carries 1-based line/column of the parse error, or -1 when unknown. I/O failures surface as `UncheckedIOException`.

Tests use JUnit 5 (vintage engine excluded) and mirror the main package layout under `src/test/java`.

## Workflow

Work happens on feature branches (e.g. `json-loader`) merged into `main` via pull requests on GitHub (`GuMa-Inc` org). `docs/JSON tools.xlsx` holds project/sprint planning material.
