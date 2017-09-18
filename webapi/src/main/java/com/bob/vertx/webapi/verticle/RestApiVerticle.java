package com.bob.vertx.webapi.verticle;

import com.bob.vertx.swagger.BBRouter;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.ChainAuth;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.BasicAuthHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by wangxiang on 17/9/4.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RestApiVerticle extends AbstractVerticle {

    @Autowired
    private Router router;

    @Override
    public void start() throws Exception {

        Router swaggerRouter = Router.router(vertx);
        configRoute(router);
        router.mountSubRouter("/rest", swaggerRouter);
    }

    private void configRoute(final Router router) {

        AuthProvider authProvider = ChainAuth.create();
        AuthHandler basicAuthHandler = BasicAuthHandler.create(authProvider);
        router.route("/private/*").handler(basicAuthHandler);
        Route route = router.route(HttpMethod.GET, "/private/path/");
        route.handler(ctx -> ctx.response().end(new JsonObject().put("rs", new Date().toString()).encodePrettily()));

        router.route(HttpMethod.GET, "/some/path/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            // 由于我们会在不同的处理器里写入响应，因此需要启用分块传输，仅当需要通过多个处理器输出响应时才需要
            response.setChunked(true);
            response.write("route1\n");
            // 1 秒后调用下一个处理器
            routingContext.vertx().setTimer(1000, tid -> routingContext.next());
        });
        router.route(HttpMethod.GET, "/some/path/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.write("route2\n");
            // 1 秒后调用下一个处理器
            routingContext.vertx().setTimer(1000, tid -> routingContext.next());
        });
        router.route(HttpMethod.GET, "/some/path/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.write("route3");
            // 结束响应
            routingContext.response().end();
        });

        router.route(HttpMethod.GET, "/long/time").blockingHandler(ctx -> {
            // call other service maybe long time, so here we using block pattern
            try {
                Thread.sleep(5000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ctx.response().end(new JsonObject().put("rs", new Date().toString()).encodePrettily());
        });

    }

    @BBRouter(path = "/catalogue/products/:producttype/:productid", httpMethod = HttpMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "producttype", value = "产品类型", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "productid", value = "产品标识", dataType = "string", paramType = "path")
    })
    private void handle(RoutingContext ctx) {
        String productType = ctx.request().getParam("producttype");
        String productID = ctx.request().getParam("productid");
        ctx.response()
                .putHeader("content-type", "application/json;charset=UTF-8")
                .end(new JsonObject()
                .put("now", new Date().toString())
                .put("producttype", productType)
                .put("productid", productID).encodePrettily());
    }
}