package com.bob.vertx.webapi.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * Created by wangxiang on 17/9/1.
 */
public class DBConfig {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {

        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setUsername(env.getProperty("jdbc.username"));
        hikariDataSource.setPassword(env.getProperty("jdbc.password"));
        hikariDataSource.setJdbcUrl(env.getProperty("jdbc.url"));
        hikariDataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        return hikariDataSource;
    }
}