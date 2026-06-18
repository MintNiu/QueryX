package io.github.core.queryx.validator;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 排序字段验证结果
 * 
 * <p>包含过滤后的排序字符串和警告信息列表。</p>
 * 
 * <h3>使用场景：</h3>
 * <ul>
 *   <li>当用户传入多个排序字段时，自动过滤不在白名单中的字段</li>
 *   <li>返回警告信息，告知用户哪些字段被忽略</li>
 *   <li>合法字段继续参与排序</li>
 * </ul>
 * 
 * <h3>示例：</h3>
 * <pre>
 * // 输入：id:desc,password:asc,email:desc
 * // 白名单：id, email, status
 * 
 * OrderByResult result = validator.validateAndFilter("id:desc,password:asc,email:desc");
 * 
 * // 输出：
 * // result.getFilteredOrderBy() → "id:desc,email:desc"
 * // result.getWarnings() → ["排序字段 'password' 不在白名单中，已忽略"]
 * </pre>
 * 
 * @author MintNiu
 * @since 0.1.0
 */
@Getter
@ToString
public class OrderByResult {

    /**
     * 过滤后的排序字符串（只包含白名单中的字段）
     */
    private final String filteredOrderBy;

    /**
     * 警告信息列表
     */
    private final List<String> warnings;

    /**
     * 创建验证结果
     * 
     * @param filteredOrderBy 过滤后的排序字符串
     * @param warnings 警告信息列表
     */
    public OrderByResult(String filteredOrderBy, List<String> warnings) {
        this.filteredOrderBy = filteredOrderBy;
        this.warnings = warnings != null ? Collections.unmodifiableList(new ArrayList<>(warnings)) : Collections.emptyList();
    }

    /**
     * 是否有警告信息
     * 
     * @return true 如果有警告，false 否则
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
}
