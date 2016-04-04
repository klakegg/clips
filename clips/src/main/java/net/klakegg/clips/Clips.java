package net.klakegg.clips;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.klakegg.clips.module.ConfigModule;
import net.klakegg.clips.utils.Classes;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main object loading and handling modules and services.
 */
public class Clips {

    private Config config;
    private Injector injector;
    private Optional<Services> services = Optional.empty();

    /**
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
        if (config.hasPath("clips.config.basename"))
            config = ConfigFactory.load(config.getString("clips.config.basename"));
    }

    /**
     * Loading the injector.
     *
     * @param otherModules Other modules added for the meta injector.
     */
    private void loadInjector(Module... otherModules) {
        // Gather all modules for meta injector
        List<Module> metaModules = Stream.of(otherModules).collect(Collectors.toList());
        metaModules.add(new ConfigModule(config));

        // Create meta injector used to initiate project modules
        Injector metaInjector = Guice.createInjector(metaModules);

        // Initiate modules and initiate injector
        injector = Guice.createInjector(config.getObject("clips").keySet().stream()
                // Remove modules turned off in configuration
                .filter(plugin -> "core".equals(plugin) || !config.hasPath("clips." + plugin + ".enabled") || config.getBoolean("clips." + plugin + ".enabled"))
                // Fetch classes part of modules
                .map(plugin -> config.getStringList("clips." + plugin + ".class"))
                // Make it into a stream of strings
                .flatMap(Collection::stream)
                // Get the classes identified by class names
                .map(Classes::get)
                // Initiate module using meta injector
                .map(cls -> (Module) metaInjector.getInstance(cls))
                // Collect modules
                .collect(Collectors.toList()));
    }

    /**
     * Returns the Injector for use other places.
     *
     * @return Current injector.
     */
    @SuppressWarnings("unused")
    public Injector getInjector() {
        return injector;
    }

    /**
     * Start services registered in the injector.
     */
    public void startServices() {
        if (!services.isPresent()) {
            services = Optional.of(injector.getInstance(Services.class));
            services.ifPresent(Services::start);
        }
    }

    /**
     * Stop services registered in the injector.
     */
    public void stopServices() {
        services.ifPresent(Services::stop);
        services = Optional.empty();
    }
}
