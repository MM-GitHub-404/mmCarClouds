package com.mm.loginone.service.impl;

import com.mm.commoncar.entity.UserType;
import com.mm.commoncar.entity.UserTypeExample;
import com.mm.loginone.dao.UserTypeMapper;
import com.mm.loginone.service.UserTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 茂茂
 * @create 2022-02-12 13:45
 */
@Service
public class UserTypeServiceImpl implements UserTypeService {

    @Autowired
    UserTypeMapper userTypeMapper;

    @Override
    public List<UserType> selectAllType() {
        return userTypeMapper.selectByExample(new UserTypeExample());
    }

}
