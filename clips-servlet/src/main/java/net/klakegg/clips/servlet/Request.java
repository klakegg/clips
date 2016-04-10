package net.klakegg.clips.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public abstract class Request {

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected Handler handler;

    public Request(HttpServletRequest request, HttpServletResponse response, Handler handler) {
        this.request = request;
        this.response = response;
        this.handler = handler;
    }

    public HttpServletRequest request() {
        return request;
    }

    public HttpServletResponse response() {
        return response;
    }

    void handle() throws ServletException, IOException {
        handler.handle(this);
    }

    public void output(Object o) throws IOException {
        response.getWriter().println(o);
    }

    public InputStream input() throws IOException {
        return request.getInputStream();
    }
}
