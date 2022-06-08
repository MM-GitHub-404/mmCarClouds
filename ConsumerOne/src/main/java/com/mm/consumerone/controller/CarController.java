package com.mm.consumerone.controller;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.mm.consumerone.entity.CarInfo;
import com.mm.consumerone.entity.CarType;
import com.mm.consumerone.entity.vo.CarInfoVo;
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
import com.mm.consumerone.utils.FileNameUtil;

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
@RequestMapping(value = "/car")
public class CarController {

    /**
     * 异步上传汽车图片的名称
     */
    String uploadFileName = "";

    /**
     * 远程微服务的注册中心地址
     */
    String carService = "http://ProCarInfo";

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 测试是否连通此微服务的方法
     */
    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity(carService + "/car/test", String.class);
        return responseEntity.getBody();
    }

    @RequestMapping("/initialization")
    public String initialization(HttpServletRequest request) {
        //首次查询车辆信息时一性加载车型列表
        List<CarType> carTypeList = restTemplate.getForObject(
                carService + "/car/initialization", List.class);
        //将车型列表信息装载入全局作用域中
        ServletContext application = request.getServletContext();
        application.setAttribute("carTypeList", carTypeList);
        return "forward:/car/firstPage";
    }

    @RequestMapping("/firstPage")
    public String firstPage(HttpServletRequest request) {
        ServletContext application = request.getServletContext();
        Object object = application.getAttribute("carTypeList");
        //查询车辆信息时若未加载车型列表则进入初始化方法进行加载
        if (object == null) {
            return "forward:/car/initialization";
        }
        PageInfo info = restTemplate.getForObject(
                carService + "/car/firstPage", PageInfo.class);
        request.setAttribute("info", info);
        return "admin/car";
    }

    @RequestMapping("/turnPages")
    @ResponseBody
    public void turnPages(CarInfoVo carInfoVo, HttpSession httpSession) {
        //取出carInfoVo中带有要跳转到的页数page参数,调用selectConditionPagination()进行翻页
        PageInfo info = restTemplate.postForObject(
                carService + "/car/turnPages", carInfoVo, PageInfo.class);
        httpSession.setAttribute("info", info);
    }


    @ResponseBody
    @RequestMapping("/uploadPicture")
    public Object uploadPicture(MultipartFile cimage, HttpServletRequest request) {
        //获取上传文件的名称,留置其他方法调用后再清除uploadFileName中信息
        uploadFileName = FileNameUtil.getUUIDFileName() + FileNameUtil.getFileType(cimage.getOriginalFilename());
        //获取文件存储路径
        String path = request.getServletContext().getRealPath("/image_big");
        try {
            //将文件传入服务器数据库
            cimage.transferTo(new File(path + File.separator + uploadFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> dataMap = new LinkedMultiValueMap<>();
        dataMap.add("uploadFileName", uploadFileName);
        restTemplate.postForObject(carService + "/car/uploadPicture", dataMap, String.class);

        //将文件名转为json格式返回,供前端在使用修改,新增功能时上传图片及时回显
        JSONObject object = new JSONObject();
        object.put("imgurl", uploadFileName);
        return object.toString();
    }

    @RequestMapping("/newCar")
    public String newCar(CarInfo carInfo, HttpServletRequest request) {
        int num = restTemplate.postForObject(
                carService + "/car/newCar", carInfo, Integer.class);
        if (num > 0) {
            request.setAttribute("msg", "新增汽车车型成功");
        } else {
            request.setAttribute("msg", "新增汽车车型失败");
        }
        //最后清空承载上传文件名称的变量,请求转发跳转第一页显示
        uploadFileName = "";
        return "forward:/car/firstPage";
    }

    @RequestMapping("/selectByIdCar")
    public String selectByIdCar(Integer cId, CarInfoVo carInfoVo, Model model, HttpSession httpSession) {
        //通过获取的车型id进行查询
        MultiValueMap<String, Integer> dataMap = new LinkedMultiValueMap<>();
        dataMap.add("cId", cId);
        CarInfo info = restTemplate.postForObject(
                carService + "/car/selectByIdCar", dataMap, CarInfo.class);
        //将车辆信息放入model的变量中,返回前端修改界面显示
        model.addAttribute("prod", info);
        //在会话作用域设置carInfoVo变量承载前端获得的查询条件,留置
        httpSession.setAttribute("carInfoVo", carInfoVo);
        return "admin/carUpdate";
    }

    @RequestMapping("/updateCarInfo")
    public String updateCarInfo(CarInfo carInfo, HttpServletRequest request) {
        //如果上传文件名称变量中有值,则修改carInfo变量中的属性
        if (!"".equals(uploadFileName)) {
            carInfo.setcImage(uploadFileName);
        }
        int num = restTemplate.postForObject(
                carService + "/car/updateCarInfo", carInfo, Integer.class);
        //判断是否更新成功,反馈信息到前端
        if (num > 0) {
            request.setAttribute("msg", "更新汽车信息成功");
        } else {
            request.setAttribute("msg", "更新汽车信息失败");
        }
        //最后清空承载上传文件名称的变量,跳转最初页面显示
        uploadFileName = "";
        return "forward:/car/firstPage";
    }

    @RequestMapping("/deleteCar")
    public String deleteCar(int cId, CarInfoVo carInfoVo, HttpServletRequest request) {
        //通过获取的车型id进行删除
        MultiValueMap<String, Integer> dataMap = new LinkedMultiValueMap<>();
        dataMap.add("cId", cId);
        int num = restTemplate.postForObject(
                carService + "/car/deleteCar", dataMap, Integer.class);
        //反馈前端删除结果,删除成功将页面状态信息放入会话作用域对象中提供deleteRefresh方法调用
        if (num > 0) {
            request.setAttribute("msg", "删除此汽车类型成功");
            request.getSession().setAttribute("deleteCarVo", carInfoVo);
        } else {
            request.setAttribute("msg", "删除此汽车类型失败");
        }
        return "forward:/car/deleteRefresh";
    }


    @RequestMapping(value = "/deleteRefresh", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object deleteRefresh(HttpServletRequest request) {
        PageInfo pageInfo = null;
        //获取carInfoVo对象中保存页面状态
        Object vo = request.getSession().getAttribute("deleteCarVo");
        pageInfo = restTemplate.getForObject(
                carService + "/car/deleteRefresh", PageInfo.class);
        //如果对象不为空则获取应跳转回的页面状态的车辆信息,否则获取第一页车辆信息
        if (vo != null) {
            request.getSession().removeAttribute("deleteCarVo");
        }
        //将查询集合放入会话作用域中供前端获取信息(刷新页面)
        request.getSession().setAttribute("info", pageInfo);
        //反馈前端删除结果
        return request.getAttribute("msg");
    }

    @RequestMapping("/deleteCarBatch")
    public String deleteCarBatch(String cIds, CarInfoVo carInfoVo, HttpServletRequest request) {
        //批量删除方法deleteCarBatch()参数为String数组,需要将要删除车辆id拆分装入数组
        MultiValueMap<String, String> dataMap = new LinkedMultiValueMap<>();
        dataMap.add("cIds", cIds);
        //获取影响记录条数
        int num = restTemplate.postForObject(
                carService + "/car/deleteCarBatch", dataMap, Integer.class);
        //反馈前端删除情况
        if (num > 0) {
            request.setAttribute("msg", "批量删除汽车成功");
            //将批量删除页面信息装入会话作用域中供刷新方法使用
            request.getSession().setAttribute("deleteCarVo", carInfoVo);
        } else {
            request.setAttribute("msg", "批量删除汽车失败");
        }
        //删除结束后刷新页面
        return "forward:/car/deleteRefresh";
    }
}