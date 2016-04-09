package net.klakegg.clips.plugin.grizzly;

import com.google.inject.servlet.GuiceFilter;
import net.klakegg.clips.annotation.Configuration;
import net.klakegg.clips.api.Service;
import net.klakegg.clips.lang.ServiceException;
import net.klakegg.commons.sortable.Sort;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.FilterRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.servlet.DispatcherType;
import java.io.IOException;
import java.util.EnumSet;

@Sort(1000)
@SuppressWarnings("all")
public class GrizzlyService implements Service {

    private static Logger logger = LoggerFactory.getLogger(GrizzlyService.class);

    private HttpServer httpServer;

    @Configuration("grizzly.name")
    private String name = "GuiceContext";
    @Configuration("grizzly.port")
    private int port = 9000;

    public void start() {
        if (!SLF4JBridgeHandler.isInstalled()) {
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();
        }

        WebappContext webappContext = new WebappContext(name);
        FilterRegistration filterRegistration = webappContext.addFilter("GuiceFilter", GuiceFilter.class);
        filterRegistration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), "/*");

        httpServer = HttpServer.createSimpleServer(".", port);
        webappContext.deploy(httpServer);

        try {
            httpServer.start();
        } catch (IOException e) {
            throw new ServiceException("Unable to start Grizzly.", e);
        }
    }

    public void stop() {
        httpServer.shutdownNow();
    }
}