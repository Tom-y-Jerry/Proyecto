package es.ulpgc.dacd.config;

import io.github.cdimascio.dotenv.Dotenv;

public final class EnvLoader {

    private static final Dotenv ENV = Dotenv.configure()
            .directory("./")
            .ignoreIfMissing()
            .load();

    public static String get(String key) {
        String v = ENV.get(key);
        if (v == null || v.isEmpty())
            System.err.printf("⚠️  Env var %s not found%n", key);
        return v;
    }
}
