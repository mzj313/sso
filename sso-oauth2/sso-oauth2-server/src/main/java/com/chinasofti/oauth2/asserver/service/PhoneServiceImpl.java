package com.chinasofti.oauth2.asserver.service;

import com.chinasofti.oauth2.asserver.entity.PhoneAuth;
import com.chinasofti.oauth2.asserver.utils.JedisUtil;
import com.chinasofti.oauth2.asserver.utils.SerializeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

/**
 * Created by yangkai on 15/6/15.
 */
/**
 */
@Transactional
@Service
public class PhoneServiceImpl implements PhoneService{


    public PhoneAuth findPhoneAuth(String phoneId){
        PhoneAuth phoneAuth = null;
        phoneAuth = (PhoneAuth)JedisUtil.getObject(phoneId);

        return phoneAuth;

    }

    public void addPhoneAuth(String phoneId,PhoneAuth phoneAuth, int expire){

        JedisUtil.saveObject(phoneId, phoneAuth, expire);
    }

    public void removePhoneAuth(String phoneId){
        JedisUtil.removeObject(phoneId);
    }

    public void refreshExpire(String phoneId, int expire){
        JedisUtil.refreshObject(phoneId,expire);
    }
}
