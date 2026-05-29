# Configuration & Externalized Settings Inventory

The project uses a small set of local configuration sources with environment-variable overrides for database connectivity.

## Configuration Sources

| Source | Type | Path/Location | Notes |
|---|---|---|---|
| Gradle build file | Build config | `build.gradle` | Declares plugins, Java target, and dependencies |
| Web deployment descriptor | Runtime config | `src/main/webapp/WEB-INF/web.xml` | Defines servlet registrations and URL mappings |
| Application properties | Runtime config | `src/main/resources/kyc.properties` | Provides default DB host/port/name/user/password |
| Environment variables | Externalized config | Process env (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`) | Override properties at runtime |

## Build Profiles

| Profile | Activation | Purpose | Key Dependencies/Plugins |
|---|---|---|---|
| default Gradle build | `gradle build` | Compile Java and package WAR | `java`, `war` plugins |

## Runtime Profiles

| Profile | Activation Method | Config Files | Key Overrides |
|---|---|---|---|
| Default runtime | Container startup | `web.xml`, `kyc.properties` | Env vars can override DB values |

## Properties Inventory

| Property Key | Default | Profiles | Source |
|---|---|---|---|
| db.host | sqlserver | Default | `kyc.properties` |
| db.port | 1433 | Default | `kyc.properties` |
| db.name | ZavaBankDB | Default | `kyc.properties` |
| db.user | sa | Default | `kyc.properties` |
| db.password | [MASKED] | Default | `kyc.properties` |
| DB_HOST | unset | Runtime override | Environment variable |
| DB_PORT | unset | Runtime override | Environment variable |
| DB_NAME | unset | Runtime override | Environment variable |
| DB_USER | unset | Runtime override | Environment variable |
| DB_PASSWORD | unset | Runtime override | Environment variable |

## Startup Parameters & Resource Requirements

| Service | JVM/Runtime Options | Memory | Instance Count |
|---|---|---|---|
| ZavaKYCService | Not specified in repository | Not specified | Not specified |

## Startup Dependency Chain

1. Servlet container starts application and loads `KycBootstrapServlet` on startup.
2. `KycBootstrapServlet` requires SQL Server connectivity to initialize tables.
3. API servlets become fully functional after successful bootstrap.

## Secrets & Sensitive Configuration

| Secret Reference | Type | Storage (masked) |
|---|---|---|
| db.password / DB_PASSWORD | Database password | Properties file default and/or env var ([MASKED]) |

### Secrets Provisioning Workflow

Secrets are expected to be provided either by `kyc.properties` defaults or by deployment-time environment variables. No managed secret store integration or automated secret rotation workflow is configured in this repository.

## Feature Flags

| Flag Name | Default | Controlled By |
|---|---|---|
| None detected | N/A | N/A |

## Framework & Runtime Versions

| Component | Version | Source |
|---|---|---|
| Java target compatibility | 1.8 | `build.gradle` |
| Servlet API | 4.0.1 | `build.gradle` |
| SQL Server JDBC driver | 12.6.3.jre8 | `build.gradle` |
| org.json | 20140107 | `build.gradle` |
| Gradle plugins | java, war | `build.gradle` |
