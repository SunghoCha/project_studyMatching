package com.app.global.config.cache;

import org.ehcache.jsr107.EhcacheCachingProvider;
import javax.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URISyntaxException;
import java.net.URL;

//@Configuration
//@EnableCaching
public class EhcacheConfig {

    @Bean
    public CacheManager cacheManager() throws URISyntaxException {
        CachingProvider provider = Caching.getCachingProvider(EhcacheCachingProvider.class.getName());
        URL url = getClass().getResource("/ehcache.xml");
        return provider.getCacheManager(url.toURI(), getClass().getClassLoader());
    }
}
