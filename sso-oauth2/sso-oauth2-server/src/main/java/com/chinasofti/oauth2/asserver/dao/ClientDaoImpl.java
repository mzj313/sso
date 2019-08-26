package com.chinasofti.oauth2.asserver.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.chinasofti.oauth2.asserver.entity.Client;

@SuppressWarnings({"unchecked","rawtypes"})
@Repository
public class ClientDaoImpl implements ClientDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public Client createClient(final Client client) {
        final String sql = "insert into t_app(id, name, url, display_order, comment" +
                ", access_group_id, secret_key_private, secret_key_public, createtime" +
                ", code, client_id, client_secret, scope, access_token_validity,refresh_token_validity) " +
                "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        jdbcTemplate.update(
                sql,
                client.getId(), client.getName(), client.getUrl(), client.getDisplayOrder(), client.getComment()
                , client.getAccessGroupId(), client.getSecretKeyPrivate(), client.getSecretKeyPublic(), client.getCreatetime()
                , client.getCode(), client.getClientId(), client.getClientSecret(), client.getScope(), client.getAccessTokenValidity()
                , client.getRefreshTokenValidity());
        return client;
    }

    public Client updateClient(Client client) {
        String sql = "update t_app set name=?, url=?, display_order=?, comment=?, access_group_id=?, secret_key_private=?" +
                ", secret_key_public=?, createtime=?, code=?, client_id=?, client_secret=?, scope=?" +
                ", access_token_validity=?, refresh_token_validity=? where id=?";
        jdbcTemplate.update(
                sql,
                client.getName(), client.getUrl(), client.getDisplayOrder(), client.getComment()
                , client.getAccessGroupId(), client.getSecretKeyPrivate(), client.getSecretKeyPublic(), client.getCreatetime()
                , client.getCode(), client.getClientId(), client.getClientSecret(), client.getScope(), client.getAccessTokenValidity()
                , client.getRefreshTokenValidity(),client.getId());
        return client;
    }

    public void deleteClient(String id) {
        String sql = "delete from t_app where id=?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Client findOne(String id) {
        String sql = "select id, name, url, display_order, comment" +
                ", access_group_id, secret_key_private, secret_key_public, createtime" +
                ", code, client_id, client_secret, scope, access_token_validity,refresh_token_validity " +
                "from t_app where id=?";
		List<Client> clientList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Client.class), id);
        if(clientList.size() == 0) {
            return null;
        }
        return clientList.get(0);
    }

    @Override
    public List<Client> findAll() {
        String sql = "select id, name, url, display_order, comment" +
                ", access_group_id, secret_key_private, secret_key_public, createtime" +
                ", code, client_id, client_secret, scope, access_token_validity,refresh_token_validity " +
                "from t_app";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper(Client.class));
    }


    @Override
    public Client findByClientId(String clientId) {
        String sql = "select id, name, url, display_order, comment" +
                ", access_group_id, secret_key_private, secret_key_public, createtime" +
                ", code, client_id, client_secret, scope, access_token_validity,refresh_token_validity " +
                "from t_app where client_id=?";
        List<Client> clientList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Client.class), clientId);
        if(clientList.size() == 0) {
            return null;
        }
        return clientList.get(0);
    }


    @Override
    public Client findByClientSecret(String clientSecret) {
        String sql = "select id, name, url, display_order, comment" +
                ", access_group_id, secret_key_private, secret_key_public, createtime" +
                ", code, client_id, client_secret, scope, access_token_validity,refresh_token_validity " +
                "from t_app where client_secret=?";
        List<Client> clientList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Client.class), clientSecret);
        if(clientList.size() == 0) {
            return null;
        }
        return clientList.get(0);
    }

    /**
     * 根据clientId 获取client
     * by makai
     *
     * @param clientId
     * @return
     */
    @Override
    public Client findOneByClientId(String clientId) {
        String sql = "select id, name, url, display_order, comment" +
                ", access_group_id, secret_key_private, secret_key_public, createtime" +
                ", code, client_id, client_secret, scope, access_token_validity,refresh_token_validity " +
                "from t_app where client_id=?";
        List<Client> clientList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Client.class), clientId);
        if(clientList.size() == 0) {
            return null;
        }
        return clientList.get(0);
    }
}
