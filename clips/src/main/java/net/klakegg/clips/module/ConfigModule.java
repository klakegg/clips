package net.klakegg.clips.module;

import com.google.inject.Inject;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.typesafe.config.Config;
import net.klakegg.clips.annotation.Configuration;
import net.klakegg.clips.api.PluginModule;
import net.klakegg.clips.utils.ConfigHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.stream.Stream;

/**
 * Simple module created to expose configuration in application.
 */
public class ConfigModule extends PluginModule {

    private static Logger logger = LoggerFactory.getLogger(ConfigModule.class);

    private Config config;

    @Inject
    public ConfigModule(Config config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        bind(Config.class).toInstance(config);
        bind(ConfigHelper.class);

        // Enable injection of configurations
        bindListener(Matchers.any(), new TypeListener() {
            @Override
            public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
                // Fetch all fields of inspected type
                Stream.of(type.getRawType().getDeclaredFields())
                        // Remove all fields not having Configuration annotation
                        .filter(f -> f.isAnnotationPresent(Configuration.class))
                        // Register field for injection of configuration
                        .forEach(f -> encounter.register(new ConfigurationMembersInjector<>(f)));
            }
        });
    }

    private class ConfigurationMembersInjector<I> implements MembersInjector<I> {
        private final Field field;

        public ConfigurationMembersInjector(Field field) {
            this.field = field;
            field.setAccessible(true);
        }

        @Override
        public void injectMembers(I instance) {
            try {
                Configuration configuration = field.getAnnotation(Configuration.class);
                if (ConfigModule.this.config.hasPath(configuration.value())) {
                    if (String.class.equals(field.getType()))
                        field.set(instance, ConfigModule.this.config.getString(configuration.value()));
                    else if (Integer.TYPE.equals(field.getType()))
                        field.set(instance, ConfigModule.this.config.getInt(configuration.value()));
                    else if (Integer.class.equals(field.getType()))
                        field.set(instance, ConfigModule.this.config.getInt(configuration.value()));
                    else
                        logger.warn("{}: {}", field, field.getType());
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
