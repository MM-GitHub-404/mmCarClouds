package com.mm.commoncar.service;

import com.mm.commoncar.entity.CarType;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;

/**
 * @author 茂茂
 * @create 2022-02-12 13:45
 */
@FeignClient(name = "provider-1")
public interface CarTypeService {
    /**
     * 查询所有车辆类型
     * @return 返回车辆类型的list集合
     */
    List<CarType> selectAllType();
}
