package io.github.core.queryx.metadata;

/**
 * 查询字段元数据
 * 
 * @author MintNiu
 */
public class QueryFieldMetadata {

    private String fieldName;
    private Object value;
    private QueryOperator operator;
    private boolean likePrefix;
    private boolean likeSuffix;

    public QueryFieldMetadata() {
    }

    public QueryFieldMetadata(String fieldName, Object value, QueryOperator operator) {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public QueryOperator getOperator() {
        return operator;
    }

    public void setOperator(QueryOperator operator) {
        this.operator = operator;
    }

    public boolean isLikePrefix() {
        return likePrefix;
    }

    public void setLikePrefix(boolean likePrefix) {
        this.likePrefix = likePrefix;
    }

    public boolean isLikeSuffix() {
        return likeSuffix;
    }

    public void setLikeSuffix(boolean likeSuffix) {
        this.likeSuffix = likeSuffix;
    }
}
