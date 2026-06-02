# Project Overview

Apache Sling API (`org.apache.sling.api`) is an OSGi bundle that extends the Jakarta Servlet API 6.0 to define the core Sling programming model. It provides interfaces for resource resolution (`Resource`, `ResourceResolver`), Sling-specific HTTP request/response (`SlingHttpServletRequest`, `SlingJakartaHttpServletRequest`), adaptables, scripting, URI handling, and servlet registration. This is a pure API bundle — no runtime implementations are included. Java 17 is required. The bundle is built with Maven and packaged using bnd.

# Core Commands

- **Build:** `mvn clean install`
- **Build (skip tests):** `mvn clean install -DskipTests`
- **Run full test suite:** `mvn test`
- **Run a single test class:** `mvn test -Dtest=SlingUriBuilderTest`
- **Lint / code style (Spotless):** `mvn spotless:check`
- **Apply Spotless formatting:** `mvn spotless:apply`
- **OSGi baseline check:** `mvn verify` (runs `bnd-baseline-maven-plugin`)
- **License header check:** `mvn apache-rat:check`

No dev server — this is a library bundle.

# Project Layout

```
pom.xml                          Maven build descriptor
bnd.bnd                          OSGi bundle manifest overrides
src/
  main/java/org/apache/sling/
    api/                         Core Sling API interfaces and exceptions
      adapter/                   Adaptable / AdapterFactory contracts
      auth/                      Authentication info interfaces
      request/                   Request parameter and dispatcher APIs
      resource/                  Resource, ResourceResolver, ResourceFactory
      scripting/                 ScriptHelper and scripting support
      security/                  PermissionInfo
      servlets/                  SlingSafeMethodsServlet, SlingAllMethodsServlet, helper types
      uri/                       SlingUri / SlingUriBuilder
      wrappers/                  Decorator wrappers for request, response, resource
    spi/resource/                SPI interfaces for ResourceProvider implementations
  main/resources/                Static resources (e.g., HtmlResponse.html)
  test/java/org/apache/sling/
    api/                         Unit tests mirroring main package structure
target/                          Build output (ignored by version control)
```

# Development Patterns & Constraints

- **Java version:** 17 (set via `sling.java.version` property).
- **Code style:** Enforced by Spotless (configured in parent POM). Run `mvn spotless:apply` before committing.
- **API compatibility:** Every public/protected interface and class change must be backward-compatible or accompanied by a semantic version bump. The `bnd-baseline-maven-plugin` enforces this automatically during `mvn verify`.
- **OSGi versioning:** Package versions are declared in `package-info.java` files using `@Version`. Increment according to OSGi semantic versioning rules (major = breaking, minor = new API, micro = bugfix/doc).
- **No implementations:** This bundle defines contracts only. Do not add runtime logic beyond what is necessary to fulfill an interface default method or utility helper directly specified by the API.
- **`javax.jcr` dependency is optional** — declared as `resolution:=optional` in `bnd.bnd`. Do not make JCR types mandatory.
- **Nullability:** Use `@NotNull` / `@Nullable` from `org.jetbrains.annotations` on all public method signatures.
- **License headers:** All `.java` files must carry the Apache 2.0 license header. Checked by `apache-rat-plugin`.

# Git Workflow

- Default branch: `master`
- Feature branches: `maia/issue-<jira-id>-<suffix>` or descriptive names; no strict enforced prefix beyond convention.
- Commit messages: Reference the JIRA issue where applicable — e.g., `SLING-12345 Short description of change`.
- PRs target `master`. CI runs via Jenkins (`Jenkinsfile` at repo root).
- Do not push directly to `master`; use PRs for review.

# Testing Guidelines

- **Framework:** JUnit 4 (`junit:junit`) + Mockito + Hamcrest.
- **Test location:** `src/test/java/` mirroring the package of the class under test.
- **Naming:** `<ClassName>Test.java`.
- **Run all tests:** `mvn test`
- **Run one test:** `mvn test -Dtest=SlingUriTest`
- **Run one method:** `mvn test -Dtest=SlingUriTest#testParse`
- **Surefire reports:** `target/surefire-reports/`
- Tests are unit tests only — no container or integration test setup exists in this module.

# Gotchas

- Both `javax.servlet` and `jakarta.servlet` APIs are on the compile classpath. `SlingHttpServletRequest` wraps `javax.servlet`; `SlingJakartaHttpServletRequest` wraps `jakarta.servlet`. Do not conflate the two hierarchies.
- The `bnd-baseline-maven-plugin` will fail the build if a public API change is made without a corresponding package version bump in `package-info.java`. Always update `@Version` when adding or changing public members.
- The parent POM (`sling-bundle-parent`) controls most plugin versions and default configurations. Avoid overriding plugin versions locally unless strictly necessary.
- `HtmlResponse.html` in `src/main/resources` is intentionally excluded from RAT license checking (see `pom.xml` exclusion list).
- Spotless failures block the build. If CI fails with a formatting error, run `mvn spotless:apply` locally and commit the result.

# Security

<!-- sling-security-default:start -->
The threat model for this project is https://github.com/apache/sling/blob/master/docs/threat-model.md .
<!-- sling-security-default:end -->

