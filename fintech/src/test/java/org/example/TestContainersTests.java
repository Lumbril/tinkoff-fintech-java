package org.example;

import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class TestContainersTests extends ApplicationTests {
    private static final String DATABASE_NAME = "test-db";

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withReuse(true)
            .withDatabaseName(DATABASE_NAME);

    public TestContainersTests() {
        postgreSQLContainer.start();
    }

    @Test
    public void testConnection() {
        assertTrue(postgreSQLContainer.isRunning());
    }
}
