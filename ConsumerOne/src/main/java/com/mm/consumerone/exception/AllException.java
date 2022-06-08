package com.mm.consumerone.exception;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;

/**
 * @author 茂茂
 * @create 2022-05-25 23:31
 */

@ControllerAdvice
public class AllException {

    /**
     * 后台异常返回错误界面
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView exceptionHandler(HttpServletRequest request, Exception e) {
        ModelAndView mv = new ModelAndView();
        //获取异常的相关信息
        mv.addObject("url", request.getRequestURL());
        mv.addObject("exception", e);
        //返回自定义的异常界面
        mv.setViewName("admin/error");
        return mv;
    }
}


