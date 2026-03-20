package com.excel.controller;

import com.excel.annotation.OperationLog;
import com.excel.annotation.RequirePermission;
import com.excel.entity.Client;
import com.excel.service.ClientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "客户端管理")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping
    @RequirePermission("client:write")
    @OperationLog(type = "客户端", value = "创建客户端")
    public Client createClient(@RequestBody Client client) {
        return clientService.createClient(client);
    }

    @GetMapping
    @RequirePermission("client:read")
    public List<Client> getClients() {
        return clientService.list();
    }

    @GetMapping("/{id}")
    @RequirePermission("client:read")
    public Client getClient(@PathVariable Long id) {
        return clientService.getById(id);
    }

    @PutMapping("/{id}")
    @RequirePermission("client:write")
    @OperationLog(type = "客户端", value = "更新客户端")
    public Client updateClient(@PathVariable Long id, @RequestBody Client client) {
        client.setId(id);
        clientService.updateById(client);
        return client;
    }

    @DeleteMapping("/{id}")
    @RequirePermission("client:write")
    @OperationLog(type = "客户端", value = "删除客户端")
    public void deleteClient(@PathVariable Long id) {
        clientService.removeById(id);
    }

    @GetMapping("/client-id/{clientId}")
    @RequirePermission("client:read")
    public Client getClientByClientId(@PathVariable String clientId) {
        return clientService.getByClientId(clientId).orElse(null);
    }

    @PutMapping("/client-id/{clientId}/status")
    @RequirePermission("client:write")
    @OperationLog(type = "客户端", value = "更新客户端状态")
    public void updateClientStatus(@PathVariable String clientId, @RequestParam String status) {
        clientService.updateClientStatus(clientId, status);
    }
}
