package net.klakegg.clips.servlet;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;

public abstract class ClipsServlet extends HttpServlet {

    protected static final String DELETE = "DELETE";
    protected static final String HEAD = "HEAD";
    protected static final String GET = "GET";
    protected static final String OPTIONS = "OPTIONS";
    protected static final String POST = "POST";
    protected static final String PUT = "PUT";
    protected static final String TRACE = "TRACE";

    private static Logger logger = LoggerFactory.getLogger(ClipsServlet.class);

    private Map<String, List<HandlerWrapper>> handlers = new HashMap<>();
    private String contextPath;

    @Inject
    private Injector injector;

    public ClipsServlet(String contextPath) {
        this.contextPath = contextPath;

        configure();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();

        if (!handlers.containsKey(method)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, String.format("Method '%s' not supported.", method));
            return;
        }

        if (GET.equals(method))
            doGet(req, resp);
        else
            perform(method, req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        perform(GET, req, resp);
    }

    protected void perform(String method, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = URLDecoder.decode(req.getRequestURI(), "UTF-8").replaceAll("/$", "");
        logger.info(uri);

        Optional<Request> request = handlers.get(method).stream()
                .map(w -> w.detect(uri, req, resp))
                .filter(r -> r != null)
                .findFirst();

        if (request.isPresent())
            request.get().handle();
        else
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Page not found.");
    }

    protected abstract void configure();

    protected void register(String method, String regex, Handler handler) {
        append(method, regex, handler);
    }

    protected void register(String method, String regex, Class<? extends Handler> handler) {
        append(method, regex, injector.getInstance(handler));
    }

    protected void registerJson(String method, String regex, JsonHandler handler) {
        append(method, regex, handler);
    }

    protected void registerGeneric(String method, String regex, GenericHandler handler) {
        append(method, regex, handler);
    }

    protected void append(String method, String regex, Handler handler) {
        if (!handlers.containsKey(method))
            handlers.put(method, new ArrayList<>());

        handlers.get(method).add(new HandlerWrapper(contextPath + regex, handler));
    }
}
