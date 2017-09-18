package com.bob.vertx.webapi.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import org.springframework.context.annotation.*;

/**
 * Created by wangxiang on 17/9/1.
 */
@Configuration
@PropertySource(value = {"classpath:application.properties"}, encoding = "utf-8")
@ComponentScan("com.bob.vertx.*")
@Import(DBConfig.class)
public class SpringConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = Json.mapper;
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        Json.prettyMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return objectMapper;
    }

    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }
}