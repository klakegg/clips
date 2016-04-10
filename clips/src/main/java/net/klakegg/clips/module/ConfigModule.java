package net.klakegg.clips.module;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import net.klakegg.clips.api.PluginModule;
import net.klakegg.clips.utils.ConfigHelper;

/**
 * Simple module created to expose configuration in application.
 */
public class ConfigModule extends PluginModule {

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
