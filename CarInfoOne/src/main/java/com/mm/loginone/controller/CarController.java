package com.mm.loginone.controller;


import com.github.pagehelper.PageInfo;
import com.mm.commoncar.entity.CarInfo;
import com.mm.commoncar.entity.CarType;
import com.mm.commoncar.entity.vo.CarInfoVo;
import com.mm.loginone.service.CarTypeService;
import com.mm.loginone.service.CarInfoService;
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
@RequestMapping(value = "/car")
public class CarController {

    /**
     * 设置分页显示每页条数
     */
    private static final int CAR_PAGE_SIZE = 7;

    /**
     * 异步上传汽车图片的名称
     */
    String uploadFileName = "";

    @Autowired
    CarInfoService carInfoService;
    @Autowired
    CarTypeService carTypeService;

    @RequestMapping("/test")
    @ResponseBody
    String myTest() {
        return "Car一 测试";
    }

    @RequestMapping("/initialization")
    @ResponseBody
    public List<CarType> initialization() {
        List<CarType> carTypeList = carTypeService.selectAllType();
        return carTypeList;
    }

    @RequestMapping("/firstPage")
    @ResponseBody
    public PageInfo firstPage(HttpServletRequest request) {
        PageInfo info = null;
        //获取selectCarVo对象判断是否为带条件查询
        Object voObjet = request.getSession().getAttribute("carInfoVo");
        //有条件调用多条件查询方法
        if (voObjet != null) {
            info = carInfoService.selectConditionPagination((CarInfoVo) voObjet, CAR_PAGE_SIZE);
            //传入参数后将清理会话作用域中的无用对象
            request.getSession().removeAttribute("carInfoVo");
            //否则只返回所有结果的第一页记录
        } else {
            info = carInfoService.pagination(1, CAR_PAGE_SIZE);
        }
        //将查询结果封装为PageInfo类型属性赋予请求作用域返回car页面
        request.setAttribute("info", info);
        return info;
    }


    @RequestMapping("/turnPages")
    @ResponseBody
    public PageInfo turnPages(@RequestBody CarInfoVo carInfoVo) {
        //取出carInfoVo中带有要跳转到的页数page参数,调用selectConditionPagination()进行翻页
        PageInfo info = carInfoService.selectConditionPagination(carInfoVo, CAR_PAGE_SIZE);
        return info;
    }

    @ResponseBody
    @RequestMapping("/uploadPicture")
    public void uploadPicture(@RequestParam("uploadFileName") String string) {
        uploadFileName = string;
    }

    @RequestMapping("/newCar")
    @ResponseBody
    public int newCar(@RequestBody CarInfo carInfo) {
        //向carInfo承载文件名和时间的属性赋值,uploadFileName已由uploadPicture()方法赋值
        carInfo.setcImage(uploadFileName);
        carInfo.setcDate(new Date());
        int num = -1;
        try {
            //执行插入语句
            num = carInfoService.insertCar(carInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //最后清空承载上传文件名称的变量,请求转发跳转第一页显示
        uploadFileName = "";
        return num;
    }

    @RequestMapping("/selectByIdCar")
    @ResponseBody
    public CarInfo selectByIdCar(@RequestParam("cId") Integer cId) {
        //通过获取的车型id进行查询
        CarInfo info = carInfoService.selectById(cId);
        return info;
    }

    @RequestMapping("/updateCarInfo")
    @ResponseBody
    public int updateCarInfo(@RequestBody CarInfo carInfo) {
        //修改时间
        carInfo.setcDate(new Date());
        //如果上传文件名称变量中有值,则修改carInfo变量中的属性
        if (!uploadFileName.equals("")) {
            carInfo.setcImage(uploadFileName);
        }
        int num = -1;
        try {
            //调用sql修改数据库信息
            num = carInfoService.updateCarInfo(carInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //最后清空承载上传文件名称的变量,跳转最初页面显示
        uploadFileName = "";
        return num;
    }

    @RequestMapping("/deleteCar")
    @ResponseBody
    public int deleteCar(@RequestParam("cId") Integer cId) {
        int num = -1;
        try {
            //调用删除方法
            num = carInfoService.deleteCar(cId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }


    @RequestMapping(value = "/deleteRefresh")
    @ResponseBody
    public PageInfo deleteRefresh(HttpServletRequest request) {
        Object vo = request.getSession().getAttribute("deleteCarVo");
        PageInfo pageInfo = null;
        if (vo != null) {
            pageInfo = carInfoService.selectConditionPagination((CarInfoVo) vo, CAR_PAGE_SIZE);
        } else {
            pageInfo = carInfoService.pagination(1, CAR_PAGE_SIZE);
        }
        return pageInfo;
    }

    @RequestMapping("/deleteCarBatch")
    @ResponseBody
    public int deleteCarBatch(@RequestParam("cIds") String cIds) {
        //批量删除方法deleteCarBatch()参数为String数组,需要将要删除车辆id拆分装入数组
        String[] strs = cIds.split(",");
        int num = -1;
        //获取影响记录条数
        num = carInfoService.deleteCarBatch(strs);
        return num;
    }

    @ResponseBody
    @RequestMapping("/selectConditions")
    public void selectConditions(CarInfoVo carInfoVo, HttpSession httpSession) {
        //按条件查询,将结果list放入会话作用域中等待调用
        List<CarInfo> list = carInfoService.selectConditions(carInfoVo);
        httpSession.setAttribute("list", list);
    }
}