package net.klakegg.clips;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.typesafe.config.Config;
import net.klakegg.clips.api.ClipsModule;
import net.klakegg.clips.utils.Classes;

import java.util.Collection;

/**
 * Special module used to prepare modules for the injector we want to make.
 */
class BootstrapModule extends AbstractModule {

    private Config config;

    // This is the only module truly outside the Injector.
    public BootstrapModule(Config config) {
        this.config = config;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void configure() {
        // Configuration
        bind(Config.class).toInstance(config);

        // Initiate modules
        Multibinder<ClipsModule> modules = Multibinder.newSetBinder(binder(), ClipsModule.class);

        // Load modules from configuration
        config.getObject("clips.modules").keySet().stream()
                // Remove modules turned off in configuration
                .filter(module -> "base".equals(module) || !config.hasPath("clips.plugin." + module) || config.getBoolean("clips.plugin." + module))
                // Fetch classes part of modules
                .map(module -> config.getStringList("clips.modules." + module))
                // Make it into a stream of strings of class names
                .flatMap(Collection::stream)
                // Get the classes identified by class names
                .map(Classes::get)
                // Cast classes to Module as this is a requirement
                .map(cls -> (Class<ClipsModule>) cls)
                // Bind classes to the multibinder
                .forEach(cls -> modules.addBinding().to(cls));
    }
}
