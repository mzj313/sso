package com.chinasofti.oauth2.asserver.service;

import com.chinasofti.oauth2.asserver.entity.Client;
import com.chinasofti.oauth2.asserver.dao.ClientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 */
@Transactional
@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private ClientDao clientDao;

    @Override
    public Client createClient(Client client) {

        client.setClientId(UUID.randomUUID().toString());
        client.setClientSecret(UUID.randomUUID().toString());
        return clientDao.createClient(client);
    }

    @Override
    public Client updateClient(Client client) {
        return clientDao.updateClient(client);
    }

    @Override
    public void deleteClient(String id) {
        clientDao.deleteClient(id);
    }

    @Override
    public Client findOne(String id) {
        return clientDao.findOne(id);
    }

    @Override
    public List<Client> findAll() {
        return clientDao.findAll();
    }

    @Override
    public Client findByClientId(String clientId) {
        return clientDao.findByClientId(clientId);
    }

    @Override
    public Client findByClientSecret(String clientSecret) {
        return clientDao.findByClientSecret(clientSecret);
    }

    /**
     * 根据clientId 获取client信息
     * by makai
     *
     * @param clientId
     * @return
     */
    @Override
    public Client findOneByClientId(String clientId) {
        return clientDao.findOneByClientId(clientId);
    }
}
