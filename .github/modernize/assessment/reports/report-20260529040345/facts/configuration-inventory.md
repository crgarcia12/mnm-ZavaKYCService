# Configuration & Externalized Settings Inventory

This project uses a compact configuration model based on Gradle build settings, servlet XML mappings, and a properties-plus-environment-variable approach for runtime database settings. Secrets are partially externalized through environment variables but a default password value exists in repository configuration.

## Configuration Sources

| Source | Type | Path/Location | Notes |
|---|---|---|---|
| build.gradle | Build config | `/build.gradle` | Dependencies and WAR packaging |
| settings.gradle | Build config | `/settings.gradle` | Root project name |
| web.xml | Servlet config | `/src/main/webapp/WEB-INF/web.xml` | Servlet declarations and URL mappings |
| kyc.properties | Runtime properties | `/src/main/resources/kyc.properties` | Default DB host/port/name/user/password |
| Environment variables | Externalized runtime config | `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` | Overrides property defaults |
| Dockerfile | Container runtime config | `/Dockerfile` | Build and runtime container definitions |

## Build Profiles

| Profile | Activation | Purpose | Key Dependencies/Plugins |
|---|---|---|---|
| default | Automatic | Build WAR artifact | `war` plugin, servlet API, SQL Server JDBC, JSON library |

## Runtime Profiles

| Profile | Activation Method | Config Files | Key Overrides |
|---|---|---|---|
| default | Automatic servlet startup | `kyc.properties` | Base DB connection values |
| env-override | Environment variables | Runtime process environment | Overrides all DB properties when set |

## Properties Inventory

| Property Key | Default | Profiles | Source |
|---|---|---|---|
| db.host | localhost | default | kyc.properties |
| db.port | 1433 | default | kyc.properties |
| db.name | ZavaKyc | default | kyc.properties |
| db.user | sa | default | kyc.properties |
| db.password | [MASKED] | default | kyc.properties |
| DB_HOST | unset | env-override | Environment variable |
| DB_PORT | unset | env-override | Environment variable |
| DB_NAME | unset | env-override | Environment variable |
| DB_USER | unset | env-override | Environment variable |
| DB_PASSWORD | unset | env-override | Environment variable |

## Startup Parameters & Resource Requirements

| Service | JVM/Runtime Options | Memory | Instance Count |
|---|---|---|---|
| ZavaKYCService | No explicit JVM args defined in repo | Not specified | Not specified |

## Startup Dependency Chain

1. Servlet container starts WAR deployment.
2. `KycBootstrapServlet` initializes tables and seed watchlist row.
3. API servlets become available after bootstrap initialization completes.

## Secrets & Sensitive Configuration

| Secret Reference | Type | Storage (masked) |
|---|---|---|
| `db.password` / `DB_PASSWORD` | Database password | Property file default plus environment override |
| `db.user` / `DB_USER` | Database user credential | Property file default plus environment override |

### Secrets Provisioning Workflow

Secrets can be injected by environment variables at runtime, which override values from `kyc.properties`. The repository also contains fallback credentials in `kyc.properties`, so deployment pipelines should supply real secrets externally and avoid using checked-in defaults.

## Feature Flags

No feature flag framework or conditional feature toggle configuration was detected.

## Framework & Runtime Versions

| Component | Version | Source |
|---|---|---|
| Java target compatibility | 1.8 | build.gradle |
| Servlet API | 4.0.1 | build.gradle |
| SQL Server JDBC driver | 8.4.1.jre8 | build.gradle |
| JSON library | 20231013 | build.gradle |
| Gradle (local runner) | 9.5.1 | baseline command output |
| Container runtime | Tomcat 9 with JDK8 base | Dockerfile |
