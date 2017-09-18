package com.bob.vertx.swagger;

import com.google.common.collect.Lists;
import io.swagger.config.Scanner;
import io.vertx.core.Verticle;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wangxiang on 17/9/6.
 */
public class RouteScanner implements Scanner {

    private boolean prettyPrint = false;
    private List<String> resourcePackages;

    public RouteScanner(List<String> resourcePackages) {
        this.resourcePackages = resourcePackages;
    }

    public RouteScanner(String resourcePackage) {
        this(Lists.newArrayList(resourcePackage));
    }

    @Override
    public Set<Class<?>> classes() {
        Set<Class<?>> ss = new HashSet<>();
        for (String temp : resourcePackages) {
            Set<Class<? extends Verticle>> s = new Reflections(temp).getSubTypesOf(Verticle.class);
            for (Class<? extends Verticle> obj : s) {
                if (!obj.getName().startsWith(temp)) {
                    continue;
                }
                ss.add(obj);
            }
        }
        return ss;
    }

    @Override
    public boolean getPrettyPrint() {
        return prettyPrint;
    }

    @Override
    public void setPrettyPrint(boolean shouldPrettyPrint) {
        this.prettyPrint = shouldPrettyPrint;
    }
}