package com.mm.commoncar.service;

import com.mm.commoncar.entity.UserType;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;

/**
 * @author 茂茂
 * @create 2022-02-12 13:45
 */
@FeignClient(name = "provider-1")
public interface UserTypeService {
    /**
     * 查询所有用户类型
     * @return 返回用户类型的list集合
     */
    List<UserType> selectAllType();
}
