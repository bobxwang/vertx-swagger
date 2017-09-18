package com.bob.vertx.webapi.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by wangxiang on 17/9/6.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IndexVerticle extends AbstractVerticle {

    private Logger logger = LoggerFactory.getLogger(IndexVerticle.class);

    @Autowired
    private Router router;

    @Override
    public void start() throws Exception {
        super.start();

        router.get("/").handler(ctx ->
                {
                    logger.info(Thread.currentThread().getName());
                    ctx.response().putHeader("content-type", "application/json;charset=UTF-8")
                            .end(new JsonObject()
                                    .put("hello", "welcome to open api!")
                                    .encodePrettily());
                }
        );
    }
}