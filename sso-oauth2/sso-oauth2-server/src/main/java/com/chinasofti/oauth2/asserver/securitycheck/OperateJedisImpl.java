package com.chinasofti.oauth2.asserver.securitycheck;

import com.chinasofti.oauth2.asserver.utils.JedisUtil;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

/**
 * Created by xueyong on 15/6/24.
 */

@Component
public class OperateJedisImpl implements ICheckOperate {

    @Override
    public String getFromJedis(String key) {
        return JedisUtil.getString(key);
    }

    @Override
    public void saveToJedis(String key, Integer seconds, String value) {
        JedisUtil.saveString(key,value,seconds);
    }

    @Override
    public void saveToJedis(String key, String value) {
        JedisUtil.saveString(key,value);
    }

    @Override
    public void removeFromJedis(String key) {
        JedisUtil.removeObject(key);

    }
}
