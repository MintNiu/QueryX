package io.github.core.queryx.builder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * Wrapper 构建器接口
 * 
 * <p>提供查询条件构建、分页对象构建、动态排序等核心功能。</p>
 * <p>数据权限和租户配置应在自动配置阶段完成，无需通过接口暴露。</p>
 * 
 * @author MintNiu
 * @since 0.1.0
 */
public interface WrapperBuilder {

    /**
     * 构建查询条件
     * 
     * @param query 查询 DTO
     * @return QueryWrapper
     */
    <T> QueryWrapper<T> build(Object query);
    
    /**
     * 构建分页查询（自动应用排序）
     * 
     * @param query 查询 DTO（需继承 BasePageQuery）
     * @return 包含分页信息和排序的 Page 对象
     */
    <T> Page<T> buildPage(Object query);
    
    /**
     * 构建分页查询的 QueryWrapper（包含查询条件和排序）
     * <p>用于配合 MyBatis Plus 的 IService.page(Page, Wrapper) 方法使用</p>
     * 
     * @param query 查询 DTO
     * @return QueryWrapper
     */
    <T> QueryWrapper<T> buildPageWrapper(Object query);
    
    /**
     * 应用排序到 QueryWrapper
     * 
     * @param wrapper QueryWrapper
     * @param orderBy 排序字符串，格式："id:desc,age:asc"
     */
    <T> void applyOrder(QueryWrapper<T> wrapper, String orderBy);
}
