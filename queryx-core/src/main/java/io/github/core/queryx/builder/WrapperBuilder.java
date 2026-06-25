package io.github.core.queryx.builder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.core.queryx.support.DataPermissionProvider;
import io.github.core.queryx.support.TenantProvider;

/**
 * Wrapper 构建器接口
 * 
 * @author MintNiu
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
     * 用于配合 MyBatis Plus 的 IService.page(Page, Wrapper) 方法使用
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
    
    /**
     * 设置数据权限提供者
     * 
     * @param provider 数据权限提供者
     */
    void setDataPermissionProvider(DataPermissionProvider provider);
    
    /**
     * 设置租户提供者
     * 
     * @param provider 租户提供者
     */
    void setTenantProvider(TenantProvider provider);
    
    /**
     * 设置租户字段名
     * 
     * @param tenantField 租户字段名（默认 tenant_id）
     */
    void setTenantField(String tenantField);
}
