package net.klakegg.clips.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HandlerWrapper {

    private Pattern pattern;
    private Handler handler;

    public HandlerWrapper(String regex, Handler handler) {
        this.pattern = Pattern.compile(regex);
        this.handler = handler;
    }

    public Request detect(String path, HttpServletRequest req, HttpServletResponse resp) {
        Matcher matcher = pattern.matcher(path);
        if (!matcher.matches())
            return null;

        return handler.request(req, resp);
    }
}
