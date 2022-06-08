package com.mm.loginone.service.impl;

import com.mm.commoncar.entity.CarType;
import com.mm.commoncar.entity.CarTypeExample;
import com.mm.loginone.service.CarTypeService;
import com.mm.loginone.dao.CarTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 茂茂
 * @create 2022-02-12 13:45
 */
@Service
public class CarTypeServiceImpl implements CarTypeService {

    @Autowired
    CarTypeMapper carTypeMapper;

    @Override
    public List<CarType> selectAllType() {
        return carTypeMapper.selectByExample(new CarTypeExample());
    }

}
