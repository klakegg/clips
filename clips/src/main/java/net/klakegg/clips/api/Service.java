package net.klakegg.clips.api;

public interface Service {

    /**
     * Starts a given service.
     *
     * Should throw ServiceException to indicate problems while loading service.
     */
    void start();

    /**
     * Stops a given service.
     */
    void stop();
}
