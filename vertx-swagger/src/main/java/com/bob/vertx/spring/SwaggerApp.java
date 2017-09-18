package com.bob.vertx.spring;

import com.bob.vertx.swagger.BBRouter;
import com.bob.vertx.swagger.Reader;
import com.bob.vertx.swagger.RouteScanner;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.impl.RouterImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by wangxiang on 17/9/8.
 */
public final class SwaggerApp {

    private static Logger logger = LoggerFactory.getLogger(SwaggerApp.class);

    private static Swagger swagger = null;

    public static AnnotationConfigApplicationContext Run(Class<?> clasz) {

        final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(clasz);
        BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(ApplicationContextHolder.class);
        applicationContext.registerBeanDefinition("applicationContextHolder", bdb.getBeanDefinition());
        // Just For Init
        applicationContext.getBean(ApplicationContextHolder.class);
        final Environment environment = applicationContext.getBean(Environment.class);

        final Vertx vertx = applicationContext.getBean(Vertx.class);
        final SpringVerticleFactory verticleFactory = new SpringVerticleFactory();
        vertx.registerVerticleFactory(verticleFactory);

        try {
            applicationContext.getBean(Router.class);
        } catch (BeansException be) {
            if (be instanceof NoSuchBeanDefinitionException) {
                Router rr = new RouterImpl(vertx);
                applicationContext.getBeanFactory().registerSingleton("router", rr);
            }
        }

        final Router router = applicationContext.getBean(Router.class);

        initSwagger(environment);

        configRouter(vertx, router, environment);

        Map<String, Verticle> maps = applicationContext.getBeansOfType(Verticle.class);
        DeploymentOptions options = new DeploymentOptions().setInstances(4).setWorker(true);
        for (Map.Entry<String, Verticle> temp : maps.entrySet()) {
            Verticle verticle = temp.getValue();
            String name = verticle.getClass().getSimpleName().substring(0, 1).toLowerCase() + verticle.getClass().getSimpleName().substring(1);
            vertx.deployVerticle(verticleFactory.prefix() + ":" + name, options);
            for (Method method : verticle.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Override.class)) {
                    continue;
                }
                if (method.getName().contains("lambda")) {
                    continue;
                }
                if (io.swagger.util.ReflectionUtils.isOverriddenMethod(method, verticle.getClass())) {
                    continue;
                }
                if (method.isAnnotationPresent(BBRouter.class)) {
                    BBRouter bbRouter = io.swagger.util.ReflectionUtils.getAnnotation(method, BBRouter.class);
                    Route route = router.route(bbRouter.httpMethod(), bbRouter.path());
                    route.handler(ctx -> {
                        ReflectionUtils.makeAccessible(method);
                        ReflectionUtils.invokeMethod(method, verticle, ctx);
                    });
                }
            }
        }

        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router::accept);
        int port;
        try {
            port = Integer.valueOf(environment.getProperty("server.port", "8080"));
        } catch (Exception e) {
            throw new RuntimeException("请配置有效端口号");
        }
        httpServer.listen(port, ar -> {
            if (ar.succeeded()) {
                logger.info("Server started on port " + ar.result().actualPort());
            } else {
                logger.error("Cannot start the server: " + ar.cause());
            }
        });

        return applicationContext;
    }

    private static void initSwagger(final Environment environment) {
        if (swagger == null) {
            swagger = new Swagger();
            swagger.setSwagger("2.0");
            String value = environment.getProperty("swagger.scanner.path");
            if (Strings.isNullOrEmpty(value)) {
                throw new RuntimeException("请配置好swagger扫描路径");
            }
            RouteScanner scanner;
            if (value.contains(";")) {
                scanner = new RouteScanner(Arrays.asList(value.split(";")));
            } else {
                scanner = new RouteScanner(value);
            }

            Info info = new Info();
            info.title(environment.getProperty("server.description", ""))
                    .version("1.0.0");
            swagger.info(info);
            Reader.read(swagger, scanner.classes());
        }
    }

    private static void configRouter(final Vertx vertx, final Router router, final Environment environment) {

        router.route().handler(BodyHandler.create());

        if (environment.getProperty("server.iscors", "false").contentEquals("true")) {
            CorsHandler corsHandler;
            String value = environment.getProperty("server.allowedoriginpattern");
            if (Strings.isNullOrEmpty(value)) {
                corsHandler = CorsHandler.create("*");
            } else {
                corsHandler = CorsHandler.create(value);
            }
            value = environment.getProperty("server.allowed.method");
            if (Strings.isNullOrEmpty(value)) {
                corsHandler.allowedMethods(Sets.newHashSet(HttpMethod.values()));
            } else {
                if (value.contains(";")) {
                    String[] methods = value.split(";");
                    for (String m : methods) {
                        configCorsAllowedMethod(corsHandler, m);
                    }
                } else {
                    configCorsAllowedMethod(corsHandler, value);
                }
            }
            value = environment.getProperty("server.maxageseconds");
            if (!Strings.isNullOrEmpty(value)) {
                try {
                    Integer i = Integer.valueOf(value);
                    corsHandler.maxAgeSeconds(i);
                } catch (NumberFormatException e) {
                    logger.warn("server.maxageseconds值请配置成整数" + e.getMessage(), e);
                }
            }

            router.route().handler(corsHandler);
        }

        router.route("/static/*").handler(StaticHandler.create());

        Router swaggerRouter = Router.router(vertx);
        router.mountSubRouter("/swagger", swaggerRouter);
        swaggerRouter.get("/definition.json").handler(ctx -> {
            ctx.response()
                    .putHeader("content-type", "application/json;charset=UTF-8")
                    .end(Json.encodePrettily(swagger));
        });
    }

    private static void configCorsAllowedMethod(CorsHandler corsHandler, String method) {
        String m = method.toUpperCase();

        try {
            HttpMethod httpMethod = HttpMethod.valueOf(m);
            corsHandler.allowedMethod(httpMethod);
        } catch (Exception e) {
            logger.warn(method + e.getMessage(), e);
        }
    }
}