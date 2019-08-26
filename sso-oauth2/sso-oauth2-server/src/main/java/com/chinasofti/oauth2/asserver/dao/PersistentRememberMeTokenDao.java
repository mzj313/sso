package com.chinasofti.oauth2.asserver.dao;

import com.chinasofti.oauth2.asserver.entity.PersistentRememberMeToken;

import java.util.List;

/**TODO 查询持久化登录对象dao层
 * Created by yangkai on 15/5/4.
 */
public interface PersistentRememberMeTokenDao {

    public PersistentRememberMeToken createPrmToken(PersistentRememberMeToken prmToken);
    public PersistentRememberMeToken updatePrmToken(PersistentRememberMeToken prmToken);
    public void deletePrmToken(String series);

    PersistentRememberMeToken findOne(String series);

    PersistentRememberMeToken findOneByUserName(String userName);

    List<PersistentRememberMeToken> findAll();
}
