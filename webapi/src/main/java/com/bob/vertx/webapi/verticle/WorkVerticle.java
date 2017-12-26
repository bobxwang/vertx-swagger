package com.bob.vertx.webapi.verticle;

import com.bob.vertx.webapi.service.DatabaseService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ServiceBinder;

import java.util.Date;

/**
 * Created by wangxiang on 17/10/26.
 */
public class WorkVerticle extends AbstractVerticle {

    private Logger logger = LoggerFactory.getLogger(WorkVerticle.class);

    private String wikiDbQueue = "wikidb.queue";

    private ServiceBinder serviceBinder = null;

    private MessageConsumer<JsonObject> consumer = null;

    @Override
    public void start() throws Exception {
        super.start();

//        vertx.eventBus().consumer(wikiDbQueue, this::onMessage);

        serviceBinder = new ServiceBinder(vertx);
        DatabaseService service = DatabaseService.create(vertx);
        if (consumer == null) {
            consumer = serviceBinder.setAddress(wikiDbQueue).register(DatabaseService.class, service);
        }
    }

    @Override
    public void stop() throws Exception {
        if (consumer == null) {
            serviceBinder.unregister(consumer);
        }
    }

    private void onMessage(Message<JsonObject> message) {
        if (!message.headers().contains("action")) {
            logger.error("no action header specified for message with header {} and body {}",
                    message.headers(), message.body().encodePrettily());
            message.fail(1, "No action header specified");
            return;
        }
        String action = message.headers().get("action");
        switch (action) {
            case "all-apges":
                message.reply(new JsonObject()
                        .put("dbthread", Thread.currentThread().getName())
                        .put("pages", "from dbVerticle" + new Date().toString()));
                break;
            default:
                message.fail(2, "Bad action: " + action);
                break;
        }
    }
}