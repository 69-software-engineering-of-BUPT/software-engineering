# TA Recruitment System

Java Servlet/JSP project for managing teaching assistant recruitment across three roles:

- `TA`: browse positions, maintain profile data, upload a CV, apply for jobs, and track application status
- `MO`: publish and manage positions, review applicants, update decisions, and communicate with TAs
- `ADMIN`: monitor accounts, monitor projects, inspect logs, freeze or unfreeze accounts, and export data

The system uses file-based persistence only. All runtime data is stored in local JSON files under `data/`. No database is required or used.

## Team

| GitHub Username | QMID |
| :--- | :--- |
| `gdt1205` | `231225959` |
| `Gaojie13` | `231225926` |
| `Ddark-123` | `231225753` |
| `MasterYibo` | `231225948` |
| `Kerui-aua` | `231225845` |
| `wangyanzhou` | `231225937` |

## Tech Stack

- Java 11
- Java Servlet 4.0
- JSP 2.3
- Maven Wrapper
- Embedded Tomcat via `tomcat7-maven-plugin`
- Gson for JSON serialization
- JUnit 4 for tests

## Architecture

This project follows a traditional multi-page Java Web architecture.

- `controller/`: HTTP entry points implemented as servlets
- `service/`: business rules and workflow orchestration
- `repository/`: JSON file persistence
- `model/`: domain entities
- `util/`: helper utilities
- `web/`: servlet filter and web infrastructure
- `src/main/webapp/jsp/`: JSP views
- `src/main/webapp/js/`: browser-side interaction scripts
- `src/main/webapp/css/`: shared styling

## Repository Layout

