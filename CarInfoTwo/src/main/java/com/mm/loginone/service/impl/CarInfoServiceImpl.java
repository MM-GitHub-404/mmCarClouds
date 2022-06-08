package com.mm.loginone.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mm.commoncar.entity.CarInfo;
import com.mm.commoncar.entity.CarInfoExample;
import com.mm.commoncar.entity.vo.CarInfoVo;
import com.mm.loginone.service.CarInfoService;
import com.mm.loginone.dao.CarInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 茂茂
 * @create 2022-02-11 17:41
 */
@Service
public class CarInfoServiceImpl implements CarInfoService {

    @Autowired
    CarInfoMapper carInfoMapper;

    @Override
    public List<CarInfo> selectAll() {
        //selectByExample方法参数为CarInfoExample类型
        return carInfoMapper.selectByExample(new CarInfoExample());
    }

    @Override
    public PageInfo pagination(int pageNum, int pageSize) {
        //优先使用PageHelper工具类进行分页设置
        PageHelper.startPage(pageNum, pageSize);
        CarInfoExample carInfoExample = new CarInfoExample();
        //设置倒序查询语句
        carInfoExample.setOrderByClause("c_id desc");
        //查询所有车辆记录,装入list集合
        List<CarInfo> list = carInfoMapper.selectByExample(carInfoExample);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCar(CarInfo carInfo) {
        return carInfoMapper.insert(carInfo);
    }

    @Override
    public CarInfo selectById(Integer cId) {
        return carInfoMapper.selectByPrimaryKey(cId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCarInfo(CarInfo carInfo) {
        int num = carInfoMapper.updateByPrimaryKey(carInfo);
        return num;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteCar(int cId) {
        return carInfoMapper.deleteByPrimaryKey(cId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCarBatch(String[] cIds) {
        return carInfoMapper.deleteCarBatch(cIds);
    }

    @Override
    public PageInfo<CarInfo> selectConditionPagination(CarInfoVo carInfoVo, int page) {
        //优先使用PageHelper工具类进行分页设置
        PageHelper.startPage(carInfoVo.getPage(), page);
        //调用多条件查询sql获取符合条件的车辆信息列表
        List<CarInfo> list = carInfoMapper.selectConditions(carInfoVo);
        return new PageInfo<>(list);
    }

    @Override
    public List<CarInfo> selectConditions(CarInfoVo carInfoVo) {
        //按条件查询
        return carInfoMapper.selectConditions(carInfoVo);
    }

}
