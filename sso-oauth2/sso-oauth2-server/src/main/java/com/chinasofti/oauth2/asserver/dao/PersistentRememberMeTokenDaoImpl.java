package com.chinasofti.oauth2.asserver.dao;

import com.chinasofti.oauth2.asserver.entity.PersistentRememberMeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Repository;

import java.util.List;

/**TODO 查询持久化登录对象dao层实现
 * Created by yangkai on 15/5/4.
 */
@Repository
public class PersistentRememberMeTokenDaoImpl implements PersistentRememberMeTokenDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public PersistentRememberMeToken createPrmToken(PersistentRememberMeToken prmToken) {
        final String sql = "insert into persistent_logins(username, series, token, last_used) values(?,?,?,?)";

        jdbcTemplate.update(
                sql,
                prmToken.getUsername(),prmToken.getSeries(),prmToken.getTokenValue(),prmToken.getDate());
        return prmToken;
    }

    @Override
    public PersistentRememberMeToken updatePrmToken(PersistentRememberMeToken prmToken) {
        String sql = "update persistent_logins set username=?, token=?, last_used=? where series=?";
        jdbcTemplate.update(
                sql,
                prmToken.getUsername(), prmToken.getTokenValue(),prmToken.getDate(), prmToken.getSeries());
        return prmToken;
    }

    @Override
    public void deletePrmToken(String series) {
        String sql = "delete from persistent_logins where series=?";
        jdbcTemplate.update(sql, series);
    }

    @Override
    public PersistentRememberMeToken findOne(String series) {
        String sql = "select username, series, token tokenValue from persistent_logins where series=?";
        List<PersistentRememberMeToken> prmTokenList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(PersistentRememberMeToken.class), series);
        if(prmTokenList.size() == 0) {
            return null;
        }
        return prmTokenList.get(0);
    }

    @Override
    public PersistentRememberMeToken findOneByUserName(String userName) {
        String sql = "select username, series, token tokenValue from persistent_logins where username=?";
        List<PersistentRememberMeToken> prmTokenList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(PersistentRememberMeToken.class), userName);
        if(prmTokenList.size() == 0) {
            return null;
        }
        return prmTokenList.get(0);
    }

    @Override
    public List<PersistentRememberMeToken> findAll() {
        String sql = "select username, series, token tokenValue from persistent_logins";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper(PersistentRememberMeToken.class));
    }
}
