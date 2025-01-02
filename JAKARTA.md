Open questions:
- org/apache/sling/api/SlingConstants.java: Deprecate as jakarta servlet api provides them (Dispatcher)?
- org/apache/sling/api/servlets/OptingServlet.java: Should we deprecate this?
- org/apache/sling/api/wrappers/SlingRequestPaths.java: Constants can be deprecated, methods copied?

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
- org/apache/sling/api/servlets/ServletResolver.java: added new methods (Provider Type)
- org/apache/sling/api/servlets/SlingAllMethodsServlet.java: added new class SlingJakartaAllMethodsServlet
- org/apache/sling/api/servlets/SlingSafeMethodsServlet.java: added new class SlingJakartaSafeMethodsServlet
- org/apache/sling/api/uri/SlingUriBuilder.java: added new method: createFrom (Class)
- org/apache/sling/api/wrappers/ResourceResolverWrapper.java: added new methods from ResourceResolver (Class)
- org/apache/sling/api/wrappers/SlingHttpServletRequestWrapper.java: new class SlingJakartaHttpServletRequestWrapper
- org/apache/sling/api/wrappers/SlingHttpServletResponseWrapper.java: new class SlingJakartaHttpServletResponseWrapper

These files contain references to javax.servlet, but do not require an alternative:
- org/apache/sling/api/auth/NoAuthenticationHandlerException.java: only used in javadoc
- org/apache/sling/api/resource/Resource.java: only used in javadoc
- org/apache/sling/api/resource/ResourceProvider.java: Interface is deprecated, no change needed
- org/apache/sling/api/resource/ResourceDecorator.java: Method is deprecated, no change needed
- org/apache/sling/api/servlets/HtmlResponse.java: Class is deprecated, no change needed
- org/apache/sling/api/servlets/ServletResolverConstants.java: only used in javadoc

These are private classes and can be neglected for now:
- org/apache/sling/api/request/builder/impl/HttpSessionImpl.java
- org/apache/sling/api/request/builder/impl/ServletContextImpl.java
- org/apache/sling/api/request/builder/impl/SlingHttpServletRequestImpl.java
- org/apache/sling/api/request/builder/impl/SlingHttpServletResponseImpl.java

Comments/javadocs have not been adjusted!


