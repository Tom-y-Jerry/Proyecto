package es.ulpgc.dacd.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./")
            .ignoreIfMissing()
            .load();

    public static String load(String keyName) {
        String value = dotenv.get(keyName);
        if (value == null || value.isEmpty()) {
            System.err.println("❌ Variable de entorno '" + keyName + "' no encontrada en .env");
            return null;
        }
        return value;
    }

    public static class EnvLoader {
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
}
