package com.chinasofti.oauth2.asserver.service;

import com.chinasofti.oauth2.asserver.dao.PersistentRememberMeTokenDao;
import com.chinasofti.oauth2.asserver.entity.PersistentRememberMeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**TODO 查询持久化登录对象service层实现
 * Created by yangkai on 15/5/4.
 */
@Transactional
@Service()
public class PersistentRememberMeTokenServiceImpl implements PersistentRememberMeTokenService {

    @Autowired
    private PersistentRememberMeTokenDao persistentRememberMeTokenDao;

    @Override
    public PersistentRememberMeToken createPrmToken(PersistentRememberMeToken prmToken) {
        return persistentRememberMeTokenDao.createPrmToken(prmToken);
    }

    @Override
    public PersistentRememberMeToken updatePrmToken(PersistentRememberMeToken prmToken) {
        return persistentRememberMeTokenDao.updatePrmToken(prmToken);
    }

    @Override
    public void deletePrmToken(String series) {
        persistentRememberMeTokenDao.deletePrmToken(series);
    }

    @Override
    public PersistentRememberMeToken findOne(String series) {
        return persistentRememberMeTokenDao.findOne(series);
    }

    @Override
    public List<PersistentRememberMeToken> findAll() {
        return persistentRememberMeTokenDao.findAll();
    }

    @Override
    public PersistentRememberMeToken findByUserName(String userName) {
        return persistentRememberMeTokenDao.findOneByUserName(userName);
    }
}
