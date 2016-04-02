package net.klakegg.clips.module;

import net.klakegg.clips.api.PluginModule;
import net.klakegg.clips.api.Service;
import net.klakegg.commons.sortable.Sort;

@Sort(Integer.MIN_VALUE)
public class ClipsModule extends PluginModule {

    @Override
    protected void configure() {
        emptyMultibinder(Service.class);
    }
}
