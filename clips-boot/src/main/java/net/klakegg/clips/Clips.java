package net.klakegg.clips;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.klakegg.clips.module.ConfigModule;
import net.klakegg.clips.util.ConfigHelper;
import net.klakegg.clips.utils.Classes;
import net.klakegg.sortable.Sortables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main object loading and handling modules and services.
 */
public class Clips {

    private static Logger logger = LoggerFactory.getLogger(Clips.class);

    private Injector injector;
    private Optional<Services> services = Optional.empty();

    /**
     * @param metaModules Other modules added for the meta injector.
     */
    public Clips(Module... metaModules) {
        loadInjector(loadConfig(), metaModules);
    }

    /**
     * @param config Configuration to override automatic loaded configuration.
     * @param metaModules Other modules added for the meta injector.
     */
    public Clips(Config config, Module... metaModules) {
        loadInjector(config, metaModules);
    }

    /**
     * Loading configuration.
     */
    private Config loadConfig() {
        // Load configuration using defaults
        Config config = ConfigFactory.load();

        // Override configuration if basename is defined in configuration.
        if (config.hasPath("clips.config.basename"))
            config = ConfigFactory.load(config.getString("clips.config.basename"));

        return config;
    }

    /**
     * Loading the injector.
     *
     * @param otherModules Other modules added for the meta injector.
     */
    private void loadInjector(Config config, Module... otherModules) {
        // Create a ConfigHelper.
        ConfigHelper configHelper = new ConfigHelper(config);

        // Gather all modules for meta injector
        List<Module> metaModules = configHelper.getStringList("clips.pre.class").orElse(Collections.emptyList()).stream()
                // Get the classes identified by class names
                .map(Classes::get)
                // Initiate module without context
                .map(c -> (Module) Classes.instance(c))
                // Sort modules
                .sorted(Sortables.comparator())
                // Collect modules
                .collect(Collectors.toList());
        // Add modules provided by constructor.
        metaModules.addAll(Stream.of(otherModules).collect(Collectors.toList()));
        // Add ConfigModule.
        metaModules.add(new ConfigModule(config));

        // Create meta injector used to initiate project modules
        Injector metaInjector = Guice.createInjector(metaModules);

        // Initiate modules and initiate injector
        injector = Guice.createInjector(config.getObject("clips").keySet().stream()
                // Remove plugin "pre".
                .filter(plugin -> !"pre".equals(plugin))
                // Remove modules turned off in configuration
                .filter(plugin -> "core".equals(plugin) || configHelper.getBoolean("clips." + plugin + ".enabled").orElse(true))
                // Fetch classes part of modules
                .map(plugin -> configHelper.getStringList("clips." + plugin + ".class").orElse(Collections.emptyList()))
                // Make it into a stream of strings
                .flatMap(Collection::stream)
                // Get the classes identified by class names
                .map(Classes::get)
                // Initiate module using meta injector
                .map(cls -> (Module) metaInjector.getInstance(cls))
                // Sort modules
                .sorted(Sortables.comparator())
                // Logging for debug
                .peek(m -> logger.debug("Module '{}'", m.getClass()))
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
     * Returns an instance of the requested class type from injector.
     */
    @SuppressWarnings("unused")
    public <T> T getInstance(Class<T> cls) {
        return injector.getInstance(cls);
    }

    /**
     * Start services registered in the injector.
     */
    public void startServices() {
        if (!services.isPresent()) {
            services = Optional.of(injector.getInstance(Services.class));
            services.ifPresent(Services::start);
            logger.info("Ready");
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
