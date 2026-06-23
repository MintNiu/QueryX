package io.github.core.queryx.support;

/**
 * 数据权限提供者接口
 * 
 * <p>用户实现此接口提供当前用户的数据权限值。</p>
 * <p>框架会在构建 QueryWrapper 时自动调用此接口，并追加权限条件。</p>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * &#64;Component
 * public class UserDataPermissionProvider implements DataPermissionProvider {
 *     &#64;Override
 *     public Object getPermissionValue() {
 *         // 从当前登录用户获取部门 ID
 *         return SecurityContextHolder.getContext().getDepartmentId();
 *     }
 * }
 * </pre>
 * 
 * <h3>返回值说明：</h3>
 * <ul>
 *   <li>返回 null → 不追加权限条件（管理员场景）</li>
 *   <li>返回单个值 → 生成 WHERE field = value</li>
 *   <li>返回 Collection → 生成 WHERE field IN (values)</li>
 * </ul>
 * 
 * @author MintNiu
 * @since 0.1.0
 * @see io.github.core.queryx.annotation.DataScope
 */
public interface DataPermissionProvider {

    /**
     * 获取当前用户的数据权限值
     * 
     * @return 权限值，返回 null 表示不限制
     */
    Object getPermissionValue();
}
