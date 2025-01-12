[![Apache Sling](https://sling.apache.org/res/logos/sling.png)](https://sling.apache.org)

&#32;[![Build Status](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-api/job/master/badge/icon)](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-api/job/master/)&#32;[![Test Status](https://img.shields.io/jenkins/tests.svg?jobUrl=https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-api/job/master/)](https://ci-builds.apache.org/job/Sling/job/modules/job/sling-org-apache-sling-api/job/master/test/?width=800&height=600)&#32;[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=apache_sling-org-apache-sling-api&metric=coverage)](https://sonarcloud.io/dashboard?id=apache_sling-org-apache-sling-api)&#32;[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=apache_sling-org-apache-sling-api&metric=alert_status)](https://sonarcloud.io/dashboard?id=apache_sling-org-apache-sling-api)&#32;[![JavaDoc](https://www.javadoc.io/badge/org.apache.sling/org.apache.sling.api.svg)](https://www.javadoc.io/doc/org.apache.sling/org.apache.sling.api)&#32;[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.sling/org.apache.sling.api/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.apache.sling%22%20a%3A%22org.apache.sling.api%22) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling API

This module is part of the [Apache Sling](https://sling.apache.org) project.

The Sling API defines an extension to the Jakarta Serlvet API 6.0 to
provide access to content and unified access to request
parameters hiding the differences between the different methods
of transferring parameters from client to server. Note that the
Sling API bundle does not include the Servlet API but instead
requires the API to be provided by the Servlet container in
which the Sling framework is running or by another bundle.

## Updating to Sling API 3

Sling API 3 adds support for Jakarta Servlet API. Updating to the latest API version should just be a drop-in replacement. However, there are some points to considers.

### Dependency to Jakarta Servlet API

As the API references the Jakarta Servlet API in several signatures, it is very likely that you need to add the Jakarta Servlet API to the dependency list of your project. And that is in addition to the already existing dependency to the Javax Servlet API.

Add a dependency like `jakarta.servlet:jakarta.servlet-api:6.0.0` to your project.

At runtime, you need a container implementing the Jakarta Servlet API. For example, you can use Apache Felix Http Jetty 12.

### Minimum Java Runtime

As the Jakarta Servlet API requires Java 17, the Sling API now requires Java 17 as a minimum Java version at runtime as well.

### Source Code Incompatibilties

For some methods where a Javax Servlet Type is used in the signature, an alternative method has been added with the same name but different signature. This can lead to compilation errors due to disambigutie *if* the actual argument is `null`. However, all of these methods to not allow `null` as an argument, therefore this should not create a problem in reality.

If such compilation error occurs nevertheless, a quick (but wrong) fix is to cast `null` to the Jakarta Servlet API type. However, the correct fix is to not call this method with a `null` argument at all and change the code accordingly.

## Migrating from Servlet API 3 to Jakarta Servlet API 6

With the release of Sling API Version 3, the Jakarta Servlet API is used as the base. Previous releases are based on the Javax Servlet API 3. While the API based on Servlet 3 is still available and functional, it is mainly there for compatiblity. All new code should leverage Jakarta Servelt API and it is advised to migrate existing code.

In most cases, the migration is as simply as a series of search and replace operations. For Sling API based on Servlet 3 there is an alternative with the same functionality based on Jakarta Servelt API. However, for API that has been deprecated already, there is no alternative. Therefore, all usage of deprecated Sling API needs to be replaced first.

The following table lists the replacements:

| Feature | Servlet API 3 | Jakarta Servlet API 6 |
| ------- | -------------- | --------------------- |
| Package Prefix | `javax.servlet` | `jakarta.servlet` |
| Request Interface | SlingHttpServletRequest | SlingJakartaHttpServletRequest |
| Response Interface | SlingHttpServletResponse | SlingJakartaHttpServletResponse |
| Event Interface | SlingRequestEvent | SlingJakartaRequestEvent |
| Event Listener | SlingRequestListener | SlingJakartaRequestListener |
| Builders | SlingHttpServletRequestBuilder.build() | SlingHttpServletRequestBuilder.buildJakartaRequest() |
| Builders | SlingHttpServletResponseBuilder.build() | SlingHttpServletResponseBuilder.buildJakartaResponse() |
| Builders | SlingHttpServletResponseResult | SlingJakartaHttpServletResponseResult |
| Media Range | MediaRangeList | JakartaMediaRangeList |
| Scripting | SlingBindings | SlingJakartaBindings |
| Scripting | SlingScript | SlingJakartaScript |
| Scripting | SlingScriptHelper | SlingJakartaScriptHelper |
| Error Handling | ErrorHandler | JakartaErrorHandler |
| Scripting | OptingServlet | JakartaOptinServlet |
| Scripting | SlingAllMethodsServlet | SlingJakartaAllMethodsServlet |
| Scripting | SlingSafeMethodsServlet | SlingJakartaSafeMethodsServlet |
| Wrapper | SlingHttpServletRequestWrapper | SlingJakartaHttpServletRequestWrapper |
| Wrapper | SlingHttpServletResponseWrapper | SlingJakartaHttpServletResponseWrapper |
