package com.pokestore.mcp.server.config;

/**
 * ThreadLocal holder for the Authorization header,
 * allowing pass-through from incoming MCP requests to outgoing API calls.
 */
public final class AuthTokenHolder {

    private static final ThreadLocal<String> TOKEN = new ThreadLocal<>();

    private AuthTokenHolder() {
    }

    public static void set(String token) {
        TOKEN.set(token);
    }

    public static String get() {
        return TOKEN.get();
    }

    public static void clear() {
        TOKEN.remove();
    }
}
