package com.chinasofti.oauth2.asserver.securitycheck;

import com.chinasofti.oauth2.asserver.utils.ApplicationUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by xueyong on 15/6/9.
 */
@Component
public class ClientSecretSecurityCheck extends SecurityCheck {

    private static Logger logger= LoggerFactory.getLogger(ClientSecretSecurityCheck.class);
    private static final String IP_PREFIX = "server_client_secret_prefix_";
    private static final String INTERCEPT_PREFIX = "server_client_secret_intercept_prefix_";
    @Autowired
    private ICheckOperate operateJedisImpl;

    @Override
    public boolean isReplyToken(HttpServletRequest request) {
        String remote_ip = getIpAddr(request);
        String jedisValue = operateJedisImpl.getFromJedis(IP_PREFIX + remote_ip);
        int count = Integer.valueOf(StringUtils.isBlank(jedisValue) ? "0" : jedisValue);
        String accessTimes = ApplicationUtil.getValue("access_times", ACCESS_TIMES);
        String interceptTime = ApplicationUtil.getValue("intercept_time",INTERCEPT_TIME);
        if(count >= Integer.valueOf(accessTimes)){//如果访问失败次数大于限制数
            operateJedisImpl.saveToJedis(INTERCEPT_PREFIX + remote_ip, Integer.valueOf(interceptTime), String.valueOf(count));
            removeKey(request);
            logger.info("IP："+remote_ip +" 次数超过限制");
            return false;
        }else{
            operateJedisImpl.saveToJedis(IP_PREFIX +remote_ip, String.valueOf(++count));
            logger.info("计数器累加："+count);
            return true;
        }
    }

    @Override
    public boolean isFailureByIntercept(HttpServletRequest request) {
        String remote_ip = getIpAddr(request);
        String obj = operateJedisImpl.getFromJedis(INTERCEPT_PREFIX + remote_ip);
        return StringUtils.isNotBlank(obj);//obj true is fail
    }

    @Override
    public void removeKey(HttpServletRequest request) {
        String remote_ip = getIpAddr(request);
        operateJedisImpl.removeFromJedis(IP_PREFIX + remote_ip);
    }

    public void setOperateJedisImpl(ICheckOperate operateJedisImpl) {
        this.operateJedisImpl = operateJedisImpl;
    }
}
