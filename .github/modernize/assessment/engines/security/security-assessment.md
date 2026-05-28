# Security Assessment Report

**Generated:** 2026-05-28T23:16:00Z

## Summary

| Metric | Count |
|--------|-------|
| Total Findings | 8 |
| CVE Vulnerabilities | 2 |
| CWE Vulnerabilities | 6 |
| Total Rules Assessed | 59 |
| Rules Passed | 53 |

### By Severity

| Severity | Count |
|----------|-------|
| mandatory | 2 |
| optional | 3 |
| potential | 3 |

### By Category

| Category | Count |
|----------|-------|
| CVE | 2 |
| Code Quality | 3 |
| Credentials & Secrets | 3 |

## CVE Findings (Dependency Vulnerabilities)

### CVE-2023-5072: Java: DoS Vulnerability in JSON-JAVA
- **Severity:** mandatory
- **Story Points:** 1
- **Files:** build.gradle:17

[CVE-2023-5072](https://github.com/advisories/GHSA-4jq9-2xhw-jpx7): Java: DoS Vulnerability in JSON-JAVA

Severity: HIGH

Affected dependencies:
  - org.json:json:20140107 (declared at build.gradle:17)

The parser bug can be used to circumvent a check that prevents a key in a JSON object from being another JSON object. By nesting JSON objects, an attacker can trigger exponential memory usage, causing OutOfMemoryError.

Recommended fix:
  - Upgrade org.json:json to 20231013 or later

---

### CVE-2022-45688: json stack overflow vulnerability
- **Severity:** mandatory
- **Story Points:** 1
- **Files:** build.gradle:17

[CVE-2022-45688](https://github.com/advisories/GHSA-3vqj-43w4-2q58): json stack overflow vulnerability

Severity: HIGH

Affected dependencies:
  - org.json:json:20140107 (declared at build.gradle:17)

A stack overflow in the XML.toJSONObject component allows attackers to cause a Denial of Service (DoS) via crafted JSON or XML data.

Recommended fix:
  - Upgrade org.json:json to 20230227 or later

---

## CWE Findings (Code-Level Vulnerabilities)

### CWE-477: Use of Obsolete Function
- **Category:** Code Quality
- **Severity:** optional
- **Story Points:** 1
- **Files:** src/main/java/com/zavabank/kycservice/KycConnectionFactory.java

In KycConnectionFactory (line 10), `Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")` is used to manually register the JDBC driver. Since JDBC 4.0 (Java SE 6), drivers are auto-registered via the ServiceLoader mechanism, making this manual registration obsolete and unnecessary.

---

### CWE-772: Missing Release of Resource after Effective Lifetime
- **Category:** Code Quality
- **Severity:** potential
- **Story Points:** 3
- **Files:** src/main/java/com/zavabank/kycservice/KycConfig.java

In the KycConfig static initializer (lines 12-15), the InputStream opened via `getResourceAsStream()` is closed on line 15, but not inside a `finally` block. If `PROPERTIES.load(inputStream)` throws an IOException, the stream will never be closed, resulting in a resource leak.

---

### CWE-775: Missing Release of File Descriptor or Handle after Effective Lifetime
- **Category:** Code Quality
- **Severity:** potential
- **Story Points:** 3
- **Files:** src/main/java/com/zavabank/kycservice/KycConfig.java

In the KycConfig static initializer (lines 12-15), the InputStream file handle is closed on line 15, but only if no exception occurs during `PROPERTIES.load(inputStream)`. Because `close()` is not in a `finally` block, the underlying file descriptor can be leaked if an exception is thrown.

---

### CWE-259: Use of Hard-coded Password
- **Category:** Credentials & Secrets
- **Severity:** optional
- **Story Points:** 5
- **Files:** src/main/resources/kyc.properties

The file `src/main/resources/kyc.properties` (line 5) contains the hard-coded database password. While `KycConfig` reads environment variables first, this default password is shipped in the packaged artifact and exposes the SQL Server credentials if the environment variable `DB_PASSWORD` is not set.

---

### CWE-778: Insufficient Logging
- **Category:** Credentials & Secrets
- **Severity:** potential
- **Story Points:** 3
- **Files:** src/main/java/com/zavabank/kycservice/KycVerifyServlet.java, src/main/java/com/zavabank/kycservice/KycStatusServlet.java

No logging framework is used anywhere in the codebase. Security-critical events are silently discarded: `KycVerifyServlet.doPost()` swallows `SQLException` without logging, and KYC verification outcomes (PASS/REVIEW) are never recorded in any audit log. Without logging, security incidents, errors, and compliance-relevant KYC decisions cannot be audited or monitored.

---

### CWE-798: Use of Hard-coded Credentials
- **Category:** Credentials & Secrets
- **Severity:** optional
- **Story Points:** 5
- **Files:** src/main/resources/kyc.properties

The file `src/main/resources/kyc.properties` (lines 4-5) contains hard-coded database credentials: `db.user=sa` and a hard-coded password. These credentials are bundled into the WAR artifact and fall back to these values when `DB_USER` and `DB_PASSWORD` environment variables are not provided, exposing the SQL Server sa account password in the source repository and the packaged application.
