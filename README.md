# vertx-swagger
> 将swagger引入vert.x-web的一个组件包, 参照spring-boot的写法, 同时添加自定义注解**BBRouter**, 减少Spring开发者学习vertx-web的成本

* swagger/definition.json
* v2/api-docs
> 此地址就像spring-boot结合springfox一样所暴露出来的v2/api-docs, 返回swagger的json说明
* static/index.html 
> 此地址就像spring-boot结合springfox一样所暴露出来的swagger-ui.html, 用于swagger测试 

# webapi
> 一个示例工程, 如何使用vertx-swagger组件包
* IndexVerticle/WorkVerticle
> indexVerticle收到请求后使用eventbus/service proxy入队,workVerticle进行处理
* UserVerticle
> 这是一个非常像Spring-Controller写法的verticle, 利用**BBRouter**进行路由注册, 就像**RequestMapping**一样
* RestVerticle
> 这是一个vert.x-web路由常规写法的verticle, 当然里面也使用了**BBRouter**的形式, 可以共存 

#### io.rest-assured 
> 一个http rest的测试库