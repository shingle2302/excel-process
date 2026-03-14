package com.excel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.excel.entity.Client;
import com.excel.mapper.ClientMapper;
import com.excel.service.ClientService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {

    @Override
    public Optional<Client> getByClientId(String clientId) {
        LambdaQueryWrapper<Client> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Client::getClientId, clientId);
        Client client = baseMapper.selectOne(wrapper);
        return Optional.ofNullable(client);
    }

    @Override
    public boolean validateClient(String clientId, String clientSecret) {
        Optional<Client> clientOptional = getByClientId(clientId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            return "启用".equals(client.getStatus()) && clientSecret.equals(client.getClientSecret());
        }
        return false;
    }

    @Override
    public boolean isActiveClient(String clientId) {
        Optional<Client> clientOptional = getByClientId(clientId);
        return clientOptional.filter(client -> "启用".equals(client.getStatus())).isPresent();
    }

    @Override
    public Client createClient(Client client) {
        client.setStatus("启用");
        client.setCreateTime(LocalDateTime.now());
        client.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(client);
        return client;
    }

    @Override
    public void updateClientStatus(String clientId, String status) {
        LambdaQueryWrapper<Client> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Client::getClientId, clientId);
        Client client = new Client();
        client.setStatus(status);
        client.setUpdateTime(LocalDateTime.now());
        baseMapper.update(client, wrapper);
    }
}