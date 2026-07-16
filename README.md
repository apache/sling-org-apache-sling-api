[![Apache Sling](https://sling.apache.org/res/logos/sling.png)](https://sling.apache.org)

&#32;[![Build Status](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-api/job/master/badge/icon)](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-api/job/master/)&#32;[![Test Status](https://img.shields.io/jenkins/tests.svg?jobUrl=https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-api/job/master/)](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-api/job/master/test/?width=800&height=600)&#32;[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=apache_sling-org-apache-sling-api&metric=coverage)](https://sonarcloud.io/dashboard?id=apache_sling-org-apache-sling-api)&#32;[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=apache_sling-org-apache-sling-api&metric=alert_status)](https://sonarcloud.io/dashboard?id=apache_sling-org-apache-sling-api)&#32;[![JavaDoc](https://www.javadoc.io/badge/org.apache.sling/org.apache.sling.api.svg)](https://www.javadoc.io/doc/org.apache.sling/org.apache.sling.api)&#32;[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.sling/org.apache.sling.api/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.apache.sling%22%20a%3A%22org.apache.sling.api%22) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling API

This module is part of the [Apache Sling](https://sling.apache.org) project.

The Sling API defines an extension to the Jakarta Servlet API 6.0 to provide access to content and unified access to request parameters, hiding differences between transfer methods from client to server.

The Sling API bundle does not include a Servlet API implementation. Servlet APIs must be provided by the runtime (Servlet container or another bundle). The module includes both `javax.servlet` and `jakarta.servlet` API dependencies with `provided` scope to support both API families.

## Build and test

Common Maven commands:

- `mvn clean install` - build and run tests
- `mvn clean install -DskipTests` - build without tests
- `mvn test` - run unit tests
- `mvn test -Dtest=SlingUriBuilderTest` - run a single test class
- `mvn spotless:check` - run formatting checks
- `mvn spotless:apply` - apply formatting
- `mvn verify` - run verification including OSGi baseline checks
- `mvn apache-rat:check` - verify license headers

## Updating to Sling API 3.x

Sling API 3.x adds Jakarta Servlet API support while keeping compatibility paths for existing `javax.servlet`-based usages. In most cases, upgrading is a drop-in replacement, with some migration considerations described below.

### Dependencies and runtime

Because the API now references Jakarta Servlet types in public signatures, most projects should add `jakarta.servlet:jakarta.servlet-api` (for example `6.1.0`) in addition to any existing `javax.servlet` dependency that is still needed.

At runtime, use a container that implements Jakarta Servlet API 6+ (for example Apache Felix Http Jetty 12).

### Minimum Java runtime

Sling API 3.x requires Java 17 at runtime.

### Source-code incompatibilities

Some APIs now have both `javax` and `jakarta` overloads. Passing `null` as an argument can cause ambiguous method resolution at compile time. These methods are not intended to accept `null`; update calling code accordingly instead of casting `null`.

## Migrating from Servlet API 3 to Jakarta Servlet API 6

Sling API 3.x uses Jakarta Servlet API as its base. Previous releases were based on `javax.servlet` (Servlet API 3). While compatibility APIs remain available, new code should target Jakarta types.

In most cases, migration is a search/replace exercise, but deprecated APIs may require explicit refactoring.

The following table lists common replacements:

| Feature | Servlet API 3 | Jakarta Servlet API 6 |
| ------- | -------------- | --------------------- |
| Package Prefix | `javax.servlet` | `jakarta.servlet` |
| Request interface | `SlingHttpServletRequest` | `SlingJakartaHttpServletRequest` |
| Response interface | `SlingHttpServletResponse` | `SlingJakartaHttpServletResponse` |
| Request event | `SlingRequestEvent` | `SlingJakartaRequestEvent` |
| Request listener | `SlingRequestListener` | `SlingJakartaRequestListener` |
| Request builder | `SlingHttpServletRequestBuilder.build()` | `SlingHttpServletRequestBuilder.buildJakartaRequest()` |
| Response builder | `SlingHttpServletResponseBuilder.build()` | `SlingHttpServletResponseBuilder.buildJakartaResponseResult()` |
| Response builder result | `SlingHttpServletResponseResult` | `SlingJakartaHttpServletResponseResult` |
| Media range | `MediaRangeList` | `JakartaMediaRangeList` |
| Error handling | `ErrorHandler` | `JakartaErrorHandler` |
| Opt-in servlet | `OptingServlet` | `JakartaOptingServlet` |
| Method servlets | `SlingSafeMethodsServlet` / `SlingAllMethodsServlet` | `SlingJakartaSafeMethodsServlet` / `SlingJakartaAllMethodsServlet` |
| Request/response wrappers | `SlingHttpServletRequestWrapper` / `SlingHttpServletResponseWrapper` | `SlingJakartaHttpServletRequestWrapper` / `SlingJakartaHttpServletResponseWrapper` |

Additional migration notes:

- `SlingBindings` and `SlingScriptHelper` provide Jakarta request/response methods (`getJakartaRequest()`, `getJakartaResponse()`, related setters/bindings).
- Wrapper APIs now consistently expose the wrapped object via `getWrappedObject()`.
