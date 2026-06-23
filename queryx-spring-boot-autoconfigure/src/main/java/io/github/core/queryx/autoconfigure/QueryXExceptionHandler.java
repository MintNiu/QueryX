package io.github.core.queryx.autoconfigure;

import io.github.core.queryx.support.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * QueryX 全局异常处理器
 * 
 * <p>通过 {@code @RestControllerAdvice} 统一处理所有异常，返回 JSON 格式的友好响应。</p>
 * 
 * <h3>处理的异常类型：</h3>
 * <ul>
 *   <li>{@link IllegalArgumentException} - 参数错误（状态码 400）</li>
 *   <li>{@link IllegalStateException} - 状态错误（状态码 503）</li>
 *   <li>{@link Exception} - 其他未知异常（状态码 500）</li>
 * </ul>
 * 
 * <h3>响应示例：</h3>
 * <pre>
 * {
 *   "code": 400,
 *   "message": "分页数量 1000 超过最大限制 500",
 *   "data": null
 * }
 * </pre>
 * 
 * <h3>自定义方式：</h3>
 * <p>用户可通过以下方式覆盖或禁用：</p>
 * <ol>
 *   <li>自定义同名 Bean（创建 QueryXExceptionHandler 子类）</li>
 *   <li>配置 {@code queryx.exceptionHandlerEnabled=false} 完全禁用</li>
 * </ol>
 * 
 * @author MintNiu
 * @since 0.1.0
 * @see io.github.core.queryx.support.Result
 * @see QueryXAutoConfiguration.ExceptionHandlerAutoConfiguration
 */
@RestControllerAdvice
public class QueryXExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(QueryXExceptionHandler.class);

    /**
     * 处理参数错误（IllegalArgumentException）
     * <p>场景：分页参数错误、排序字段白名单验证失败等。</p>
     * <p>响应：状态码 400，返回错误消息。</p>
     * 
     * @param e 异常对象
     * @return 参数错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("[QueryX] 参数错误: {}", e.getMessage());
        return Result.badRequest(e.getMessage());
    }

    /**
     * 处理状态错误（IllegalStateException）
     * <p>场景：服务状态不正确，如 QueryX 配置错误等。</p>
     * <p>响应：状态码 503，返回错误消息。</p>
     * 
     * @param e 异常对象
     * @return 状态错误响应
     */
    @ExceptionHandler(IllegalStateException.class)
    public Result<Void> handleIllegalStateException(IllegalStateException e) {
        log.error("[QueryX] 状态错误: {}", e.getMessage());
        return Result.error(503, e.getMessage());
    }

    /**
     * 处理其他所有异常
     * <p>场景：未预期的系统异常。</p>
     * <p>响应：状态码 500，返回通用错误消息（不暴露具体异常信息）。</p>
     * <p>注意：静态资源 404（如 favicon.ico）不记录 ERROR 日志。</p>
     * 
     * @param e 异常对象
     * @return 系统错误响应
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        // 静态资源 404（如 /favicon.ico）不记录 ERROR 日志
        String exClassName = e.getClass().getName();
        if (exClassName.contains("NoResourceFound") || exClassName.contains("ResourceNotFound")) {
            log.debug("[QueryX] 资源未找到: {}", e.getMessage());
            return Result.error(404, "资源未找到");
        }
        log.error("[QueryX] 系统异常: ", e);
        return Result.error("系统内部错误，请稍后再试");
    }
}
