package com.mm.commoncar.service;

import com.mm.commoncar.entity.Admin;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 茂茂
 * @create 2022-05-16 20:27
 */
@Component
@FeignClient(name = "ProLogin")
public interface AdminService {
    /**
     * 登录验证
     *
     * @param userName 传入的用户名
     * @param passWord 传入的密码
     * @return 一个Admin对象, Admin!=null则通过登录验证.
     */
    @RequestMapping("/home/login")
    Admin login(String userName, String passWord);

    @RequestMapping("/home/test")
    String test();
}
