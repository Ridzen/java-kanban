package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;

public abstract class BaseHttpHandler implements com.sun.net.httpserver.HttpHandler {
    protected final TaskManager manager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    protected String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void sendText(HttpExchange exchange, String response, int code) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 404);
    }

    protected void sendHasIntersections(HttpExchange exchange) throws IOException {
        sendText(exchange, "Task time overlaps with existing task", 406);
    }

    protected void sendServerError(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 500);
    }

    protected int getIdFromQuery(String query) {
        if (query == null) return 0;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if ("id".equals(kv[0]) && kv.length > 1) return Integer.parseInt(kv[1]);
        }
        return 0;
    }
}
