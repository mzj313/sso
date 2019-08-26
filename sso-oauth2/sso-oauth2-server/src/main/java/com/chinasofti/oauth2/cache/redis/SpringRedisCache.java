package com.chinasofti.oauth2.cache.redis;

import java.util.Set;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.chinasofti.oauth2.cache.CacheException;

public class SpringRedisCache
  implements Cache
{
  private final RedisClientWrapper cache;
  private final String name;

  public SpringRedisCache(RedisClientWrapper cache, String name)
  {
    Assert.notNull(cache, "cache must not be null");
    this.cache = cache;
    this.name = name;
  }

  public String getName()
  {
    return this.name;
  }

  public RedisClientWrapper getNativeCache()
  {
    return this.cache;
  }

  public Cache.ValueWrapper get(Object key)
  {
    byte[] element = this.cache.get(getByteKey(key));
    return element != null ? new SimpleValueWrapper(SerializeUtils.deserialize(element)) : null;
  }

  public <T> T get(Object key, Class<T> type)
  {
    Object element = SerializeUtils.deserialize(this.cache.get(getByteKey(key)));
    if ((type != null) && (!type.isInstance(element))) {
      throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + element);
    }
    return (T) element;
  }

  public void put(Object key, Object value)
  {
    this.cache.set(getByteKey(key), SerializeUtils.serialize(value));
  }

  public Cache.ValueWrapper putIfAbsent(Object key, Object value)
  {
    Cache.ValueWrapper wrapper = get(key);
    if (wrapper == null) {
      put(key, value);
      wrapper = new SimpleValueWrapper(value);
    }
    return wrapper;
  }

  public void evict(Object key)
  {
    this.cache.del(getByteKey(key));
  }

  public void clear()
  {
    try
    {
      Set<byte[]> keys = this.cache.keys(getName() + "*");
      if (!CollectionUtils.isEmpty(keys))
        for (byte[] key : keys)
          this.cache.del(key);
    }
    catch (Throwable t)
    {
      throw new CacheException(t);
    }
  }

  private byte[] getByteKey(Object key)
  {
    if ((key instanceof String)) {
      String preKey = getName() + key;
      return preKey.getBytes();
    }
    return SerializeUtils.serialize(key);
  }
}