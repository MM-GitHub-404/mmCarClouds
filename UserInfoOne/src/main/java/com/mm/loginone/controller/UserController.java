package com.mm.loginone.controller;


import com.github.pagehelper.PageInfo;
import com.mm.commoncar.entity.UserInfo;
import com.mm.commoncar.entity.UserType;
import com.mm.commoncar.entity.vo.UserInfoVo;
import com.mm.loginone.service.UserInfoService;
import com.mm.loginone.service.UserTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * @author 茂茂
 * @create 2022-05-17 10:41
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {

    //设置分页显示每页条数
    private static final int CAR_PAGE_SIZE = 7;
    //异步上传用户头像的名称
    String uploadFileName = "";

    @Autowired
    UserInfoService userInfoService;
    @Autowired
    UserTypeService userTypeService;

    @RequestMapping("/test")
    @ResponseBody
    String myTest() {
        return "User一 测试";
    }

    @RequestMapping("/initialization")
    @ResponseBody
    public List<UserType> initialization() {
        List<UserType> userTypeList = userTypeService.selectAllType();
        return userTypeList;
    }

    @RequestMapping("/firstPage")
    @ResponseBody
    public PageInfo firstPage(HttpServletRequest request) {
        PageInfo info = null;
        //获取selectCarVo对象判断是否为带条件查询
        Object voObjet = request.getSession().getAttribute("userInfoVo");
        //有条件调用多条件查询方法
        if (voObjet != null) {
            info = userInfoService.selectConditionPagination((UserInfoVo) voObjet, CAR_PAGE_SIZE);
            //传入参数后将清理会话作用域中的无用对象
            request.getSession().removeAttribute("userInfoVo");
            //否则只返回所有结果的第一页记录
        } else {
            info = userInfoService.pagination(1, CAR_PAGE_SIZE);
        }
        //将查询结果封装为PageInfo类型属性赋予请求作用域返回car页面
        request.setAttribute("userInfo", info);
        return info;
    }


    @RequestMapping("/turnPages")
    @ResponseBody
    public PageInfo turnPages(@RequestBody UserInfoVo userInfoVo) {
        //取出UserInfoVo中带有要跳转到的页数page参数,调用selectConditionPagination()进行翻页
        PageInfo info = userInfoService.selectConditionPagination(userInfoVo, CAR_PAGE_SIZE);
        return info;
    }


    @RequestMapping("/uploadPicture")
    @ResponseBody
    public void uploadPicture(@RequestParam("uploadFileName") String string) {
        uploadFileName = string;
    }

    @RequestMapping("/newUser")
    @ResponseBody
    public int newCar(@RequestBody UserInfo userInfo) {
        //向carInfo承载文件名和时间的属性赋值,uploadFileName已由uploadPicture()方法赋值
        userInfo.setuAvatar(uploadFileName);
        userInfo.setuDate(new Date());
        int num = -1;
        try {
            //执行插入语句
            num = userInfoService.insertUser(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //最后清空承载上传文件名称的变量,请求转发跳转第一页显示
        uploadFileName = "";
        return num;
    }

    @RequestMapping("/selectByIdUser")
    @ResponseBody
    public UserInfo selectByIdCar(@RequestParam("uId") Integer uId) {
        //通过获取的车型id进行查询
        UserInfo info = userInfoService.selectById(uId);
        return info;
    }

    @RequestMapping("/updateUserInfo")
    @ResponseBody
    public int updateUserInfo(@RequestBody UserInfo userInfo) {
        //修改时间
        userInfo.setuDate(new Date());
        //如果上传文件名称变量中有值,则修改carInfo变量中的属性
        if (!uploadFileName.equals("")) {
            userInfo.setuAvatar(uploadFileName);
        }
        int num = -1;
        try {
            //调用sql修改数据库信息
            num = userInfoService.updateUserInfo(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //最后清空承载上传文件名称的变量,跳转最初页面显示
        uploadFileName = "";
        return num;
    }

    @RequestMapping("/deleteUser")
    @ResponseBody
    public int deleteCar(@RequestParam("uId") Integer uId) {
        int num = -1;
        try {
            //调用删除方法
            num = userInfoService.deleteUser(uId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }


    @RequestMapping(value = "/deleteRefresh")
    @ResponseBody
    public PageInfo deleteRefresh(HttpServletRequest request) {
        Object vo = request.getSession().getAttribute("deleteUserVo");
        PageInfo pageInfo = null;
        if (vo != null) {
            pageInfo = userInfoService.selectConditionPagination((UserInfoVo) vo, CAR_PAGE_SIZE);
        } else {
            pageInfo = userInfoService.pagination(1, CAR_PAGE_SIZE);
        }
        return pageInfo;
    }

    @RequestMapping("/deleteUserBatch")
    @ResponseBody
    public int deleteUserBatch(@RequestParam("uIds") String uIds) {
        //批量删除方法deleteUserBatch()参数为String数组,需要将要删除车辆id拆分装入数组
        String[] strs = uIds.split(",");
        int num = -1;
        //获取影响记录条数
        num = userInfoService.deleteUserBatch(strs);
        return num;
    }

    @ResponseBody
    @RequestMapping("/selectConditions")
    public void selectConditions(UserInfoVo UserInfoVo, HttpSession httpSession) {
        //按条件查询,将结果list放入会话作用域中等待调用
        List<UserInfo> list = userInfoService.selectConditions(UserInfoVo);
        httpSession.setAttribute("list", list);
    }
}