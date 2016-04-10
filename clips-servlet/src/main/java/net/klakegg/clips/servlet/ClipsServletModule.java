package net.klakegg.clips.servlet;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

public class ClipsServletModule extends ServletModule {

    protected void serve(Class<? extends ClipsServlet> cls) {
        String path = cls.getAnnotation(Path.class).value();

        bind(cls).in(Singleton.class);
        serveRegex(path, path + "/*").with(cls);

    }
}
