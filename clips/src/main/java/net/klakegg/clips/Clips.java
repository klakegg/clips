package net.klakegg.clips;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.klakegg.clips.api.ClipsModule;
import net.klakegg.clips.lang.ServiceException;

import java.util.Set;

/**
 * Main object loading and handling modules and services.
 */
public class Clips {

    private Injector injector;

    private ClipsServices services;

    public Clips() {
        loadInjector();
    }

    private void loadInjector() {
        // Load configuration using defaults
        Config config = ConfigFactory.load();

        // Override configuration if basename is defined in configuration.
        if (config.hasPath("clips.config"))
            config = ConfigFactory.load(config.getString("clips.config"));

        // Create meta injector holder all modules defined in configuration using multibinder
        Injector metaInjector = Guice.createInjector(new BootstrapModule(config));

        // Get all modules
        Set<ClipsModule> modules = metaInjector.getInstance(ClipsHelper.class).getModules();

        // Create injector using the modules as defined
        injector = Guice.createInjector(modules);

        // Call GC.
        System.gc();
    }

    public Injector getInjector() {
        return injector;
    }

    /**
     * Start services registered in the injector.
     *
     * @throws Exception
     */
    public void startServices() throws Exception {
        try {
            if (services == null) {
                services = injector.getInstance(ClipsServices.class);
                services.start();
            }
        } catch (Exception e) {
            throw new ServiceException("Unable to load services.", e);
        }
    }

    /**
     * Stop services registered in the injector.
     */
    public void stopServices() {
        if (services != null) {
            services.stop();
            services = null;
        }
    }
}
