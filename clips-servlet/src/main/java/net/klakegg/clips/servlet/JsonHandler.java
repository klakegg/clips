package net.klakegg.clips.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@FunctionalInterface
public interface JsonHandler extends Handler<JsonRequest> {

    void handle(Request request) throws ServletException, IOException;

    default JsonRequest request(HttpServletRequest req, HttpServletResponse resp) {
        return new JsonRequest(req, resp, this);
    }
}
