# Testing Program Submission

This folder contains the testing program for the TA Recruitment System.

## Contents

- `src/test/java/`: JUnit 4 test cases for repository, service, and acceptance-level checks.
- `pom.xml`: Maven configuration showing the JUnit dependency and test setup used by the project.

## How to Run

Copy or keep this testing program with the main project source code, then run the tests from the main project root:

```powershell
.\mvnw.cmd test
```

The tests follow the standard Maven layout. The application source code remains in `src/main`, while the testing program is under `src/test`.

## Test Coverage Summary

- Repository tests: JSON persistence and data retrieval behavior.
- Service tests: business rules, validation, application submission, notifications, and admin workflows.
- Acceptance summary tests: TA and Admin module testing summaries.
