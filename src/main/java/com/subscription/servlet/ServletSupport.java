package com.subscription.servlet;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

final class ServletSupport {

    private static final Gson GSON = new Gson();

    private ServletSupport() {}

    static boolean wantsJson(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("application/json");
    }

    static void writeJson(HttpServletResponse response, int status, Object body)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        GSON.toJson(body, response.getWriter());
    }

    static void writeError(HttpServletRequest request, HttpServletResponse response,
                           int status, String message) throws IOException {
        if (wantsJson(request)) {
            writeJson(response, status, Collections.singletonMap("error", message));
        } else {
            response.sendError(status, message);
        }
    }

    static int requiredId(HttpServletRequest request) {
        String value = request.getParameter("id");
        try {
            int id = Integer.parseInt(value);
            if (id > 0) {
                return id;
            }
        } catch (NumberFormatException ignored) {
            // Report all malformed/missing IDs with the same client error.
        }
        throw new IllegalArgumentException("A valid record ID is required.");
    }

    static String required(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " is required.");
        }
        return value.trim();
    }
}
