package com.bob.vertx.webapi.verticle;

import com.bob.vertx.swagger.BBRouter;
import com.bob.vertx.webapi.param.req.UserReq;
import com.bob.vertx.webapi.param.res.UserRes;
import com.google.common.base.Strings;
import io.swagger.annotations.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by wangxiang on 17/9/6.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserVerticle extends AbstractVerticle {

    private Logger logger = LoggerFactory.getLogger(UserVerticle.class);

    @BBRouter(path = "/user/", httpMethod = HttpMethod.POST)
    @ApiOperation(value = "创建一个用户", response = UserRes.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "用户体", dataType = "com.bob.vertx.webapi.param.req.UserReq", paramType = "body")
    })
    private void postUser(RoutingContext ctx) {
        logger.info(Thread.currentThread().getName());
        JsonObject jsonObject = ctx.getBodyAsJson();
        jsonObject.put("now", new Date().toString());
        ctx.response().putHeader("content-type", "application/json;charset=UTF-8")
                .end(Json.encodePrettily(jsonObject));
    }

    @BBRouter(path = "/user/:id", httpMethod = HttpMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户标识", required = true, dataType = "integer", paramType = "path"),
            @ApiImplicitParam(name = "name", value = "用户姓名", dataType = "string", paramType = "query")
    })
    @ApiOperation(value = "查找一个用户")
    @ApiResponses({
            @ApiResponse(
                    message = "response_annotation1",
                    code = 200,
                    response = UserRes.class)
    })
    private void getUserById(RoutingContext ctx) {
        logger.info(Thread.currentThread().getName());
        String name = ctx.request().getParam("name");
        if (Strings.isNullOrEmpty(name)) {
            name = "微贷";
        }
        ctx.response().putHeader("content-type", "application/json;charset=UTF-8")
                .end(new JsonObject()
                        .put("id", ctx.request().getParam("id"))
                        .put("name", name)
                        .encodePrettily());
    }

    @BBRouter(path = "/user/:id", httpMethod = HttpMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户标识", required = true, dataType = "integer", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "用户体", dataType = "com.bob.vertx.webapi.param.req.UserReq", paramType = "body")
    })
    @ApiOperation(value = "更新一个用户", response = UserRes.class)
    @ApiResponses({
            @ApiResponse(
                    message = "response_annotation1",
                    code = 200,
                    response = UserRes.class)
    })
    private void updateUserById(RoutingContext ctx) {
        UserReq userReq = Json.decodeValue(ctx.getBody(), UserReq.class);
        UserRes userRes = new UserRes();
        userRes.setId(Integer.valueOf(ctx.request().getParam("id")));
        userRes.setName(userReq.getName() + " updated");
        ctx.response().putHeader("content-type", "application/json;charset=UTF-8")
                .end(Json.encodePrettily(userRes));
    }
}