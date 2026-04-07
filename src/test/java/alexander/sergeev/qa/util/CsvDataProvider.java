package alexander.sergeev.qa.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvDataProvider {

    private static final Logger logger = LoggerFactory.getLogger(CsvDataProvider.class);

    public static Object[][] load(String filePath) {
        List<Object[]> rows = new ArrayList<>();

        try (InputStream input = CsvDataProvider.class.getClassLoader().getResourceAsStream(filePath)) {
            if (input == null) {
                logger.error("CSV file not found in classpath: {}", filePath);
                throw new IllegalStateException("CSV file not found: " + filePath);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                reader.readLine();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isBlank()) {
                        rows.add(line.split(",", -1));
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Failed to read CSV file: {}", e.getMessage());
            throw new RuntimeException("CSV loading failed: " + filePath, e);
        }

        return rows.toArray(new Object[0][]);
    }
}