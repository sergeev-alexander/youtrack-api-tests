package alexander.sergeev.qa.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    public static final String YOUTRACK_AUTH_TOKEN = "YOUTRACK_AUTH_TOKEN";
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private final Properties properties;

    public ConfigManager(String configFileName) {
        this.properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (input == null) {
                logger.error("Config file not found in classpath!");
                throw new IllegalStateException("Configuration file not found!");
            }
            properties.load(input);
        } catch (IOException e) {
            logger.error("Failed to parse config: {}", e.getMessage());
            throw new RuntimeException("Configuration loading failed!", e);
        }
        logger.info("Properties loaded successful!");
    }

    public String getAuthToken() {
        String token = System.getenv(YOUTRACK_AUTH_TOKEN);
        if (token == null || token.isBlank()) {
            logger.error("Missing YOUTRACK_AUTH_TOKEN!");
            throw new IllegalStateException("Missing YOUTRACK_AUTH_TOKEN!");
        }
        logger.info("YOUTRACK_AUTH_TOKEN loaded successful!");
        return token;
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}