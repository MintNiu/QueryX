package io.github.core.queryx.builder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public interface WrapperBuilder {

    <T> QueryWrapper<T> build(Object query);
}
