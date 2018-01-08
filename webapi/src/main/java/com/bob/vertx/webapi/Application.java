package com.bob.vertx.webapi;

import com.bob.vertx.spring.SwaggerApp;
import com.bob.vertx.webapi.config.SpringConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.FileResolver;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangxiang on 17/9/1.
 */
public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException {

        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        System.setProperty("hsqldb.reconfig_logging", "false");

        ApplicationContext applicationContext = SwaggerApp.Run(SpringConfig.class);

        Application application = new Application();
        application.demo(applicationContext);

        final ObjectMapper objectMapper = applicationContext.getBean(ObjectMapper.class);
        final Router router = applicationContext.getBean(Router.class);
        router.route().failureHandler(frc -> {
            /**
             * 任一请求出错都会进入到这里
             */
            int statusCode = frc.statusCode();
            HttpServerResponse response = frc.response();
            response.putHeader("content-type", "application/json;charset=UTF-8");
            Map<String, String> map = new HashMap<>();
            map.put("error", "Sorry! Not today");
            map.put("status", String.valueOf(statusCode));
            map.put("path", frc.request().path());
            map.put("msg", frc.failure() == null ? "" : frc.failure().getMessage());
            String rs = "{}";
            try {
                rs = objectMapper.writeValueAsString(map);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            response.setStatusCode(statusCode).end(rs);
        });
    }

    /**
     * a demo method about how to using vertx
     *
     * @param applicationContext
     */
    private void demo(ApplicationContext applicationContext) {
        final Vertx vertx = applicationContext.getBean(Vertx.class);
        vertx.setPeriodic(1000, id -> {
            // This handler will get called every second
            System.out.println("timer fired!" + new Date().toString());
        });
        vertx.setTimer(1000, id -> {
            // this handler will called after one second, just once
            System.out.println("And one second later this is printed");
        });

        // 拦截所有消息
        vertx.eventBus().addInterceptor(event -> {
            Message message = event.message();
            logger.info(message);
            event.next();
        });

        FileResolver fileResolver = new FileResolver(vertx);
        File file = fileResolver.resolveFile("webjars/bycdao-ui/cdao/DUI.js");
        if (file != null) {
            System.out.println(file.getAbsoluteFile());
        } else {
            System.out.println("null");
        }
    }
}