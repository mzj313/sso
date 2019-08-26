package com.chinasofti.oauth2.asserver.service;

import com.chinasofti.oauth2.asserver.entity.PhoneAuth;

/**
 * Created by yangkai on 15/6/15.
 */
public interface PhoneService {

    public PhoneAuth findPhoneAuth(String phoneId);

    public void addPhoneAuth(String phoneId,PhoneAuth phoneAuth, int expire);

    public void removePhoneAuth(String phoneId);

    public void refreshExpire(String phoneId, int expire);

}
