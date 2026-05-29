# Architecture Diagram

This service is a Java Servlet-based web application that exposes KYC APIs and persists verification data in SQL Server.

## Application Architecture

```mermaid
flowchart TD
    subgraph Client["Client Layer"]
        Browser["Browser or API Client"]
    end
    subgraph App["Application Layer - Java Servlet"]
        JSP["index.jsp"]
        API["KYC Servlets"]
        Config["KycConfig"]
    end
    subgraph Data["Data Layer"]
        JDBC["JDBC Driver"]
        SQL[("SQL Server")]
    end

    Browser -->|"HTTP requests"| JSP
    Browser -->|"REST calls"| API
    API -->|"read config"| Config
    API -->|"execute SQL"| JDBC
    JDBC -->|"T-SQL queries"| SQL
```

### Technology Stack Summary

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| Presentation | JSP / Servlet API | Servlet 4.0 | Expose health page and KYC HTTP endpoints |
| Application | Java | 8 target | Request handling and KYC decision logic |
| Data Access | JDBC (mssql-jdbc) | 12.6.3.jre8 | SQL Server connectivity |
| Build | Gradle (java, war) | N/A | Build WAR artifact |

### Data Storage & External Services

The service stores watchlist and verification records in SQL Server tables (`KYCWatchlist`, `KYCVerification`). No additional message broker, cache, or external API integrations were detected.

### Key Architectural Decisions

- Uses classic servlet mappings in `web.xml` instead of annotation-driven controllers.
- Initializes required database tables on startup via `KycBootstrapServlet`.
- Keeps infrastructure simple with direct JDBC access and no ORM layer.

## Component Relationships

```mermaid
flowchart LR
    subgraph Presentation["Presentation"]
        Health["HealthServlet"]
        Verify["KycVerifyServlet"]
        Status["KycStatusServlet"]
    end
    subgraph Business["Business Logic"]
        Bootstrap["KycBootstrapServlet"]
    end
    subgraph DataAccess["Data Access"]
        ConnFactory["KycConnectionFactory"]
        Cfg["KycConfig"]
        DB[("SQL Server")]
    end

    Verify -->|"open DB connection"| ConnFactory
    Status -->|"open DB connection"| ConnFactory
    Bootstrap -->|"open DB connection"| ConnFactory
    ConnFactory -->|"read credentials"| Cfg
    ConnFactory -->|"JDBC"| DB
```

### Component Inventory

| Component | Layer | Type | Responsibility |
|---|---|---|---|
| HealthServlet | Presentation | Servlet | Returns application liveness HTML |
| KycVerifyServlet | Presentation | Servlet | Accepts verification requests and writes results |
| KycStatusServlet | Presentation | Servlet | Reads latest verification status by customer |
| KycBootstrapServlet | Business Logic | Startup Servlet | Creates required tables and seed watchlist row |
| KycConnectionFactory | Data Access | Utility | Creates SQL Server JDBC connections |
| KycConfig | Data Access | Config Utility | Resolves DB settings from env vars or properties |
