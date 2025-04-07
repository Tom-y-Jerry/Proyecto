package es.ulpgc.dacd.infrastructure.api;

import io.github.cdimascio.dotenv.Dotenv;

public class ApiKeyLoader {
    public static String loadApiKey() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();
        String key = dotenv.get("TICKETMASTER_API_KEY");

        if (key == null || key.isEmpty()) {
            System.err.println("❌ API key not found in .env file.");
            return null;
        }
        return key;
    }
}
