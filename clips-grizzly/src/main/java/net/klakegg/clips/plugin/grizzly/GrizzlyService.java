package net.klakegg.clips.plugin.grizzly;

import com.google.inject.Inject;
import com.google.inject.servlet.GuiceFilter;
import com.typesafe.config.Config;
import net.klakegg.clips.api.Service;
import net.klakegg.commons.sortable.Sort;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.FilterRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

@Sort(1000)
@SuppressWarnings("unused")
public class GrizzlyService implements Service {

    private static Logger logger = LoggerFactory.getLogger(GrizzlyService.class);

    private HttpServer httpServer;

    private Config config;

    @Inject
    public GrizzlyService(Config config) {
        this.config = config;
    }

    public void start() throws Exception {
        if (!SLF4JBridgeHandler.isInstalled()) {
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();
        }

        WebappContext webappContext = new WebappContext(config.hasPath("grizzly.name") ? config.getString("grizzly.name") : "GuiceContext");
        FilterRegistration filterRegistration = webappContext.addFilter("GuiceFilter", GuiceFilter.class);
        filterRegistration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), "/*");

        httpServer = HttpServer.createSimpleServer(".", config.hasPath("grizzly.port") ? config.getInt("grizzly.port") : 9000);
        webappContext.deploy(httpServer);

        httpServer.start();
    }

    public void stop() {
        httpServer.shutdownNow();
    }
}