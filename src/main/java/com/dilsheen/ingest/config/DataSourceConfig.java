package com.dilsheen.ingest.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource(
            @Value("${DATABASE_URL:jdbc:postgresql://localhost:5432/ingest}") String databaseUrl,
            @Value("${DATABASE_USER:ingest}") String defaultUser,
            @Value("${DATABASE_PASSWORD:ingest}") String defaultPassword
    ) {
        JdbcUrlParser.Parsed parsed = JdbcUrlParser.parse(databaseUrl);
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(parsed.jdbcUrl());
        dataSource.setUsername(parsed.username() != null ? parsed.username() : defaultUser);
        dataSource.setPassword(parsed.password() != null ? parsed.password() : defaultPassword);
        dataSource.setMaximumPoolSize(5);
        dataSource.setConnectionTimeout(3_000);
        dataSource.setInitializationFailTimeout(-1);
        return dataSource;
    }
}
