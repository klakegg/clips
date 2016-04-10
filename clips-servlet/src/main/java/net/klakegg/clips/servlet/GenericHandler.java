package net.klakegg.clips.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@FunctionalInterface
public interface GenericHandler extends Handler<Request> {

    void handle(Request request) throws ServletException, IOException;

    default GenericRequest request(HttpServletRequest req, HttpServletResponse resp) {
        return new GenericRequest(req, resp, this);
    }
}
