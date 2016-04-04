package net.klakegg.clips.plugin.grizzly;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import net.klakegg.clips.api.PluginModule;
import net.klakegg.clips.api.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrizzlyModule extends PluginModule {

    private static Logger logger = LoggerFactory.getLogger(GrizzlyModule.class);

    private Config config;

    @Inject
    public GrizzlyModule(Config config) {
        this.config = config;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void configure() {
        try {
            Class<? extends Service> serviceClass = GrizzlyService.class;
            if (config.hasPath("grizzly.impl"))
                serviceClass = (Class<? extends Service>) Class.forName(config.getString("grizzly.impl"));

            multibinder(Service.class).to(serviceClass).in(Singleton.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}