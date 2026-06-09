package com.example.queryx.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.queryx.entity.User;
import com.example.queryx.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
