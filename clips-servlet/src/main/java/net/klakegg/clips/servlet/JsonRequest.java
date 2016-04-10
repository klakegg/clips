package net.klakegg.clips.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonRequest extends Request {

    private Gson gson = new GsonBuilder().create();

    public JsonRequest(HttpServletRequest request, HttpServletResponse response, Handler handler) {
        super(request, response, handler);
    }

    public void output(Object o) throws IOException {
        response.setContentType("application/json;charset=utf8");
        gson.toJson(o, response.getWriter());
    }

    public <T> T input(Class<T> cls) throws IOException {
        return gson.fromJson(new InputStreamReader(request.getInputStream()), cls);
    }
}
