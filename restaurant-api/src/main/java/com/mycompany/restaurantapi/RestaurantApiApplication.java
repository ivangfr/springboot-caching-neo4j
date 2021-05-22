package com.mycompany.restaurantapi;

import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.framework.Advised;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.DecoratingProxy;
import org.springframework.nativex.hint.ProxyHint;
import org.springframework.nativex.hint.TypeHint;

import javax.cache.annotation.CacheMethodDetails;

@TypeHint(types = {
        CacheMethodDetails.class,
        LoggerConfig.class
})
@ProxyHint(types = {
        SpringProxy.class,
        Advised.class,
        DecoratingProxy.class
})
@SpringBootApplication
public class RestaurantApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantApiApplication.class, args);
    }

}
