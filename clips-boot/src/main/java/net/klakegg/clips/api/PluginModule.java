package net.klakegg.clips.api;

import com.google.inject.AbstractModule;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;

/**
 * Helper class making available some handy methods.
 */
public abstract class PluginModule extends AbstractModule {

    protected void emptyMultibinder(Class<?> cls) {
        Multibinder.newSetBinder(binder(), cls);
    }

    protected <T> LinkedBindingBuilder<T> multibinder(Class<T> cls) {
        return Multibinder.newSetBinder(binder(), cls).addBinding();
    }
}
