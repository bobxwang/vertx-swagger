package com.bob.vertx.webapi.verticle;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by wangxiang on 17/11/1.
 */
@RunWith(VertxUnitRunner.class)
public class IndexVerticleTest {

    @Test
    public void TestIndex(TestContext context) {

        Async async = context.async();

        final Vertx vertx = Vertx.vertx();
        vertx.createHttpServer().requestHandler(req ->
                req.response()
                        .putHeader("Content-Type", "text/plain")
                        .end("OK"))
                .listen(8080, context.asyncAssertSuccess(s -> {
                    WebClient webClient = WebClient.create(vertx);
                    webClient.get(8080, "localhost", "/")
                            .send(ar -> {
                                if (ar.succeeded()) {
                                    HttpResponse<Buffer> response = ar.result();
                                    context.assertTrue(response.headers().contains("Content-Type"));
                                    context.assertEquals("text/plain", response.getHeader("Content-Type"));
                                    context.assertEquals("OK", response.bodyAsString());
                                    webClient.close();
                                    async.complete();
                                } else {
                                    async.resolve(Future.failedFuture(ar.cause()));
                                }
                            });
                }));
        async.awaitSuccess(5000);
    }
}