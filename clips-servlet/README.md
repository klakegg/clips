# Clips Servlet

Project to simplify working with servlets using Guice without Jersey.

Sample Guice module:

```java
public class MyServletModule extends ClipsServletModule {

    @Override
    protected void configureServlets() {
        serve(MyServlet.class);
    }
}
```

Sample servlet:

```java
@Path("/my")
public class MyServlet extends ClipsServlet {

    @Override
    protected void configure() {
        // Generic
        registerGeneric(GET, ROOT, r -> r.output("Root"));
        // JSON (requires Gson in classpath)
        registerJson(GET, "/hello", r -> r.output("World!"));
    }
}
```