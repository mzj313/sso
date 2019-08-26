package com.chinasofti.oauth2.asserver.dao;

import com.chinasofti.oauth2.asserver.entity.Client;

import java.util.List;

/**
 */
public interface ClientDao {

    public Client createClient(Client client);
    public Client updateClient(Client client);
    public void deleteClient(String id);

    Client findOne(String id);

    List<Client> findAll();

    Client findByClientId(String clientId);
    Client findByClientSecret(String clientSecret);

    /**
     * 根据clientId 获取client
     * by makai
     * @param clientId
     * @return
     */
    Client findOneByClientId(String clientId);

}