```text
software-engineering/
├── src/
│   ├── main/
│   │   ├── java/com/bupt/tarecruit/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── util/
│   │   │   └── web/
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       ├── css/
│   │       ├── js/
│   │       ├── assets/
│   │       ├── index.jsp
│   │       └── jsp/
│   └── test/
│       └── java/com/bupt/tarecruit/
├── data/
│   ├── users/
│   ├── jobs/
│   ├── applications/
│   ├── notifications/
│   └── operation-logs/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Prerequisites

- JDK 11 or later
- Git
- No separate Maven installation is needed because Maven Wrapper is included

## Running the Application

Start the embedded Tomcat server from the project root:

```powershell
.\mvnw.cmd tomcat7:run
```

Open the application at:

- [http://localhost:8080/](http://localhost:8080/)

### Running on a Different Port

The Tomcat port is controlled by the Maven property `maven.tomcat.port` in [pom.xml](/E:/Desktop/软工/software-engineering/pom.xml:1).

To override it at runtime:

```powershell
.\mvnw.cmd -Dmaven.tomcat.port=8081 tomcat7:run
```

## Default Accounts

The repository already contains seed accounts in `data/users/`.

| Role | User ID | Password | Notes |
| :--- | :--- | :--- | :--- |
| `ADMIN` | `ADMIN001` | `password123` | system administration account |
| `MO` | `MO001` | `password123` | module organiser account |
| `TA` | `TA001` | `password123` | seeded TA profile with CV path and profile fields |
| `TA` | `TA002` | `123456` | second TA account for testing |

## Main User Flows

### TA flow

1. Sign in with a TA account.
2. Open `/ta/jobs` to browse available positions.
3. Upload a CV if required.
4. Submit an application.
5. Open `/ta/home` to review application status, feedback, and conversation history.
6. Open `/ta/notifications` to review notifications.

### MO flow

1. Sign in with the MO account.
2. Open `/mo/home` for the MO dashboard.
3. Publish a position from `/mo/publish`.
4. Review existing positions at `/mo/positions`.
5. Open applicants or application detail pages.
6. Approve, reject, or move applications into interview status.
7. Use the conversation pages to communicate with applicants.

### Admin flow

1. Sign in with the admin account.
2. Open `/ad/accounts` to review all accounts.
3. Freeze or unfreeze a user account.
4. Open `/ad/projects` for project-level statistics.
5. Open `/ad/logs` to inspect operation logs.

## Route Overview

### Public routes

- `/`
- `/login`
- `/logout`
- `/register`

### TA routes

- `/ta/enter`
- `/ta/home`
- `/ta/jobs`
- `/ta/myApplications`
- `/ta/notifications`
- `/ta/profile`
- `/ta/uploadCv`
- `/ta/apply`
- `/ta/application/reply`
- `/ta/conversations`
- `/ta/conversations/read`

### MO routes

- `/mo/home`
- `/mo/publish`
- `/mo/positions`
- `/mo/edit`
- `/mo/job/down`
- `/mo/job/delete`
- `/mo/job/applicants`
- `/mo/applications`
- `/mo/view/application`
- `/mo/application/action`
- `/mo/application/reply`
- `/mo/conversations`
- `/mo/conversations/read`

### Legacy compatibility routes

- `/motest/login`
- `/motest/logout`
- `/motest/register`
- `/mo/apply/list`
- `/mo/apply/update`

### Admin routes

- `/ad/home`
- `/ad/accounts`
- `/ad/accounts/action`
- `/ad/projects`
- `/ad/logs`
- `/ad/logs/record`

### File serving route

- `/uploads/*`

This route is handled by [CvFileServlet.java](/E:/Desktop/软工/software-engineering/src/main/java/com/bupt/tarecruit/controller/CvFileServlet.java:1). CV PDFs are served from `data/uploads/` and are only accessible to authenticated users.

## Data Storage

The application persists runtime state to local files:

- `data/users/`: user accounts and profile data
- `data/jobs/`: published positions
- `data/applications/`: TA applications
- `data/notifications/`: TA notifications
- `data/operation-logs/`: administrative or audit log entries

Typical naming conventions:

- users: `USER_<userId>.json`
- jobs: `JOB_<jobId>.json`
- applications: `<applicationId>_application.json`
- notifications: `NOTI_<id>.json`
- logs: `LOG_<id>.json`

## Authentication and Authorization

Authentication is handled by [LoginServlet.java](/E:/Desktop/软工/software-engineering/src/main/java/com/bupt/tarecruit/controller/LoginServlet.java:1) together with [AuthService.java](/E:/Desktop/软工/software-engineering/src/main/java/com/bupt/tarecruit/service/AuthService.java:1).

Role-based access control is enforced centrally by [AuthFilter.java](/E:/Desktop/软工/software-engineering/src/main/java/com/bupt/tarecruit/web/AuthFilter.java:1).

Current access policy:

- TA-only pages require role `TA`
- MO-only pages require role `MO`
- Admin-only pages require role `ADMIN`
- frozen users are blocked at authentication time

Legacy `motest` routes are still present for compatibility, but they are not the primary entry path for normal use.

## Testing

Run the full test suite with:

```powershell
.\mvnw.cmd test
```

The test suite currently includes:

- controller tests
- repository tests
- service tests
- acceptance-style report tests

At the time of writing, `.\mvnw.cmd test` completes with 70 passing tests on the current branch.

Key test files include:

- [LoginServletTest.java](/E:/Desktop/软工/software-engineering/src/test/java/com/bupt/tarecruit/controller/LoginServletTest.java:1)
- [RegisterServletTest.java](/E:/Desktop/软工/software-engineering/src/test/java/com/bupt/tarecruit/controller/RegisterServletTest.java:1)
- [LogoutServletTest.java](/E:/Desktop/软工/software-engineering/src/test/java/com/bupt/tarecruit/controller/LogoutServletTest.java:1)
- [TaModuleTestReportTest.java](/E:/Desktop/软工/software-engineering/src/test/java/com/bupt/tarecruit/acceptance/TaModuleTestReportTest.java:1)
- [AuthServiceTest.java](/E:/Desktop/软工/software-engineering/src/test/java/com/bupt/tarecruit/service/AuthServiceTest.java:1)
- [AdminServiceTest.java](/E:/Desktop/软工/software-engineering/src/test/java/com/bupt/tarecruit/service/AdminServiceTest.java:1)
- [ApplicationServiceTest.java](/E:/Desktop/软工/software-engineering/src/test/java/com/bupt/tarecruit/service/ApplicationServiceTest.java:1)
- [NotificationServiceTest.java](/E:/Desktop/软工/software-engineering/src/test/java/com/bupt/tarecruit/service/NotificationServiceTest.java:1)

## Development Conventions

- Do not add a database.
- Keep all persistent state in `data/`.
- Keep servlet classes focused on HTTP request handling.
- Put business rules in `service/`.
- Put file read/write logic in `repository/`.
- Do not hard-code final business data inside JSP or front-end scripts.
- When adding a new field, update the model class, repository handling, and seed JSON data together.

## Common Tasks

### Create a WAR package

```powershell
.\mvnw.cmd package
```

### Clean build artifacts

```powershell
.\mvnw.cmd clean
```

### Re-run only tests

```powershell
.\mvnw.cmd test
```

## Known Notes

- This is a coursework-style system built for demonstration and incremental development.
- Data files in `data/` act as the single source of truth.
- The UI is JSP-based and does not use a separate front-end framework.
- Some older MO testing routes remain in the codebase because the project evolved from an earlier MO-only workflow.

## Suggested Demo Order

If you need to present the project, this order works well:

1. Start the server and show the login page.
2. Sign in as `MO001` and publish or inspect positions.
3. Sign in as `TA001` and apply for a position.
4. Return to `MO001` to review and update the application.
5. Return to `TA001` to show status changes and notifications.
6. Sign in as `ADMIN001` to show account control and logs.
