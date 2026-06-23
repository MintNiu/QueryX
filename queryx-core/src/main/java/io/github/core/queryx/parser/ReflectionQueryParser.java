package io.github.core.queryx.parser;

import io.github.core.queryx.annotation.Between;
import io.github.core.queryx.annotation.BetweenValue;
import io.github.core.queryx.annotation.Eq;
import io.github.core.queryx.annotation.In;
import io.github.core.queryx.annotation.Like;
import io.github.core.queryx.annotation.Or;
import io.github.core.queryx.annotation.OrderBy;
import io.github.core.queryx.metadata.QueryClassMetadata;
import io.github.core.queryx.metadata.QueryClassMetadata.FieldBinding;
import io.github.core.queryx.metadata.QueryFieldMetadata;
import io.github.core.queryx.metadata.QueryOperator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于反射的查询解析器（支持元数据缓存）
 * 
 * <p>通过 ConcurrentHashMap 缓存 DTO 类的注解元数据，
 * 避免每次请求重复反射扫描，提升高频场景性能。</p>
 * 
 * @author MintNiu
 */
public class ReflectionQueryParser implements QueryParser {

    /**
     * 类级别元数据缓存（线程安全）
     */
    private static final ConcurrentHashMap<Class<?>, QueryClassMetadata> CLASS_CACHE = new ConcurrentHashMap<>();

    @Override
    public List<QueryFieldMetadata> parse(Object query) {
        List<QueryFieldMetadata> result = new ArrayList<>();
        if (query == null) {
            return result;
        }
        
        // 从缓存获取类级别元数据（首次调用时解析并缓存）
        QueryClassMetadata classMeta = CLASS_CACHE.computeIfAbsent(
                query.getClass(), this::parseClassMetadata);
        
        // 遍历缓存的字段绑定，只读取字段值
        for (FieldBinding binding : classMeta.getBindings()) {
            try {
                Object value = binding.getField().get(query);
                if (value == null) {
                    continue;
                }
                
                QueryOperator operator = binding.getOperator();
                
                // Between 需要额外检查值类型
                if (operator == QueryOperator.BETWEEN && value instanceof BetweenValue) {
                    BetweenValue betweenValue = (BetweenValue) value;
                    if (betweenValue.getLeft() == null && betweenValue.getRight() == null) {
                        continue;
                    }
                }
                
                QueryFieldMetadata metadata = new QueryFieldMetadata(
                        binding.getDbFieldName(), value, operator);
                metadata.setNot(binding.isNot());
                metadata.setOp(binding.getOp());
                metadata.setLikePrefix(binding.isLikePrefix());
                metadata.setLikeSuffix(binding.isLikeSuffix());
                metadata.setOrFields(binding.getOrFields());
                result.add(metadata);
                
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field: " + binding.getField().getName(), e);
            }
        }
        return result;
    }
    
    /**
     * 解析类级别元数据（仅在首次调用时执行）
     */
    private QueryClassMetadata parseClassMetadata(Class<?> queryClass) {
        List<FieldBinding> bindings = new ArrayList<>();
        Field[] fields = queryClass.getDeclaredFields();
        
        for (Field field : fields) {
            field.setAccessible(true);
            
            Eq eq = field.getAnnotation(Eq.class);
            if (eq != null) {
                String fieldName = eq.value().trim().isEmpty() ? field.getName() : eq.value();
                QueryOperator operator = convertEqOpToOperator(eq.op(), eq.not());
                bindings.add(new FieldBinding(field, fieldName, operator, false, false, eq.not(), eq.op(), null));
                continue;
            }
            
            Like like = field.getAnnotation(Like.class);
            if (like != null) {
                String fieldName = like.value().trim().isEmpty() ? field.getName() : like.value();
                QueryOperator operator = like.not() ? QueryOperator.NOT_LIKE : QueryOperator.LIKE;
                bindings.add(new FieldBinding(field, fieldName, operator, like.likePrefix(), like.likeSuffix(), like.not(), null, null));
                continue;
            }
            
            In in = field.getAnnotation(In.class);
            if (in != null) {
                String fieldName = in.value().trim().isEmpty() ? field.getName() : in.value();
                QueryOperator operator = in.not() ? QueryOperator.NOT_IN : QueryOperator.IN;
                bindings.add(new FieldBinding(field, fieldName, operator, false, false, in.not(), null, null));
                continue;
            }
            
            Between between = field.getAnnotation(Between.class);
            if (between != null) {
                String fieldName = between.value().trim().isEmpty() ? field.getName() : between.value();
                bindings.add(new FieldBinding(field, fieldName, QueryOperator.BETWEEN, false, false, false, null, null));
                continue;
            }
            
            OrderBy orderBy = field.getAnnotation(OrderBy.class);
            if (orderBy != null) {
                bindings.add(new FieldBinding(field, "_orderBy", QueryOperator.ORDER_BY, false, false, false, null, null));
                continue;
            }
            
            // @Or 注解：OR 组合查询
            Or or = field.getAnnotation(Or.class);
            if (or != null) {
                List<String> orFields = Arrays.asList(or.fields());
                bindings.add(new FieldBinding(field, "_orGroup", QueryOperator.OR_GROUP, false, false, false, null, orFields));
            }
        }
        
        return new QueryClassMetadata(queryClass, bindings);
    }
    
    /**
     * 将 Eq.Op 转换为 QueryOperator
     */
    private QueryOperator convertEqOpToOperator(Eq.Op op, boolean not) {
        if (op != Eq.Op.EQ) {
            switch (op) {
                case GT: return QueryOperator.GT;
                case LT: return QueryOperator.LT;
                case GE: return QueryOperator.GE;
                case LE: return QueryOperator.LE;
                default: return QueryOperator.EQ;
            }
        }
        return not ? QueryOperator.NE : QueryOperator.EQ;
    }
}
