These files have been changed/copied:
- org/apache/sling/api/SlingHttpServletRequest.java: new interface org/apache/sling/api/SlingJakartaHttpServletRequest.java (Provider Type)
- org/apache/sling/api/SlingHttpServletResponse.java: new interface org/apache/sling/api/SlingJakartaHttpServletResponse.java (Provider Type)
- org/apache/sling/api/SlingServletException.java: added new constructor (Class)
- org/apache/sling/api/auth/Authenticator.java: added new methods: login, logout (Provider Type)
- org/apache/sling/api/request/RequestUtil.java: added new methods (Class)
- org/apache/sling/api/request/SlingRequestEvent.java: new interface org/apache/sling/api/request/SlingJakartaRequestEvent.java (marked as Consumer Type, but should be Provider Type)
- org/apache/sling/api/request/SlingRequestListener.java: new interface org/apache/sling/api/request/SlingJakartaRequestListener.java (Consumer Type)
- org/apache/sling/api/request/builder/SlingHttpServletRequestBuilder.java: added new methods (Class)
- org/apache/sling/api/request/builder/SlingHttpServletResponseBuilder.java: added new methods (Class)
- org/apache/sling/api/request/builder/SlingHttpServletResponseResult.java: new interface org/apache/sling/api/request/builder/SlingJakartaHttpServletResponseResult.java (Provider Type)
- org/apache/sling/api/request/header/MediaRangeList.java: new class org/apache/sling/api/http/MediaRangeList.java (Class)
- org/apache/sling/api/resource/ResourceResolver.java: added new methods: resolve, map (Provider Type)
- org/apache/sling/api/resource/mapping/ResourceMapper.java: added new methods: getMapping, getAllMappings (Provider Type)
- org/apache/sling/api/scripting/SlingBindings.java: New class SlingJakartaBindings
- org/apache/sling/api/scripting/SlingScript.java: New class SlingJakartaScript
- org/apache/sling/api/scripting/SlingScriptHelper.java: New class SlingJakartaScriptHelper
- org/apache/sling/api/servlets/ErrorHandler.java: added new interface JakartaErrorHandler (Consumer Type)
- org/apache/sling/api/servlets/OptingServlet.java: added new interface JakartaOptinServlet (Consumer Type)
- org/apache/sling/api/servlets/ServletResolver.java: added new methods (Provider Type)
- org/apache/sling/api/servlets/SlingAllMethodsServlet.java: added new class SlingJakartaAllMethodsServlet
- org/apache/sling/api/servlets/SlingSafeMethodsServlet.java: added new class SlingJakartaSafeMethodsServlet
- org/apache/sling/api/uri/SlingUriBuilder.java: added new method: createFrom (Class)
- org/apache/sling/api/wrappers/ResourceResolverWrapper.java: added new methods from ResourceResolver (Class)
- org/apache/sling/api/wrappers/SlingHttpServletRequestWrapper.java: new class SlingJakartaHttpServletRequestWrapper
- org/apache/sling/api/wrappers/SlingHttpServletResponseWrapper.java: new class SlingJakartaHttpServletResponseWrapper
- org/apache/sling/api/wrappers/SlingRequestPaths.java: deprecated constants, added new methods (Class)

These files contain references to javax.servlet, but do not require an alternative:
- org/apache/sling/api/SlingConstants.java: Deprecated
- org/apache/sling/api/auth/NoAuthenticationHandlerException.java: only used in javadoc
- org/apache/sling/api/resource/Resource.java: only used in javadoc
- org/apache/sling/api/resource/ResourceProvider.java: Interface is deprecated, no change needed
- org/apache/sling/api/resource/ResourceDecorator.java: Method is deprecated, no change needed
- org/apache/sling/api/servlets/HtmlResponse.java: Class is deprecated, no change needed
- org/apache/sling/api/servlets/ServletResolverConstants.java: only used in javadoc

These are private classes and are refactored to support javax.servlet as well as jakarta.servlet:
- org/apache/sling/api/request/builder/impl/HttpSessionImpl.java
- org/apache/sling/api/request/builder/impl/ServletContextImpl.java
- org/apache/sling/api/request/builder/impl/SlingHttpServletRequestImpl.java
- org/apache/sling/api/request/builder/impl/SlingHttpServletResponseImpl.java

Comments/javadocs have not been adjusted!


## Implementation

### Done (in branches)

- New methods in ResourceResolver (resourceresolver)
- New methods in ResourceMapper (resourceresolver)
- New methods in Authenticator (Auth Core)
- New methods in Servlet Resolver (servlets.resolver) (first version, based on javax.servlet)

### Open

- Support for SlingConstants.ATTR_REQUEST_JAKARTA_SERVLET (next to ATTR_REQUEST_SERVLET)
- Request attributes for include/forward (mapping should be handled by Apache Felix Http Wrappers)
- Request attributes for error handling (mapping should be handled by Apache Felix Http Wrappers)
- Support for SlingJakartaRequestListener and SlingJakartaRequestEvent (next to existing ones) (Sling Engine)
- Support for SlingJakartaBindings (next to SlingBindings)
- Support for SlingJakartaScript (next to SlingScript)
- Support for SlingJakartaScriptHelper (next to SlingScriptHelper)
- Support for JakartaErrorHandler (next to ErrorHandler) (Sling Engine)
- Support for JakartaOptingServlet (next to OptingServlet)
- Support for using Jakarta Servlet API for Sling Servlets
- Support for using Jakarta Servlet API for Sling Filters
