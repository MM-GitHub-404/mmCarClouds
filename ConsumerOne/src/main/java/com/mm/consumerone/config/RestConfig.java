package com.mm.consumerone.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author 茂茂
 * @create 2022-05-22 23:15
 */
@Configuration
public class RestConfig {

    /**
     * 创建RestTemplate的bean对象,设置负载均衡
     * @return 返回RestTemplate对象供controller调用
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
