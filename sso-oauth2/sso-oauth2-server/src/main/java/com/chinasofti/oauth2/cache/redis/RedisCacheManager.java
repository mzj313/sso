package com.chinasofti.oauth2.cache.redis;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.util.Assert;

public class RedisCacheManager extends AbstractTransactionSupportingCacheManager
{
  private static final Logger logger = LoggerFactory.getLogger(RedisCacheManager.class)
    ;
  private RedisClientWrapper cacheClient;
  private Set<String> cacheNames;

  public Cache getCache(String name)
  {
    logger.debug("获取名称为: " + name + " 的RedisCache实例");

    Cache c = super.getCache(name);

    if (c == null)
    {
      this.cacheClient.init(name);

      c = new SpringRedisCache(this.cacheClient, name);

      addCache(c);
    }
    return c;
  }

  public RedisClientWrapper getCacheClient()
  {
    return this.cacheClient;
  }

  public void setCacheClient(RedisClientWrapper cacheClient)
  {
    this.cacheClient = cacheClient;
  }

  protected Collection<Cache> loadCaches()
  {
    Assert.notNull(this.cacheClient, "A backing Redis CacheManager is required");
    Set<String> names = this.cacheNames;
    LinkedHashSet caches = new LinkedHashSet(names.size());
    for (String name : names) {
      caches.add(getCache(name));
    }
    return caches;
  }

  public void setCacheNames(Set<String> cacheNames)
  {
    this.cacheNames = cacheNames;
  }
}