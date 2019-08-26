package com.chinasofti.oauth2.asserver.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.util.Assert;

import com.chinasofti.oauth2.cache.redis.RedisClientWrapper;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class ShiroRedisCacheManager extends AbstractTransactionSupportingCacheManager {

	private static final Logger logger = LoggerFactory
			.getLogger(ShiroRedisCacheManager.class);

	private RedisClientWrapper cacheClient;

	/**
	 * The Redis key prefix for caches 
	 */
	private String keyPrefix = "shiro_redis_cache:";
	private Set<String> cacheNames;

	/**
	 * Returns the Redis session keys
	 * prefix.
	 * @return The prefix
	 */
	public String getKeyPrefix() {
		return keyPrefix;
	}

	/**
	 * Sets the Redis sessions key 
	 * prefix.
	 * @param keyPrefix The prefix
	 */
	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}
	
	@Override
	public Cache getCache(String name)  {
		logger.debug("获取名称为: " + name + " 的RedisCache实例");
		
		Cache c = super.getCache(name);
		
		if (c == null) {

			// initialize the Redis manager instance
			cacheClient.init(name);
			// create a new cache instance
			c = new ShiroSpringRedisCache(new RedisCache(cacheClient, keyPrefix), name);

			// add it to the cache collection
			this.addCache(c);
		}
		return c;
	}

	public RedisClientWrapper getCacheClient() {
		return cacheClient;
	}

	public void setCacheClient(RedisClientWrapper cacheClient) {
		this.cacheClient = cacheClient;
	}

	protected Collection<Cache> loadCaches() {
		Assert.notNull(this.cacheClient, "A backing EhCache CacheManager is required");
		Collection<String> names = this.cacheNames;
		LinkedHashSet caches = new LinkedHashSet(names.size());
		for(String name : names) {
			caches.add(this.getCache(name));
		}
		return caches;
	}

	public void setCacheNames(Set<String> cacheNames) {
		this.cacheNames = cacheNames;
	}
}
