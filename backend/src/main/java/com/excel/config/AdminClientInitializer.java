package com.excel.config;

import com.excel.entity.Client;
import com.excel.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminClientInitializer implements CommandLineRunner {

    @Autowired
    private ClientService clientService;

    @Override
    public void run(String... args) {
        clientService.getByClientId("admin").ifPresentOrElse(
                client -> {
                    if (!"启用".equals(client.getStatus())) {
                        clientService.updateClientStatus("admin", "启用");
                    }
                },
                () -> {
                    Client admin = new Client();
                    admin.setClientId("admin");
                    admin.setClientSecret("admin123");
                    admin.setClientName("系统管理员");
                    admin.setClientDesc("系统默认管理员账号");
                    clientService.createClient(admin);
                }
        );
    }
}
