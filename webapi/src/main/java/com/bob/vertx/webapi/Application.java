package com.bob.vertx.webapi;

import com.bob.vertx.spring.SwaggerApp;
import com.bob.vertx.webapi.config.SpringConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangxiang on 17/9/1.
 */
public class Application {

    public static void main(String[] args) throws IOException {

        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");

        ApplicationContext applicationContext = SwaggerApp.Run(SpringConfig.class);

        final ObjectMapper objectMapper = applicationContext.getBean(ObjectMapper.class);
        final Router router = applicationContext.getBean(Router.class);
        router.route().failureHandler(frc -> {
            int statusCode = frc.statusCode();
            HttpServerResponse response = frc.response();
            response.putHeader("content-type", "application/json;charset=UTF-8");
            Map<String, String> map = new HashMap<>();
            map.put("error", "Sorry! Not today");
            map.put("status", String.valueOf(statusCode));
            String rs = "{}";
            try {
                rs = objectMapper.writeValueAsString(map);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            response.setStatusCode(statusCode).end(rs);
        });
    }
}