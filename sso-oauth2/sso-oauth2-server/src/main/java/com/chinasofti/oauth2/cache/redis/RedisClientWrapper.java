package com.chinasofti.oauth2.cache.redis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.springframework.beans.InvalidPropertyException;

import com.chinasofti.oauth2.cache.NotSupportInClusterException;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisClientWrapper
{
  private String host = "127.0.0.1";

  private int port = 6379;

  private int expire = 0;

  private int timeout = 0;

  private String password = "";

  private static HashMap<String, JedisPool> jedisPoolMap = new HashMap(0);

  private static HashMap<String, JedisCluster> jedisClusterMap = new HashMap(0);

  private String cacheName = "";
  private Set<String> hosts;
  private boolean isCluster = false;

  public void init(String name)
  {
    this.cacheName = name;
    Set haps = null;
    if (this.hosts != null)
    {
      haps = new HashSet(this.hosts.size());
      for (String hostString : this.hosts) {
        HostAndPort hap = parseHostAndPort(hostString);
        haps.add(hap);
      }
      JedisCluster jc = null;
      if (this.timeout == 0)
        jc = new JedisCluster(haps);
      else {
        jc = new JedisCluster(haps, this.timeout);
      }

      this.isCluster = true;
      jedisClusterMap.put(name, jc);
    }

    if ((haps == null) || (haps.size() == 0))
      addToJedisPool(this.cacheName, this.host, this.port);
  }

  private void addToJedisPool(String name, String _host, int _port)
  {
    JedisPool jedisPool = (JedisPool)jedisPoolMap.get(name);
    if (jedisPool == null) {
      if ((this.password != null) && (!"".equals(this.password)))
        jedisPool = new JedisPool(new JedisPoolConfig(), _host, _port, this.timeout, this.password);
      else if (this.timeout != 0)
        jedisPool = new JedisPool(new JedisPoolConfig(), _host, _port, this.timeout);
      else {
        jedisPool = new JedisPool(new JedisPoolConfig(), _host, _port);
      }
      jedisPoolMap.put(name, jedisPool);
    }
  }

  private HostAndPort parseHostAndPort(String hostString) {
    StringTokenizer st = new StringTokenizer(hostString, ":");
    if ((st.hasMoreElements() & st.countTokens() == 2)) {
      return new HostAndPort(st.nextToken(), Integer.valueOf(st.nextToken()).intValue());
    }
    throw new InvalidPropertyException(getClass(), "hosts", "invalid host name:" + hostString + ".");
  }

  public Map<String, String> hgetAll(String key)
  {
    if (this.isCluster) {
      return hgetAllCluster(key);
    }
    return hgetAllSingle(key);
  }

  private Map<String, String> hgetAllCluster(String key)
  {
    JedisCluster jc = getJedisCluster();
    return jc.hgetAll(key);
  }

  private Map<String, String> hgetAllSingle(String key) {
    Map value = null;
    JedisPool jedisPool = getJedisPool();
    Jedis jedis = jedisPool.getResource();
    try {
      value = jedis.hgetAll(key);
    } finally {
      jedis.close();
    }
    return value;
  }

  public byte[] get(byte[] key)
  {
    if (this.isCluster) {
      return getCluster(key);
    }
    return getSingle(key);
  }

  private byte[] getCluster(byte[] key)
  {
    JedisCluster jc = getJedisCluster();
    return jc.get(new String(key)).getBytes();
  }
  
  private byte[] getSingle(byte[] key) {
    byte[] value = null;
    JedisPool jedisPool = getJedisPool();
    Jedis jedis = jedisPool.getResource();
    try {
      value = jedis.get(key);
    } finally {
      jedis.close();
    }
    return value;
  }

  public byte[] set(byte[] key, byte[] value)
  {
    if (this.isCluster) {
      return setCluster(key, value, this.expire);
    }
    return setSingle(key, value, this.expire);
  }

  public byte[] set(byte[] key, byte[] value, int expire)
  {
    if (this.isCluster) {
      return setCluster(key, value, expire);
    }
    return setSingle(key, value, expire);
  }

  private byte[] setCluster(byte[] key, byte[] value, int expire)
  {
    JedisCluster jc = getJedisCluster();
    jc.set(new String(key), new String(value));
    if (expire != 0) {
      jc.expire(new String(key), expire);
    }
    return value;
  }

  private byte[] setSingle(byte[] key, byte[] value, int expire) {
    JedisPool jedisPool = getJedisPool();
    Jedis jedis = jedisPool.getResource();
    try {
      jedis.set(key, value);
      if (expire != 0)
        jedis.expire(key, expire);
    }
    finally {
      jedis.close();
    }
    return value;
  }

  public void del(byte[] key)
  {
    if (this.isCluster)
      delCluster(key);
    else
      delSingle(key);
  }

  private void delCluster(byte[] key)
  {
    JedisCluster jc = getJedisCluster();
    jc.del(new String(key));
  }

  private void delSingle(byte[] key) {
    JedisPool jedisPool = getJedisPool();
    Jedis jedis = jedisPool.getResource();
    try {
      jedis.del(key);
    } finally {
      jedis.close();
    }
  }

  public void flushDB()
  {
    if (this.isCluster)
      flushDBCluster();
    else
      flushDBSingle();
  }

  private void flushDBCluster()
  {
    throw new NotSupportInClusterException("flushdb");
  }

  private void flushDBSingle() {
    JedisPool jedisPool = getJedisPool();
    Jedis jedis = jedisPool.getResource();
    try {
      jedis.flushDB();

      jedis.close(); } finally { jedis.close(); }

  }

  public Long dbSize()
  {
    if (this.isCluster) {
      return getDbSizeCluster();
    }
    return getDbSizeSingle();
  }

  private Long getDbSizeCluster()
  {
    throw new NotSupportInClusterException("dbsize");
  }

  private Long getDbSizeSingle() {
    Long dbSize = Long.valueOf(0L);
    JedisPool jedisPool = getJedisPool();
    Jedis jedis = jedisPool.getResource();
    try {
      dbSize = jedis.dbSize();
    } finally {
      jedis.close();
    }
    return dbSize;
  }

  public Set<byte[]> keys(String pattern)
  {
    if (this.isCluster) {
      return getKeysCluster(pattern);
    }
    return getKeysSingle(pattern);
  }

  private Set<byte[]> getKeysCluster(String pattern)
  {
    throw new NotSupportInClusterException("keys");
  }

  private Set<byte[]> getKeysSingle(String pattern) {
    Set keys = null;
    JedisPool jedisPool = getJedisPool();
    Jedis jedis = jedisPool.getResource();
    try {
      keys = jedis.keys(pattern.getBytes());
    } finally {
      jedis.close();
    }
    return keys;
  }

  private JedisPool getJedisPool() {
    return (JedisPool)jedisPoolMap.get(this.cacheName);
  }

  private JedisCluster getJedisCluster() {
    return (JedisCluster)jedisClusterMap.get(this.cacheName);
  }

  public long ttl(String key)
  {
    long ttl = 0L;
    JedisPool jedisPool = getJedisPool();
    Jedis jedis = jedisPool.getResource();
    try {
      ttl = jedis.ttl(key).longValue();
    } finally {
      jedis.close();
    }
    return ttl;
  }

  public String getHost()
  {
    return this.host;
  }

  public void setHost(String host)
  {
    this.host = host;
  }

  public int getPort()
  {
    return this.port;
  }

  public void setPort(int port)
  {
    this.port = port;
  }

  public int getExpire()
  {
    return this.expire;
  }

  public void setExpire(int expire)
  {
    this.expire = expire;
  }

  public int getTimeout()
  {
    return this.timeout;
  }

  public void setTimeout(int timeout)
  {
    this.timeout = timeout;
  }

  public String getPassword()
  {
    return this.password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public Set<String> getHosts()
  {
    return this.hosts;
  }

  public void setHosts(Set<String> hosts) {
    this.hosts = hosts;
  }

  public boolean isCluster() {
    return this.isCluster;
  }

  public String hmset(String key, Map value)
  {
    if (this.isCluster) {
      return setClusterhmset(key, value);
    }
    return setKeysSinglehmset(key, value);
  }

  private String setKeysSinglehmset(String key, Map value)
  {
    JedisPool jedisPool = getJedisPool();
    Jedis jedis = jedisPool.getResource();
    try {
      return jedis.hmset(key, value);
    }
    finally {
      jedis.close();
    }
  }

  public String setClusterhmset(String key, Map objecthash)
  {
    JedisCluster jc = getJedisCluster();
    return jc.hmset(key, objecthash);
  }
}