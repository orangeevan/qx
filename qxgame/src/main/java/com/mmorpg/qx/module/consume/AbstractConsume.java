package com.mmorpg.qx.module.consume;

import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.consume.resource.ConsumeResource;

/**
 * 消耗对象
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public abstract class AbstractConsume<T> implements Cloneable {

    /** 消耗类型 */
    private ConsumeType type;
    /** 消耗编码 */
    protected String code;
    /** 消耗值 */
    protected int value;

    public final Result verify(T object, int multiple) {
        if (multiple < 1) {
            throw new IllegalArgumentException(String.format("消耗倍数参数[%s]不合法, 该值必须>=1", multiple));
        }
        int amount = multiple * value;
        if (amount < 0) {
            throw new IllegalArgumentException(String.format("消耗验证溢出[%s]", amount));
        }
        return doVerify(object, multiple);
    }

    /**
     * 验证资源是否足够
     *
     * @param object
     * @param multiple
     * @return
     */
    protected abstract Result doVerify(T object, int multiple);

    /**
     * 消耗资源
     *
     * @param object
     * @param moduleInfo
     * @param multiple
     */
    public abstract void consume(T object, ModuleInfo moduleInfo, int multiple);

    /**
     * 合并消耗
     *
     * @param consume
     * @return
     */
    public final AbstractConsume merge(AbstractConsume consume) {
        if (canMerge(consume)) {
            return doMerge(consume);
        }
        return consume;
    }

    /**
     * 合并的条件,子类去实现
     *
     * @param consume
     * @return
     */
    protected boolean canMerge(AbstractConsume consume) {
        boolean clz = consume.getClass().equals(getClass());
        boolean code = consume.getCode().equals(getCode());
        return clz && code;
    }

    /**
     * 合并的方式
     *
     * @param consume
     * @return
     */
    protected AbstractConsume doMerge(AbstractConsume consume) {
        value += consume.value;
        return null;
    }

    /**
     * 深度克隆
     */
    @Override
    public abstract AbstractConsume clone();

    public void init(ConsumeResource resource) {
        value = resource.getValue();
        code = resource.getCode();
    }

    public ConsumeType getType() {
        return type;
    }

    public void setType(ConsumeType type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
