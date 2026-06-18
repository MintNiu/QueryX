package io.github.core.queryx.metadata;

import io.github.core.queryx.annotation.Eq;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * 查询类级别元数据（可缓存）
 * 
 * <p>缓存 DTO 类的注解信息，避免每次请求重复反射扫描。</p>
 * 
 * @author MintNiu
 * @since 0.1.0
 */
@Getter
public class QueryClassMetadata {

    private final Class<?> queryClass;
    private final List<FieldBinding> bindings;

    public QueryClassMetadata(Class<?> queryClass, List<FieldBinding> bindings) {
        this.queryClass = queryClass;
        this.bindings = Collections.unmodifiableList(bindings);
    }

    /**
     * 字段绑定信息
     * <p>记录每个字段的注解属性（不可变），运行时只需读取字段值即可。</p>
     */
    @Getter
    @RequiredArgsConstructor
    public static class FieldBinding {
        
        private final Field field;
        private final String dbFieldName;
        private final QueryOperator operator;
        private final boolean likePrefix;
        private final boolean likeSuffix;
        private final boolean not;
        private final Eq.Op op;
    }
}
