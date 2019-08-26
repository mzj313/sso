package com.chinasofti.oauth2.cache.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

public class HotspotRedisCacheManager extends RedisCacheManager
{
  private String dbname = "";

  private static final Logger logger = LoggerFactory.getLogger(HotspotRedisCacheManager.class)
    ;

  public Cache getCache(String table, String column)
  {
    if (logger.isDebugEnabled()) {
      logger.debug("获取表名为: [" + table + "]，列名为: [" + column + "] 的热区RedisCache实例");
    }
    if ((this.dbname == null) || ("".equals(this.dbname))) {
      throw new IllegalArgumentException("dbname can not be null or empty.");
    }
    return getCache("pdmi_" + this.dbname + "." + table + "_" + column + "_");
  }

  public String getDbname()
  {
    return this.dbname;
  }

  public void setDbname(String dbname) {
    this.dbname = dbname;
  }
}