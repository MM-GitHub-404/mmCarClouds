package com.mm.loginone.service;

import com.mm.commoncar.entity.UserType;

import java.util.List;

/**
 * @author 茂茂
 * @create 2022-02-12 13:45
 */
public interface UserTypeService {
    /**
     * 查询所有用户类型
     * @return 返回用户类型的list集合
     */
    List<UserType> selectAllType();
}
