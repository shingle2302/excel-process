package com.excel.controller;

import com.excel.entity.Client;
import com.excel.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping
    public Client createClient(@RequestBody Client client) {
        return clientService.createClient(client);
    }

    @GetMapping
    public List<Client> getClients() {
        return clientService.list();
    }

    @GetMapping("/{id}")
    public Client getClient(@PathVariable Long id) {
        return clientService.getById(id);
    }

    @PutMapping("/{id}")
    public Client updateClient(@PathVariable Long id, @RequestBody Client client) {
        client.setId(id);
        clientService.updateById(client);
        return client;
    }

    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable Long id) {
        clientService.removeById(id);
    }

    @GetMapping("/client-id/{clientId}")
    public Client getClientByClientId(@PathVariable String clientId) {
        return clientService.getByClientId(clientId).orElse(null);
    }

    @PutMapping("/client-id/{clientId}/status")
    public void updateClientStatus(@PathVariable String clientId, @RequestParam String status) {
        clientService.updateClientStatus(clientId, status);
    }
}