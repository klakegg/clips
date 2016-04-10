package net.klakegg.clips.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GenericRequest extends Request {

    public GenericRequest(HttpServletRequest request, HttpServletResponse response, Handler handler) {
        super(request, response, handler);
    }
}
