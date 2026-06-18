package io.github.core.queryx.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一响应结果
 * 
 * <p>用于 API 接口的标准化响应格式，包含状态码、消息和数据。</p>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * // 成功响应（无数据）
 * return Result.success();
 * 
 * // 成功响应（带数据）
 * return Result.success(userList);
 * 
 * // 参数错误
 * return Result.badRequest("参数错误：分页数量不能超过 500");
 * 
 * // 自定义错误
 * return Result.error(503, "服务不可用");
 * </pre>
 * 
 * <h3>响应示例：</h3>
 * <pre>
 * {
 *   "code": 200,
 *   "message": "success",
 *   "data": [...]
 * }
 * </pre>
 * 
 * @author MintNiu
 * @since 0.1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /**
     * 成功响应（带数据）
     * 
     * @param data 响应数据
     * @return 成功响应结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /**
     * 成功响应（带消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 失败响应（默认状态码 500）
     * 
     * @param message 错误消息
     * @return 失败响应结果
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 失败响应（自定义状态码）
     * 
     * @param code 状态码
     * @param message 错误消息
     * @return 失败响应结果
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 参数错误响应（状态码 400）
     * 
     * @param message 错误消息
     * @return 参数错误响应结果
     */
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message, null);
    }
}
