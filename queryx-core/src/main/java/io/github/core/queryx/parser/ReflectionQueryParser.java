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

import static java.lang.reflect.Modifier.isStatic;

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
        if (query == null) {
            return new ArrayList<>(0);
        }
        
        // 从缓存获取类级别元数据（首次调用时解析并缓存）
        QueryClassMetadata classMeta = CLASS_CACHE.computeIfAbsent(
                query.getClass(), this::parseClassMetadata);
        
        List<FieldBinding> bindings = classMeta.getBindings();
        // P2-5: 使用实际字段数预分配，避免默认 10 的浪费
        List<QueryFieldMetadata> result = new ArrayList<>(bindings.size());
        
        // 遍历缓存的字段绑定，只读取字段值
        for (FieldBinding binding : bindings) {
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
                
                // P0-2: 空集合跳过，避免生成 IN () 无效 SQL
                if ((operator == QueryOperator.IN || operator == QueryOperator.NOT_IN)
                        && value instanceof Collection && ((Collection<?>) value).isEmpty()) {
                    continue;
                }
                
                // P1-4: 空字符串跳过 LIKE 查询，避免生成 LIKE '%%' 匹配全部行
                if ((operator == QueryOperator.LIKE || operator == QueryOperator.NOT_LIKE)
                        && value instanceof String && ((String) value).trim().isEmpty()) {
                    continue;
                }
                
                QueryFieldMetadata metadata = new QueryFieldMetadata(
                        binding.getDbFieldName(), value, operator);
                metadata.setNot(binding.isNot());
                metadata.setOp(binding.getOp());
                metadata.setLikePrefix(binding.isLikePrefix());
                metadata.setLikeSuffix(binding.isLikeSuffix());
                metadata.setOrFields(binding.getOrFields());
                metadata.setOrOperator(binding.getOrOperator());
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
        
        // P0-3: 遍历类继承链，扫描父类字段（排除 Object）
        Class<?> currentClass = queryClass;
        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                // 跳过静态字段（如 serialVersionUID、DEFAULT_SIZE 等）
                if (isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                parseFieldAnnotations(field, bindings);
            }
            currentClass = currentClass.getSuperclass();
        }
        
        return new QueryClassMetadata(queryClass, bindings);
    }
    
    /**
     * 解析单个字段的注解并添加到绑定列表
     */
    private void parseFieldAnnotations(Field field, List<FieldBinding> bindings) {
        Eq eq = field.getAnnotation(Eq.class);
        if (eq != null) {
            // P2-6: 字段名 trim 后空值校验
            String fieldName = eq.value().trim();
            if (fieldName.isEmpty()) {
                fieldName = field.getName();
            }
            QueryOperator operator = convertEqOpToOperator(eq.op(), eq.not());
            bindings.add(new FieldBinding(field, fieldName, operator, false, false, eq.not(), eq.op(), null, null));
            return;
        }
        
        Like like = field.getAnnotation(Like.class);
        if (like != null) {
            String fieldName = like.value().trim();
            if (fieldName.isEmpty()) {
                fieldName = field.getName();
            }
            QueryOperator operator = like.not() ? QueryOperator.NOT_LIKE : QueryOperator.LIKE;
            bindings.add(new FieldBinding(field, fieldName, operator, like.likePrefix(), like.likeSuffix(), like.not(), null, null, null));
            return;
        }
        
        In in = field.getAnnotation(In.class);
        if (in != null) {
            String fieldName = in.value().trim();
            if (fieldName.isEmpty()) {
                fieldName = field.getName();
            }
            QueryOperator operator = in.not() ? QueryOperator.NOT_IN : QueryOperator.IN;
            bindings.add(new FieldBinding(field, fieldName, operator, false, false, in.not(), null, null, null));
            return;
        }
        
        Between between = field.getAnnotation(Between.class);
        if (between != null) {
            String fieldName = between.value().trim();
            if (fieldName.isEmpty()) {
                fieldName = field.getName();
            }
            bindings.add(new FieldBinding(field, fieldName, QueryOperator.BETWEEN, false, false, false, null, null, null));
            return;
        }
        
        OrderBy orderBy = field.getAnnotation(OrderBy.class);
        if (orderBy != null) {
            bindings.add(new FieldBinding(field, "_orderBy", QueryOperator.ORDER_BY, false, false, false, null, null, null));
            return;
        }
        
        // P0-1: @Or 注解解析，存储 operator 到 orOperator
        Or or = field.getAnnotation(Or.class);
        if (or != null) {
            List<String> orFields = Arrays.asList(or.fields());
            QueryOperator orOp = convertOrOpToOperator(or.operator());
            bindings.add(new FieldBinding(field, "_orGroup", QueryOperator.OR_GROUP, false, false, false, null, orFields, orOp));
        }
    }
    
    /**
     * 将 Or.Op 转换为 QueryOperator
     */
    private QueryOperator convertOrOpToOperator(Or.Op op) {
        switch (op) {
            case EQ: return QueryOperator.EQ;
            case LIKE: return QueryOperator.LIKE;
            case IN: return QueryOperator.IN;
            default: return QueryOperator.LIKE;
        }
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
    
    /**
     * 清除类级别元数据缓存
     * <p>适用于热部署场景（如 Spring DevTools），类重新加载后需清除旧缓存避免内存泄漏。</p>
     */
    public static void clearCache() {
        CLASS_CACHE.clear();
    }
}
