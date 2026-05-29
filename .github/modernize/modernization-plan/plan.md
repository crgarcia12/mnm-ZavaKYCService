# Modernization Plan: modernization-plan

**Project**: mnm-ZavaKYCService

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Java Servlet
- **Build Tool**: Gradle
- **Database**: Microsoft SQL Server
- **Key Dependencies**: javax.servlet-api, mssql-jdbc, org.json

---

## Overview

This modernization focuses on preparing the KYC service for secure Azure deployment.
The application currently uses local configuration-based credentials and contains
known dependency vulnerabilities. The modernization will:

- Move application credential handling to Azure-native secret management
- Remediate known CVEs and validate security posture before release
- Deploy the service to Azure Container Apps with managed identity

The migration follows a phased approach: code transformation, security
remediation, and deployment.

---

## Migration Impact Summary

| Application | Original Service | New Azure Service | Authentication | Comments |
|-------------|------------------|-------------------|----------------|----------|
| KYC Service | Local app config | Azure Key Vault | Managed Identity | Removes hardcoded secrets |
| KYC Service | VM/Servlet host | Azure Container Apps | Managed Identity | Default deployment target |

---

## Open Questions & Questionnaire

- [x] Q: Should the plan include environment/infrastructure provisioning? → A: No — focus on application modernization only.
- [x] Q: Should the plan include integration testing to verify migrated services? → A: No — integration testing was not explicitly requested.
- [x] Q: Should the plan include a security scan and CVE remediation task? → A: Yes — include default security/CVE remediation.
- [x] Q: Which Azure deployment target should the plan use? → A: Azure Container Apps (default).
