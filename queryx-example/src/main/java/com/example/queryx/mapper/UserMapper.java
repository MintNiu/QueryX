package com.example.queryx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.queryx.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
