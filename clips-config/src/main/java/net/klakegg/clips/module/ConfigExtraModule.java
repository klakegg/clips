package net.klakegg.clips.module;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.typesafe.config.Config;
import net.klakegg.clips.annotation.Configuration;
import net.klakegg.clips.util.ConfigHelper;
import net.klakegg.sortable.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.stream.Stream;

@Sort(-4995)
public class ConfigExtraModule extends AbstractModule {

    private static Logger logger = LoggerFactory.getLogger(ConfigExtraModule.class);

    private Config config;

    @Inject
    public ConfigExtraModule(Config config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        // Bind ConfigHelper
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

        /**
         * Field subject to configuration.
         */
        private final Field field;

        public ConfigurationMembersInjector(Field field) {
            // Store field
            this.field = field;
            // Make field accessible
            field.setAccessible(true);
        }

        @Override
        public void injectMembers(I instance) {
            try {
                // Fetch annotation
                Configuration configuration = field.getAnnotation(Configuration.class);
                // Update of field is performed only when there are something to insert
                if (config.hasPath(configuration.value())) {
                    // Handling 'String'
                    if (String.class.equals(field.getType()))
                        field.set(instance, config.getString(configuration.value()));
                    // Handling 'Integer'
                    else if (Integer.TYPE.equals(field.getType()))
                        field.set(instance, config.getInt(configuration.value()));
                    // Handling 'int'
                    else if (Integer.class.equals(field.getType()))
                        field.set(instance, config.getInt(configuration.value()));
                    // Logging field not supported
                    else
                        logger.warn("{}: {}", field, field.getType());
                }
            } catch (IllegalAccessException e) {
                // Should never happen, but just in case
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
