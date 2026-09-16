# Python Docs Disabled Test Inventory

This file tracks Python docs examples of Micronaut Multitenancy that are present but disabled, or that deviate from the
Java example because the direct port currently fails compilation or at runtime (Python compiler gaps). Use it as the
bug-fixing task list for the final migration wave.

## Reconciliation

- Last generated active `@Disabled` count: 0.
- Last generated command: `rg -n "@Disabled\(" test-suite-python/src/test/python`.
- Last full-suite command: `./gradlew :test-suite-python:test -Ppython-ci`.
- Last full-suite result: build successful, 2 tests executed (2 test classes), 0 skipped.

## Migration Rules

- Python source files must not live in a package whose `__init__.py` the Python compiler also generates for an imported
  Java package: `micronaut/multitenancy/*.py` would collide with the shim of `io.micronaut.multitenancy.Tenant`
  (`Failed to write Python code to [.../micronaut/multitenancy/__init__.py]: Output stream or writer has already been
  opened`). The snippet classes were therefore moved to `io.micronaut.multitenancy.docs` in every language; the Python
  tests live in the same package.
- Controller method names are snake_case (`echo_tenant`); the `@Secured("#{ tenantId == 'allowed' }")` evaluated
  expression is compiled by the Python compiler like in Java (the `micronaut-multitenancy-annotations` processor is a
  `testImplementation` dependency of the Python suite).
- The Java tests inject the client as a test-method parameter; Python tests inject it as a class attribute
  (`client: Annotated[HttpClient, Inject, Client("/")]`). Java exceptions are caught by their imported type
  (`except HttpClientResponseException`).

## Active `@Disabled` Tests

None.

## Commented Unsupported Snippet Ports

None.

## Intentionally Unsupported Snippet Targets

None.
