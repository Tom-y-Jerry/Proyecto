package es.ulpgc.dacd.infrastructure.api;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./")
            .ignoreIfMissing()
            .load();

    public static String load(String keyName) {
        String value = dotenv.get(keyName);

        if (value == null || value.isEmpty()) {
            System.err.println("❌ Environment variable '" + keyName + "' not found in .env file.");
            return null;
        }

        return value;
    }
}
