package net.klakegg.clips;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) throws Exception {
        Clips clips = new Clips();
        clips.startServices();

        try {
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        clips.stopServices();
    }
}
