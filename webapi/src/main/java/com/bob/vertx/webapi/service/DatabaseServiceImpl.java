package com.bob.vertx.webapi.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.Date;

/**
 * Created by wangxiang on 17/10/27.
 */
public class DatabaseServiceImpl implements DatabaseService {

    private Vertx vertx;

    public DatabaseServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public DatabaseService fetchAllPages(Handler<AsyncResult<JsonObject>> resultHandler) {

        JsonObject response = new JsonObject();
        response
                .put("dbthread", Thread.currentThread().getName())
                .put("pages", "from databaseServiceImpl" + new Date().toString());
        resultHandler.handle(Future.succeededFuture(response));
        return this;
    }
}