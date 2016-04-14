package net.klakegg.clips.module;

import net.klakegg.clips.api.PluginModule;
import net.klakegg.clips.api.Service;
import net.klakegg.sortable.Sort;

@Sort(-10000)
public class ClipsModule extends PluginModule {

    @Override
    protected void configure() {
        emptyMultibinder(Service.class);
    }
}
