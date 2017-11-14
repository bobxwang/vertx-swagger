package com.bob.vertx.webapi.verticle;

import com.bob.vertx.webapi.service.DatabaseService;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by wangxiang on 17/11/1.
 */
@RunWith(VertxUnitRunner.class)
public class DatabaseVerticleTest {

    private final String wikiDbQueue = "wikidb.queue";
    private Vertx vertx;
    private DatabaseService databaseService;

    @Before
    public void prepare(TestContext context) throws InterruptedException {

        vertx = Vertx.vertx();
        vertx.deployVerticle(new DatabaseVerticle(), context.asyncAssertSuccess(id -> {
            databaseService = DatabaseService.createProxy(vertx, wikiDbQueue);
        }));
    }

    @After
    public void finish(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testSendMsg(TestContext context) {
        final Async async = context.async();

        databaseService.fetchAllPages(r -> {
            if (r.succeeded()) {
                JsonObject jsonObject = r.result();
                context.assertNotNull(jsonObject);
                context.assertNotNull(jsonObject.getString("dbthread"));
                async.complete();
            }
        });

        async.awaitSuccess(5 * 1000);
    }
}