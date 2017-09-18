package com.bob.vertx.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.Objects;

/**
 * Created by wangxiang on 17/9/8.
 */
public final class ApplicationContextHolder implements ApplicationContextAware, DisposableBean {

    @Override
    public void destroy() throws Exception {
        ApplicationContextHolder.cleanApplicationContext();
    }

    @Override
    public void setApplicationContext(ApplicationContext appCtx) {
        Objects.requireNonNull(appCtx, "Application Context is required");
        ApplicationContextHolder.appCtx = appCtx;
    }

    public static ApplicationContext getAppCtx() {
        return appCtx;
    }

    private static ApplicationContext appCtx = null;

    private static synchronized void cleanApplicationContext() {
        appCtx = null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        checkApplicationContext();
        return (T) appCtx.getBean(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) throws BeansException {
        checkApplicationContext();
        return appCtx.getBean(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) throws BeansException {
        checkApplicationContext();
        return appCtx.getBeansOfType(clazz);
    }

    private static void checkApplicationContext() {
        if (appCtx == null) {
            throw new IllegalStateException("applicaitonContext未注入,请在applicationContext.xml中定义SpringContextHolder");
        }
    }
}