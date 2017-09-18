package com.bob.vertx.webapi.param;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by wangxiang on 17/9/8.
 */
public class Address {

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @ApiModelProperty(value = "省份", required = true)
    private String province;

    @ApiModelProperty(value = "城市", required = true)
    private String city;
}
