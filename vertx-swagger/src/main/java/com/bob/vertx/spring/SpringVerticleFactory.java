package com.bob.vertx.spring;

import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

/**
 * Created by wangxiang on 17/9/4.
 */
public class SpringVerticleFactory implements VerticleFactory {

    /**
     * Usually verticle instantiation is fast but since our verticles are Spring Beans,
     * they might depend on other beans/resources which are slow to build/lookup.
     *
     * @return
     */
    @Override
    public boolean blockingCreate() {
        return true;
    }

    @Override
    public String prefix() {
        return "java";
    }

    @Override
    public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
        Objects.requireNonNull(verticleName, "Verticle Name is required");
        verticleName = VerticleFactory.removePrefix(verticleName);
        Objects.requireNonNull(verticleName, "Verticle Name must be more than just the prefix");
        ApplicationContext ctx = ApplicationContextHolder.getAppCtx();
        if (!ctx.containsBean(verticleName))
            throw new IllegalArgumentException(String.format("No bean found for %s", verticleName));

        if (!ctx.isPrototype(verticleName))
            throw new IllegalArgumentException(String.format("Bean %s needs to be of Prototype scope", verticleName));

        return (Verticle) ctx.getBean(verticleName);
    }
}