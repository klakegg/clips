package net.klakegg.clips;

import com.google.inject.Inject;
import net.klakegg.clips.api.ClipsModule;

import java.util.Set;

/**
 * This class is used to expose the modules registered in the meta injector.
 */
class ClipsHelper {

    private Set<ClipsModule> modules;

    @Inject
    public ClipsHelper(Set<ClipsModule> modules) {
        this.modules = modules;
    }

    public Set<ClipsModule> getModules() {
        return modules;
    }
}
