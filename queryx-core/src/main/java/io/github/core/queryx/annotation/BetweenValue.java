package io.github.core.queryx.annotation;

/**
 * BETWEEN 查询的值包装类
 * 
 * @author MintNiu
 */
public class BetweenValue {

    private Object left;
    private Object right;

    public BetweenValue() {
    }

    public BetweenValue(Object left, Object right) {
        this.left = left;
        this.right = right;
    }

    public Object getLeft() {
        return left;
    }

    public void setLeft(Object left) {
        this.left = left;
    }

    public Object getRight() {
        return right;
    }

    public void setRight(Object right) {
        this.right = right;
    }
}
