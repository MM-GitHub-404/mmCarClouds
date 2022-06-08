package com.mm.loginone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 茂茂
 * @create 2022-05-18 17:46
 */
@Controller
@RequestMapping(value = "/main")
public class MainController {

    @RequestMapping(value = "/order")
    @ResponseBody
    public String order() {
        return "admin/order";
    }

    @RequestMapping(value = "/bulletin")
    @ResponseBody
    public String notice() {
        return "admin/bulletin";
    }

    @RequestMapping(value = "/quit")
    @ResponseBody
    public String quit() {
        return "index";
    }

}
