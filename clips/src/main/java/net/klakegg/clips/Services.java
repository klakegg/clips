package net.klakegg.clips;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import net.klakegg.clips.api.Service;
import net.klakegg.commons.sortable.Sortables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class Services {

    private static Logger logger = LoggerFactory.getLogger(Services.class);

    private List<Service> services;

    @Inject
    public Services(Set<Service> services) {
        this.services = services.stream()
                .sorted(Sortables.comparator())
                .collect(Collectors.toList());
    }

    public void start() {
        services.stream()
                .peek(s -> logger.info("Starting '{}'.", s.getClass().getName()))
                .forEach(Service::start);
        logger.info("Ready");
    }

    public void stop() {
        Lists.reverse(services).stream()
                .peek(s -> logger.info("Stopping '{}'.", s.getClass().getName()))
                .forEach(Service::stop);
    }
}
