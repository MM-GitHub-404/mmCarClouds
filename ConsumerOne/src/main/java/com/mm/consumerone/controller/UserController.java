package com.mm.consumerone.controller;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.mm.consumerone.entity.UserInfo;
import com.mm.consumerone.entity.UserType;
import com.mm.consumerone.entity.vo.UserInfoVo;
import com.mm.consumerone.utils.FileNameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author 茂茂
 * @create 2022-05-17 10:41
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {

    /**
     * 异步上传用户图片的名称
     */
    String uploadFileName = "";

    /**
     * 远程微服务的注册中心地址
     */
    String userService = "http://ProUserInfo";

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 测试是否连通此微服务的方法
     */
    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity(userService + "/user/test", String.class);
        return responseEntity.getBody();
    }

    @RequestMapping("/initialization")
    public String initialization(HttpServletRequest request) {
        //首次查询车辆信息时一性加载会员类型列表
        List<UserType> userTypeList = restTemplate.getForObject(
                userService + "/user/initialization", List.class);
        //将会员类型列表信息装载入全局作用域中
        ServletContext application = request.getServletContext();
        application.setAttribute("userTypeList", userTypeList);
        return "forward:/user/firstPage";
    }

    @RequestMapping("/firstPage")
    public String firstPage(HttpServletRequest request) {
        ServletContext application = request.getServletContext();
        Object object = application.getAttribute("userTypeList");
        //查询会员信息时若未加载会员类型列表则进入初始化方法进行加载
        if (object == null) {
            return "forward:/user/initialization";
        }
        PageInfo info = restTemplate.getForObject(
                userService + "/user/firstPage", PageInfo.class);
        request.setAttribute("userInfo", info);
        return "admin/user";
    }

    @RequestMapping("/turnPages")
    @ResponseBody
    public void turnPages(UserInfoVo userInfoVo, HttpSession httpSession) {
        //取出userInfoVo中带有要跳转到的页数page参数,调用selectConditionPagination()进行翻页
        PageInfo info = restTemplate.postForObject(
                userService + "/user/turnPages", userInfoVo, PageInfo.class);
        httpSession.setAttribute("userInfo", info);
    }


    @ResponseBody
    @RequestMapping("/uploadPicture")
    public Object uploadPicture(MultipartFile uavatar, HttpServletRequest request) {
        //获取上传文件的名称,留置其他方法调用后再清除uploadFileName中信息
        uploadFileName = FileNameUtil.getUUIDFileName() + FileNameUtil.getFileType(uavatar.getOriginalFilename());
        //获取文件存储路径
        String path = request.getServletContext().getRealPath("/image_big");
        try {
            //将文件传入服务器数据库
            uavatar.transferTo(new File(path + File.separator + uploadFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> dataMap = new LinkedMultiValueMap<>();
        dataMap.add("uploadFileName", uploadFileName);
        restTemplate.postForObject(userService + "/user/uploadPicture", dataMap, String.class);

        //将文件名转为json格式返回,供前端在使用修改,新增功能时上传图片及时回显
        JSONObject object = new JSONObject();
        object.put("imgurl", uploadFileName);
        return object.toString();
    }

    @RequestMapping("/newUser")
    public String newCar(UserInfo userInfo, HttpServletRequest request) {
        int num = restTemplate.postForObject(
                userService + "/user/newUser", userInfo, Integer.class);
        if (num > 0) {
            request.setAttribute("msg", "新增用户成功");
        } else {
            request.setAttribute("msg", "新增用户失败");
        }
        //最后清空承载上传文件名称的变量,请求转发跳转第一页显示
        uploadFileName = "";
        return "forward:/user/firstPage";
    }

    @RequestMapping("/selectByIdUser")
    public String selectByIdUser(Integer uId, UserInfoVo userInfoVo, Model model, HttpSession httpSession) {
        //通过获取的用户id进行查询
        MultiValueMap<String, Integer> dataMap = new LinkedMultiValueMap<>();
        dataMap.add("uId", uId);
        UserInfo info = restTemplate.postForObject(
                userService + "/user/selectByIdUser", dataMap, UserInfo.class);
        //将用户信息放入model的变量中,返回前端修改界面显示
        model.addAttribute("prod", info);
        //在会话作用域设置userInfoVo变量承载前端获得的查询条件,留置
        httpSession.setAttribute("userInfoVo", userInfoVo);
        return "admin/userUpdate";
    }

    @RequestMapping("/updateUserInfo")
    public String updateUserInfo(UserInfo userInfo, HttpServletRequest request) {
        //如果上传文件名称变量中有值,则修改userInfo变量中的属性
        if (!"".equals(uploadFileName)) {
            userInfo.setuAvatar(uploadFileName);
        }
        int num = restTemplate.postForObject(
                userService + "/user/updateUserInfo", userInfo, Integer.class);
        //判断是否更新成功,反馈信息到前端
        if (num > 0) {
            request.setAttribute("msg", "更新用户信息成功");
        } else {
            request.setAttribute("msg", "更新用户信息失败");
        }
        //最后清空承载上传文件名称的变量,跳转最初页面显示
        uploadFileName = "";
        return "forward:/user/firstPage";
    }

    @RequestMapping("/deleteUser")
    public String deleteUser(int uId, UserInfoVo userInfoVo, HttpServletRequest request) {
        //通过获取的会员id进行删除
        MultiValueMap<String, Integer> dataMap = new LinkedMultiValueMap<>();
        dataMap.add("uId", uId);
        int num = restTemplate.postForObject(
                userService + "/user/deleteUser", dataMap, Integer.class);
        //反馈前端删除结果,删除成功将页面状态信息放入会话作用域对象中提供deleteRefresh方法调用
        if (num > 0) {
            request.setAttribute("msg", "删除用户成功");
            request.getSession().setAttribute("deleteUserVo", userInfoVo);
        } else {
            request.setAttribute("msg", "删除用户失败");
        }
        return "forward:/user/deleteRefresh";
    }


    @RequestMapping(value = "/deleteRefresh", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object deleteRefresh(HttpServletRequest request) {
        PageInfo pageInfo = null;
        //获取userInfoVo对象中保存页面状态
        Object vo = request.getSession().getAttribute("deleteUserVo");
        pageInfo = restTemplate.getForObject(
                userService + "/user/deleteRefresh", PageInfo.class);
        //如果对象不为空则获取应跳转回的页面状态的用户信息,否则获取第一页用户信息
        if (vo != null) {
            request.getSession().removeAttribute("deleteUserVo");
        }
        //将查询集合放入会话作用域中供前端获取信息(刷新页面)
        request.getSession().setAttribute("userInfo", pageInfo);
        //反馈前端删除结果
        return request.getAttribute("msg");
    }

    @RequestMapping("/deleteUserBatch")
    public String deleteUserBatch(String uIds, UserInfoVo userInfoVo, HttpServletRequest request) {
        //批量删除方法deleteUserBatch()参数为String数组,需要将要删除用户id拆分装入数组
        MultiValueMap<String, String> dataMap = new LinkedMultiValueMap<>();
        dataMap.add("uIds", uIds);
        //获取影响记录条数
        int num = restTemplate.postForObject(
                userService + "/user/deleteUserBatch", dataMap, Integer.class);
        //反馈前端删除情况
        if (num > 0) {
            request.setAttribute("msg", "批量删除用户成功");
            //将批量删除页面信息装入会话作用域中供刷新方法使用
            request.getSession().setAttribute("deleteUserVo", userInfoVo);
        } else {
            request.setAttribute("msg", "批量删除用户失败");
        }
        //删除结束后刷新页面
        return "forward:/user/deleteRefresh";
    }
}