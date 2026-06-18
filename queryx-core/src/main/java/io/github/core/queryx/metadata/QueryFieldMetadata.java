package io.github.core.queryx.metadata;

import io.github.core.queryx.annotation.Eq;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public QueryFieldMetadata(String fieldName, Object value, QueryOperator operator) {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
    }
}
