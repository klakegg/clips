package net.klakegg.clips.util;

import com.google.inject.Inject;
import com.typesafe.config.Config;

import java.util.List;
import java.util.Optional;

public class ConfigHelper {

    private Config config;

    @Inject
    public ConfigHelper(Config config) {
        this.config = config;
    }

    public Optional<Boolean> getBoolean(String path) {
        return config.hasPath(path) ? Optional.of(config.getBoolean(path)) : Optional.empty();
    }

    public Optional<String> getString(String path) {
        return config.hasPath(path) ? Optional.of(config.getString(path)) : Optional.empty();
    }

    public Optional<List<String>> getStringList(String path) {
        return config.hasPath(path) ? Optional.of(config.getStringList(path)) : Optional.empty();
    }
}
