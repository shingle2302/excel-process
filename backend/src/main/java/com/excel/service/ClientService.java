package com.excel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.excel.entity.Client;

import java.util.Optional;

public interface ClientService extends IService<Client> {
    Optional<Client> getByClientId(String clientId);
    boolean validateClient(String clientId, String clientSecret);
    boolean isActiveClient(String clientId);
    Client createClient(Client client);
    void updateClientStatus(String clientId, String status);
}