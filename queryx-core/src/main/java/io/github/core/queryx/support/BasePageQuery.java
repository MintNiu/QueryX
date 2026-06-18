package io.github.core.queryx.support;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询基类
 * 
 * <p>所有需要分页的查询 DTO 都应继承此类，自动获得分页参数支持。</p>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * // 1. 定义查询 DTO，继承 BasePageQuery
 * &#64;Data
 * &#64;EqualsAndHashCode(callSuper = true)
 * public class UserQuery extends BasePageQuery {
 *     
 *     &#64;OrderBy
 *     private String orderBy;
 *     
 *     &#64;Eq("status")
 *     private Integer status;
 *     
 *     &#64;Like("username")
 *     private String username;
 * }
 * 
 * // 2. Controller 中使用
 * &#64;GetMapping("/users/page")
 * public Page&lt;User&gt; listUsers(UserQuery query) {
 *     Page&lt;User&gt; page = wrapperBuilder.buildPage(query);
 *     return userService.page(page);
 * }
 * 
 * // 3. 请求示例
 * // GET /users/page?status=1&amp;username=张&amp;orderBy=id:desc,age:asc&amp;page=1&amp;size=20
 * </pre>
 * 
 * <h3>默认值：</h3>
 * <ul>
 *   <li>page: 1（第一页）</li>
 *   <li>size: 10（每页10条）</li>
 * </ul>
 * 
 * @author MintNiu
 * @since 0.1.0
 */
@Data
public abstract class BasePageQuery implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * 默认每页大小
     */
    private static final Long DEFAULT_SIZE = 10L;
    
    /**
     * 最小页码
     */
    private static final Long MIN_CURRENT = 1L;

    /**
     * 当前页码，从1开始
     */
    private Long current = 1L;

    /**
     * 每页大小，默认10
     */
    private Long size = 10L;
    
    /**
     * 设置当前页码（自动校验）
     * 
     * @param current 页码
     */
    public void setCurrent(Long current) {
        if (current == null || current < MIN_CURRENT) {
            this.current = MIN_CURRENT;
        } else {
            this.current = current;
        }
    }
    
    /**
     * 设置每页大小（自动校验）
     * <p>注意：上限由 DefaultWrapperBuilder 的 maxPageSize 配置控制</p>
     * 
     * @param size 每页大小
     */
    public void setSize(Long size) {
        if (size == null || size < 1) {
            this.size = DEFAULT_SIZE;
        } else {
            this.size = size;
        }
    }
}
