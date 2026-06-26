package io.github.core.queryx.metadata;

import io.github.core.queryx.annotation.Eq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 查询字段元数据
 * 
 * @author MintNiu
 */
@Data
@NoArgsConstructor
public class QueryFieldMetadata {

    private String fieldName;
    private Object value;
    private QueryOperator operator;
    private boolean likePrefix;
    private boolean likeSuffix;
    private boolean not;
    private Eq.Op op;
    
    /**
     * OR 组合字段列表（用于 OR_GROUP 操作符）
     */
    private List<String> orFields;
    
    /**
     * OR 组合操作符（用于 OR_GROUP 中每个字段的条件类型）
     */
    private QueryOperator orOperator;

    public QueryFieldMetadata(String fieldName, Object value, QueryOperator operator) {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
    }
}
