# software-engineering

the best programmers

| Github Username | QMID      |
| :-------------- | :-------- |
| gdt1205         | 231225959 |
| Gaojie13        | 231225926 |
| Ddark-123       | 231225753 |
| MasterYibo      | 231225948 |
| Kerui-aua       | 231225845 |
| wangyanzhou     | 231225937 |

## Project Overview

This project is a TA recruitment system built with Java Servlet/JSP.

- No external database is used.
- Runtime data is stored in local JSON files under `data/`.
- The system supports three roles: `TA`, `MO`, and `ADMIN`.

## Environment

- JDK 11 or later
- No separate Maven installation is required
- Maven Wrapper is included: `mvnw` / `mvnw.cmd`

## Start The Project

Run from the project root:

- Windows: `.\mvnw.cmd tomcat7:run`
- macOS / Linux: `./mvnw tomcat7:run`

When Tomcat starts successfully, open [http://localhost:8080/](http://localhost:8080/).

You can also start `tomcat7:run` from IntelliJ IDEA or VS Code if you prefer an IDE task.

## Seed Accounts

These accounts are already present in `data/users/` and can be used directly:

| Role | User ID | Password | Notes |
| :--- | :------ | :------- | :---- |
| ADMIN | `ADMIN001` | `password123` | account management, project monitoring, logs |
| MO | `MO001` | `password123` | position publishing and application review |
| TA | `TA001` | `password123` | has profile fields and uploaded CV path |
| TA | `TA002` | `123456` | second TA account for application demos |

## Demo Flows

### TA demo

1. Sign in with `TA001 / password123`.
2. Open `Job overview` and browse available positions.
3. Submit an application from `/ta/jobs`.
4. Return to `/ta/home` to view application status, feedback, and message thread.
5. Open `/ta/notifications` to verify notifications are visible.

### MO demo

1. Sign in with `MO001 / password123`.
2. Open `/mo/home` and then `/mo/positions`.
3. Publish a position or edit an existing one.
4. Open `Applicants` or `Applications` to review TA submissions.
5. Open an application detail page and verify that CV links, status updates, and feedback are available.

### Admin demo

1. Sign in with `ADMIN001 / password123`.
2. Open `/ad/accounts` to view account data.
3. Open `/ad/projects` to view project/job statistics.
4. Open `/ad/logs` to inspect operation logs.
5. Freeze a TA account, sign out, and verify that the frozen TA can no longer log in.

## Validation Commands

Run unit tests:

- Windows: `.\mvnw.cmd test`
- macOS / Linux: `./mvnw test`

Run the internal navigation audit:

- Windows PowerShell: `powershell -ExecutionPolicy Bypass -File .\scripts\check-nav-links.ps1`

What the navigation audit checks:

- links and form actions declared in JSP files
- `window.location.href` targets declared in JS files
- `fetch(...)` targets declared in JS files
- whether each discovered internal target resolves to an existing servlet route or JSP

## Key Behaviors

- Login redirects users to the correct role home page.
- Protected paths under `/ta/*`, `/mo/*`, and `/ad/*` are role-restricted.
- Frozen users are blocked during authentication.
- Legacy `motest` entry points are redirected into the unified login flow.

## Project Structure

```text
software-engineering/
├── src/main/java/com/bupt/tarecruit/
│   ├── controller/   # Servlet entry points
│   ├── service/      # Business logic
│   ├── repository/   # JSON persistence layer
│   ├── model/        # Domain models
│   ├── util/         # Shared helpers
│   └── web/          # Filters and web infrastructure
├── src/main/webapp/
│   ├── WEB-INF/      # web.xml
│   ├── jsp/          # JSP views
│   ├── css/          # styles
│   ├── js/           # browser scripts
│   └── assets/       # static media assets
├── data/
│   ├── users/        # account JSON files
│   ├── jobs/         # job JSON files
│   ├── applications/ # application JSON files
│   ├── notifications/# notification JSON files
│   └── operation-logs/# operation log JSON files
├── scripts/          # local validation scripts
└── uploads/          # uploaded CV files
```

## Development Notes

- Keep persistence file-based. Do not add a database.
- Put request parsing and response routing in `controller`.
- Put business rules in `service`.
- Put read/write logic for JSON files in `repository`.
