package com.dilsheen.ingest.config;

import java.net.URI;

public final class JdbcUrlParser {

    public record Parsed(String jdbcUrl, String username, String password) {
    }

    private JdbcUrlParser() {
    }

    public static Parsed parse(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("DATABASE_URL is required");
        }
        if (raw.startsWith("jdbc:")) {
            return new Parsed(raw, null, null);
        }

        String normalized = raw.startsWith("postgres://")
                ? "postgresql://" + raw.substring("postgres://".length())
                : raw;
        URI uri = URI.create(normalized);
        if (uri.getHost() == null) {
            throw new IllegalArgumentException("DATABASE_URL must include a host");
        }

        String username = null;
        String password = null;
        String userInfo = uri.getUserInfo();
        if (userInfo != null) {
            int colon = userInfo.indexOf(':');
            if (colon >= 0) {
                username = userInfo.substring(0, colon);
                password = userInfo.substring(colon + 1);
            } else {
                username = userInfo;
            }
        }

        String jdbc = "jdbc:postgresql://"
                + uri.getHost()
                + (uri.getPort() > 0 ? ":" + uri.getPort() : "")
                + (uri.getPath() == null || uri.getPath().isBlank() ? "/" : uri.getPath())
                + (uri.getQuery() != null ? "?" + uri.getQuery() : "");
        return new Parsed(jdbc, username, password);
    }
}
