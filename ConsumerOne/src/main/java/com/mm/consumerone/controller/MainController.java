package com.mm.consumerone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

/**
 * @author 茂茂
 * @create 2022-05-18 17:46
 */
@Controller
@RequestMapping(value = "/main")
public class MainController {

    //远程微服务的注册中心地址
    String loginService = "http://Login";

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/order")
    public String order() {
        String string = restTemplate.getForObject(
                loginService + "/main/order", String.class);
        return string;
    }

    @RequestMapping(value = "/bulletin")
    public String notice() {
        String string = restTemplate.getForObject(
                loginService + "/main/bulletin", String.class);
        return string;
    }

    @RequestMapping(value = "/quit")
    public String quit(HttpSession session) {
        String string = restTemplate.getForObject(
                loginService + "/main/quit", String.class);
        //退出登录时删除登录认证信息
        session.removeAttribute("LoginAuthentication");
        return string;
    }
}
