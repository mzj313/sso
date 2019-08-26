package com.chinasofti.oauth2.client.util;

import com.chinasofti.oauth2.key.Base64;

import java.util.Random;

/**
 * Created by Administrator on 2015/5/11.
 */
public class StateGenerator {
    public static String genStateCode(){
        Random random = new Random();
        byte[] bytes = new byte[8];
        random.nextBytes(bytes);
        byte[] bs = Base64.encode(bytes);
        return new String(bs);
    }
}
