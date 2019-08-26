package com.chinasofti.oauth2.asserver.service;

import com.chinasofti.oauth2.asserver.entity.PersistentRememberMeToken;

import java.util.List;

/**TODO 查询持久化登录对象service层
 * Created by yangkai on 15/5/4.
 */
public interface PersistentRememberMeTokenService {

    /**
     * 创建登录持久化对象
     * @param prmToken
     */
    public PersistentRememberMeToken createPrmToken(PersistentRememberMeToken prmToken);

    public PersistentRememberMeToken updatePrmToken(PersistentRememberMeToken prmToken);

    public void deletePrmToken(String series);

    PersistentRememberMeToken findOne(String series);

    List<PersistentRememberMeToken> findAll();

    /**
     * 根据用户名持久化对象
     * @param userName
     * @return
     */
    public PersistentRememberMeToken findByUserName(String userName);
}
