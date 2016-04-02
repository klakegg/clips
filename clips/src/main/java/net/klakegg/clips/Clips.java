package net.klakegg.clips;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.klakegg.clips.lang.ServiceException;
import net.klakegg.clips.module.ConfigModule;
import net.klakegg.clips.utils.Classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main object loading and handling modules and services.
 */
public class Clips {

    private Config config;
    private Injector injector;

    private Services services;

    /**
     *
     * @param metaModules Other modules adding for the meta injector.
     */
    public Clips(Module... metaModules) {
        loadConfig();
        loadInjector(metaModules);
    }

    /**
     * Loading configuration.
     */
    private void loadConfig() {
        // Load configuration using defaults
        config = ConfigFactory.load();

        // Override configuration if basename is defined in configuration.
        if (config.hasPath("clips.config"))
            config = ConfigFactory.load(config.getString("clips.config"));
    }

    /**
     * Loading the injector.
     *
     * @param otherModules Other modules adding for the meta injector.
     */
    private void loadInjector(Module... otherModules) {
        List<Module> metaModules = new ArrayList<>();
        metaModules.addAll(Stream.of(otherModules).collect(Collectors.toList()));
        metaModules.add(new ConfigModule(config));

        // Create meta injector used to initiate project modules
        Injector metaInjector = Guice.createInjector(metaModules);

        // Get all modules
        List<Module> modules = config.getObject("clips.modules").keySet().stream()
                // Remove modules turned off in configuration
                .filter(module -> "base".equals(module) || !config.hasPath("clips.plugin." + module) || config.getBoolean("clips.plugin." + module))
                // Fetch classes part of modules
                .map(module -> config.getStringList("clips.modules." + module))
                // Make it into a stream of strings
                .flatMap(Collection::stream)
                // Get the classes identified by class names
                .map(Classes::get)
                // Initiate module using meta injector
                .map(cls -> (Module) metaInjector.getInstance(cls))
                // Collect modules
                .collect(Collectors.toList());

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
                services = injector.getInstance(Services.class);
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
