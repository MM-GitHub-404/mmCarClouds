package com.mm.consumerone.controller;

import com.mm.consumerone.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author 茂茂
 * @create 2022-05-23 8:51
 */
@Controller
@RequestMapping("/home")
public class AdminControler {

    //远程微服务的注册中心地址
    String loginService = "http://Login";

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/login")
    public String login(String name, String pwd, HttpServletRequest request, HttpSession session) {
        MultiValueMap<String, Object> dataMap = new LinkedMultiValueMap<>();
        dataMap.add("name", name);
        dataMap.add("pwd", pwd);
        Admin admin = restTemplate.postForObject(
                loginService + "/home/login", dataMap, Admin.class);
        //调用业务逻辑层login方法获取用户信息
        if (admin != null) {
            //登录成功后获取登录用户名,添加欢迎信息,跳转main界面
            request.setAttribute("name", admin.getaName());
            session.setAttribute("LoginAuthentication", admin.getaName());
            return "admin/main";
        }
        //登陆失败后添加错误信息,重回login界面.
        request.setAttribute("errmsg", "<h3 style='color:mediumseagreen'>用户名或密码错误</h3>");
        return "index";
    }


    @RequestMapping(value = "/register")
    public String register(HttpServletRequest request) {
        //登陆失败后添加错误信息,重回login界面.
        request.setAttribute(
                "errmsg", "<h3 style='color:mediumseagreen'>请联系系统管理员分配账号</h3>");
        return "index";
    }

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        String test =
                restTemplate.getForObject(loginService + "/home/test", String.class);
        return test;
    }
}
