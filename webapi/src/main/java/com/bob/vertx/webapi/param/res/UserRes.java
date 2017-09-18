package com.bob.vertx.webapi.param.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by wangxiang on 17/9/6.
 */
@ApiModel(description = "User Response")
public class UserRes {

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty("标识符")
    private Integer id;

    @ApiModelProperty(value = "姓名", required = true)
    private String name;
}