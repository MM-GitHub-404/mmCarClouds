package com.mm.loginone.controller;

import com.mm.commoncar.entity.Admin;
import com.mm.loginone.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author 茂茂
 * @create 2022-05-22 16:13
 */
@Controller
@RequestMapping(value = "/home")
public class AdminController {

    @Autowired
    AdminService adminService;

    @RequestMapping(value = "/login")
    @ResponseBody
    public Admin login(@RequestParam("name") String name, @RequestParam("pwd") String pwd) {
        //调用业务逻辑层login方法获取用户信息
        Admin admin = adminService.login(name, pwd);
        return admin;
    }

    @RequestMapping("/test")
    @ResponseBody
    String myTest() {
        return "登陆一 测试";
    }
}
