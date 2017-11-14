package com.bob.vertx.webapi.verticle;

import com.bob.vertx.webapi.service.DatabaseService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by wangxiang on 17/9/6.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IndexVerticle extends AbstractVerticle {

    private Logger logger = LoggerFactory.getLogger(IndexVerticle.class);

    @Autowired
    private Router router;

    private String wikiDbQueue = "wikidb.queue";

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        logger.info("in has param start");

        router.get("/index").handler(ctx -> {
            ctx.response().putHeader("content-type", "application/json;charset=UTF-8");
            ctx.response().end(new JsonObject()
                    .put("index", "index")
                    .put("now", new Date().toString()).encodePrettily());
        });

        router.get("/").handler(ctx ->
                {
//                    DeliveryOptions options = new DeliveryOptions().addHeader("action", "all-apges");
//                    vertx.eventBus().send(wikiDbQueue, new JsonObject(), options, reply -> {
//                        if (reply.succeeded()) {
//                            JsonObject body = (JsonObject) reply.result().body();
//                            ctx.response().putHeader("content-type", "application/json;charset=UTF-8");
//                            ctx.response().end(new JsonObject()
//                                    .put("index", "index")
//                                    .put("dbthread", body.getValue("dbthread"))
//                                    .put("thread", Thread.currentThread().getName())
//                                    .put("pages", body.getString("pages")).encodePrettily());
//                        } else {
//                            ctx.fail(reply.cause());
//                        }
//                    });

                    DatabaseService databaseService = DatabaseService.createProxy(vertx, wikiDbQueue);
                    databaseService.fetchAllPages(ar -> {
                        if (ar.succeeded()) {
                            JsonObject jsonObject = ar.result();
                            ctx.response().putHeader("content-type", "application/json;charset=UTF-8");
                            ctx.response().end(new JsonObject()
                                    .put("index", "index")
                                    .put("dbthread", jsonObject.getValue("dbthread"))
                                    .put("thread", Thread.currentThread().getName())
                                    .put("pages", jsonObject.getString("pages")).encodePrettily());
                        } else {
                            ctx.fail(ar.cause());
                        }
                    });
                }
        );

        Future<String> dbVerticle = Future.future();
        vertx.deployVerticle(new DatabaseVerticle(), dbVerticle.completer());
        dbVerticle.setHandler(ar -> {
            if (ar.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(ar.cause());
            }
        });
    }
}