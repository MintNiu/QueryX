package io.github.core.queryx.parser;

import io.github.core.queryx.metadata.QueryFieldMetadata;

import java.util.List;

/**
 * 查询解析器接口
 * 
 * <p>负责将查询 DTO 对象解析为 {@link QueryFieldMetadata} 列表。</p>
 * <p>实现类应缓存类级别元数据（如注解信息），避免每次请求重复反射扫描。</p>
 * 
 * @author MintNiu
 * @since 0.1.0
 */
public interface QueryParser {

    /**
     * 解析查询对象
     * 
     * @param query 查询 DTO 对象
     * @return 查询字段元数据列表
     */
    List<QueryFieldMetadata> parse(Object query);

}
