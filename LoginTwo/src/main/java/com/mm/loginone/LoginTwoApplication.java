package com.mm.loginone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.mm.loginone", "com.mm.commoncar"})
public class LoginTwoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginTwoApplication.class, args);
    }

}
