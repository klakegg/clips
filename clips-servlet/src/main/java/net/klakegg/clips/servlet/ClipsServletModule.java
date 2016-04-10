package net.klakegg.clips.servlet;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class ClipsServletModule extends ServletModule {

    protected void serve(String path, Class<? extends ClipsServlet> cls) {
        bind(cls).in(Singleton.class);
        serve(path, path + "/*").with(cls);
    }
}
