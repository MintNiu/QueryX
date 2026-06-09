package io.github.core.queryx.parser;

import io.github.core.queryx.annotation.Between;
import io.github.core.queryx.annotation.BetweenValue;
import io.github.core.queryx.annotation.Eq;
import io.github.core.queryx.annotation.In;
import io.github.core.queryx.annotation.Like;
import io.github.core.queryx.metadata.QueryFieldMetadata;
import io.github.core.queryx.metadata.QueryOperator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 基于反射的查询解析器
 * 
 * @author MintNiu
 */
public class ReflectionQueryParser implements QueryParser {

    @Override
    public List<QueryFieldMetadata> parse(Object query) {
        List<QueryFieldMetadata> result = new ArrayList<>();
        if (query == null) {
            return result;
        }
        
        Field[] fields = query.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(query);
                if (value == null) {
                    continue;
                }
                
                Eq eq = field.getAnnotation(Eq.class);
                if (eq != null) {
                    String fieldName = eq.value().trim().isEmpty() ? field.getName() : eq.value();
                    QueryFieldMetadata metadata = new QueryFieldMetadata(fieldName, value, 
                        convertEqOpToOperator(eq.op(), eq.not()));
                    metadata.setNot(eq.not());
                    metadata.setOp(eq.op());
                    result.add(metadata);
                    continue;
                }
                
                Like like = field.getAnnotation(Like.class);
                if (like != null) {
                    String fieldName = like.value().trim().isEmpty() ? field.getName() : like.value();
                    QueryFieldMetadata metadata = new QueryFieldMetadata(fieldName, value, 
                        like.not() ? QueryOperator.NOT_LIKE : QueryOperator.LIKE);
                    metadata.setLikePrefix(like.likePrefix());
                    metadata.setLikeSuffix(like.likeSuffix());
                    metadata.setNot(like.not());
                    result.add(metadata);
                    continue;
                }
                
                In in = field.getAnnotation(In.class);
                if (in != null && value instanceof Collection) {
                    String fieldName = in.value().trim().isEmpty() ? field.getName() : in.value();
                    QueryFieldMetadata metadata = new QueryFieldMetadata(fieldName, value, 
                        in.not() ? QueryOperator.NOT_IN : QueryOperator.IN);
                    metadata.setNot(in.not());
                    result.add(metadata);
                    continue;
                }
                
                Between between = field.getAnnotation(Between.class);
                if (between != null && value instanceof BetweenValue) {
                    String fieldName = between.value().trim().isEmpty() ? field.getName() : between.value();
                    BetweenValue betweenValue = (BetweenValue) value;
                    if (betweenValue.getLeft() != null || betweenValue.getRight() != null) {
                        result.add(new QueryFieldMetadata(fieldName, value, QueryOperator.BETWEEN));
                    }
                    continue;
                }
                
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field: " + field.getName(), e);
            }
        }
        return result;
    }
    
    /**
     * 将 Eq.Op 转换为 QueryOperator
     */
    private QueryOperator convertEqOpToOperator(Eq.Op op, boolean not) {
        // 如果设置了 op 属性，优先使用 op
        if (op != Eq.Op.EQ) {
            switch (op) {
                case GT: return QueryOperator.GT;
                case LT: return QueryOperator.LT;
                case GE: return QueryOperator.GE;
                case LE: return QueryOperator.LE;
                default: return QueryOperator.EQ;
            }
        }
        // 否则使用 not 属性
        return not ? QueryOperator.NE : QueryOperator.EQ;
    }
}
