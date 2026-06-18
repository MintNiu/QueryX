package io.github.core.queryx.annotation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BETWEEN 查询的值包装类
 * 
 * @author MintNiu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BetweenValue {

    private Object left;
    private Object right;
}
