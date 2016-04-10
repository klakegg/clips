package net.klakegg.clips.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface Handler<T extends Request> {

    void handle(Request request) throws ServletException, IOException;

    T request(HttpServletRequest req, HttpServletResponse resp);
}
