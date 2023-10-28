package org.example;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.entities.WeatherType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class TestContainersTests extends ApplicationTests {
    private static final String DATABASE_NAME = "test-db";

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withReuse(true)
            .withUsername("test")
            .withPassword("pass")
            .withDatabaseName(DATABASE_NAME);

    @BeforeAll
    public static void setup() {
        postgreSQLContainer.start();
    }

    @AfterAll
    public static void tearDown() {
        postgreSQLContainer.stop();
    }

    @Test
    public void testConnection() {
        assertTrue(postgreSQLContainer.isRunning());
    }

    @Test
    public void testMigrations() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:" + postgreSQLContainer.getMappedPort(5432) + "/" + DATABASE_NAME);
        dataSource.setUsername("test");
        dataSource.setPassword("pass");

        try (Connection connection = dataSource.getConnection()) {
            Liquibase liquibase = new Liquibase("changelog/changelog-master.xml",
                    new ClassLoaderResourceAccessor(), new JdbcConnection(connection));

            liquibase.update();

            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

            List<WeatherType> weatherTypes = jdbcTemplate.query(
                    "SELECT * FROM weather_type",
                    (rs, rowNum) -> WeatherType.builder()
                            .id(rs.getLong("id"))
                            .type(rs.getString("type"))
                            .build()
            );

            assertEquals(5, weatherTypes.size());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }
}
