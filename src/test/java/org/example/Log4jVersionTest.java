package org.example;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Log4jVersionTest {

    @Test
    void usesPatchedLog4jVersion() throws IOException {
        Properties properties = new Properties();
        InputStream resourceStream = LogManager.class.getResourceAsStream(
                "/META-INF/maven/org.apache.logging.log4j/log4j-api/pom.properties");

        assertNotNull(resourceStream, "Expected Log4j pom.properties to be present on the classpath");

        try (InputStream stream = resourceStream) {
            properties.load(stream);
        }

        String version = properties.getProperty("version");

        assertTrue(version != null && isAtLeast(version, 2, 16, 0),
                () -> "Expected Log4j version to be at least 2.16.0 but was " + version);
    }

    private static boolean isAtLeast(String version, int major, int minor, int patch) {
        int[] parsed = Arrays.stream(version.split("[.-]"))
                .limit(3)
                .mapToInt(Integer::parseInt)
                .toArray();

        if (parsed[0] != major) {
            return parsed[0] > major;
        }
        if (parsed[1] != minor) {
            return parsed[1] > minor;
        }
        return parsed[2] >= patch;
    }
}
