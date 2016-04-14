package net.klakegg.clips.module;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import net.klakegg.sortable.Sort;

/**
 * Simple module created to expose configuration in application.
 */
@Sort(-5000)
public class ConfigModule extends AbstractModule {

    private Config config;

    @Inject
    public ConfigModule(Config config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        bind(Config.class).toInstance(config);
    }
}
