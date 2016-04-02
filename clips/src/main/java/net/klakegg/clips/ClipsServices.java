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

class ClipsServices {

    private static Logger logger = LoggerFactory.getLogger(ClipsServices.class);

    private List<Service> services;

    @Inject
    public ClipsServices(Set<Service> services) {
        this.services = services.stream().sorted(Sortables.comparator()).collect(Collectors.toList());
    }

    public void start() throws Exception {
        for (Service service : services) {
            logger.info("Starting '{}'.", service.getClass().getName());
            service.start();
        }
        logger.info("Started.");
    }

    public void stop() {
        for (Service service : Lists.reverse(services)) {
            logger.info("Stopping '{}'.", service.getClass().getName());
            service.stop();
        }
        logger.info("Stopped.");
    }
}
