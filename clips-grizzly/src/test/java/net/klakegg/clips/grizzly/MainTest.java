package net.klakegg.clips.grizzly;

import net.klakegg.clips.Main;
import org.testng.annotations.Test;

public class MainTest {

    @Test(groups = "manual")
    public void run() throws Exception {
        Main.main();
    }
}
