package org.example;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.entities.WeatherType;
import org.example.repositories.WeatherTypeRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class TestContainersTests extends ApplicationTests {
    private static final String DATABASE_NAME = "test-db";

    @Autowired
    private WeatherTypeRepository weatherTypeRepository;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withReuse(true)
            .withUsername("test")
            .withPassword("pass")
            .withDatabaseName(DATABASE_NAME);

    @DynamicPropertySource
    static void setupProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
    }

    @BeforeAll
    public static void setup() throws SQLException, LiquibaseException {
        postgreSQLContainer.start();

        DataSource dataSource = DataSourceBuilder.create()
                .url(postgreSQLContainer.getJdbcUrl())
                .username(postgreSQLContainer.getUsername())
                .password(postgreSQLContainer.getPassword())
                .build();

        Liquibase liquibase = new Liquibase("changelog/changelog-master.xml",
                new ClassLoaderResourceAccessor(), new JdbcConnection(dataSource.getConnection()));

        liquibase.update();
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
    public void testGetWeatherTypeAfterMigration() {
        WeatherType weatherType = weatherTypeRepository.findById(1L).get();
        assertEquals("Ясно", weatherType.getType());
    }
}
