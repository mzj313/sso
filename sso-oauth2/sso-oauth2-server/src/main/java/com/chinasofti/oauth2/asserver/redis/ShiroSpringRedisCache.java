package com.chinasofti.oauth2.asserver.redis;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

/**
 * Created by liuzhuo on 2015/8/5.
 */
public class ShiroSpringRedisCache implements Cache {

    private final RedisCache cache;
    private final String name;

    public ShiroSpringRedisCache(RedisCache cache, String name) {
        Assert.notNull(cache, "Ehcache must not be null");
        this.cache = cache;
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    public RedisCache getNativeCache() {
        return this.cache;
    }

    public ValueWrapper get(Object key) {
        Object element = this.cache.get(key);
        return element != null?new SimpleValueWrapper(element):null;
    }

    public <T> T get(Object key, Class<T> type) {
        T element = (T) this.cache.get(key);
        if(type != null && !type.isInstance(element)) {
            throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + element);
        } else {
            return element;
        }
    }

    public void put(Object key, Object value) {
        this.cache.put(key, value);
    }

    // @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        ValueWrapper wrapper = this.get(key);
        if (wrapper == null) {
            this.put(key, value);
            wrapper = new SimpleValueWrapper(value);
        }
        return wrapper;
    }

    public void evict(Object key) {
        this.cache.remove(key);
    }

    public void clear() {
        this.cache.removeAll();
    }
}
