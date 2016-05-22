package net.klakegg.clips;

import com.google.inject.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) throws Exception {
        run();
    }

    public static void run(Module... metaModules) throws Exception {
        Clips clips = new Clips(metaModules);
        clips.startServices();

        try {
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        clips.stopServices();
    }
}
