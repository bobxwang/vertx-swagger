package com.bob.vertx.webapi;

import com.bob.vertx.spring.SwaggerApp;
import com.bob.vertx.webapi.config.SpringConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Observable;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangxiang on 17/9/1.
 */
public class Application {

    public static void main(String[] args) throws IOException {

        Observable.just("Hello", "World")
                .subscribe(System.out::println);

        List<String> words = Arrays.asList(
                "the",
                "quick",
                "brown",
                "fox",
                "jumped",
                "over",
                "the",
                "lazy",
                "dog"
        );
        Observable.just(words).subscribe(System.out::println);
        Observable.fromIterable(words).subscribe(System.out::println);
        Observable.range(1, 5).subscribe(System.out::println);
        Observable.fromIterable(words)
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, count) -> String.format("%2d. %s", count, string))
                .subscribe(System.out::println);
        Observable.fromIterable(words)
                .flatMap(word -> Observable.fromArray(word.split("")))
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, count) -> String.format("%2d. %s", count, string))
                .subscribe(System.out::println);
        Observable.fromIterable(words)
                .flatMap(word -> Observable.fromArray(word.split("")))
                .distinct()
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, count) -> String.format("%2d. %s", count, string))
                .subscribe(System.out::println);

        List<String> mobile = Arrays.asList(
                "13605802134",
                "13605802234",
                "13605702134",
                "13605702136",
                "13605702137",
                "13605602134",
                "13605602234",
                "13605602264",
                "13605602254"
        );
        Observable.fromIterable(mobile).groupBy(x -> x.substring(0, 7))
                .subscribe(x ->
                        x.count().subscribe(y -> {
                            System.out.println(x.getKey() + " - " + y);
                        })
                );

        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        System.setProperty("hsqldb.reconfig_logging", "false");

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
}