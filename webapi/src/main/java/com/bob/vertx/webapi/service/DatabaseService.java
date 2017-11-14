package com.bob.vertx.webapi.service;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * Created by wangxiang on 17/10/27.
 */
@ProxyGen // generate the proxy and handler
@VertxGen // generate clients in no-java languages
public interface DatabaseService {

    static DatabaseService create(Vertx vertx) {
        return new DatabaseServiceImpl(vertx);
    }

    static DatabaseService createProxy(Vertx vertx, String address) {
        return ProxyHelper.createProxy(DatabaseService.class, vertx, address);
    }

    @Fluent
    DatabaseService fetchAllPages(Handler<AsyncResult<JsonObject>> resultHandler);
}