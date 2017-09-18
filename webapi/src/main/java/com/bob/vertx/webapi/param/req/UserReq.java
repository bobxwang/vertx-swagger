package com.bob.vertx.webapi.param.req;

import com.bob.vertx.webapi.param.Address;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by wangxiang on 17/9/8.
 */
@ApiModel(description = "User Request")
public class UserReq {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(value = "姓名", required = true)
    private String name;

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    @ApiModelProperty(value = "地址，测试复杂类型", required = true)
    private List<Address> address;
}