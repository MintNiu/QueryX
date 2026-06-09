package com.example.queryx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.queryx.UserQuery;
import com.example.queryx.entity.User;
import com.example.queryx.service.UserService;
import io.github.core.queryx.builder.WrapperBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器 - 演示如何使用 QueryX 进行查询
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final WrapperBuilder wrapperBuilder;

    /**
     * 根据条件查询用户列表
     */
    @GetMapping("/list")
    public List<User> listUsers(UserQuery query) {
        // 使用 QueryX 构建查询条件
        QueryWrapper<User> wrapper = wrapperBuilder.build(query);
        return userService.list(wrapper);
    }
}
