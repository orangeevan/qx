package com.mmorpg.qx.module.consume.resource;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import com.alibaba.fastjson.annotation.JSONField;
import com.mmorpg.qx.module.consume.ConsumeType;
import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

/**
 * 消耗配置表,支持动态属性
 *
 * @author wang ke
 * @since v1.0 2018年2月28日
 */
@Resource
public class ConsumeResource {
    @Id
    private String key;

    private ConsumeType type;

    private String formula;

    private String code;

    private int value;

    /**
     * 编译后的 验证表达式
     */
    @JSONField(serialize = false)
    private transient volatile Serializable formulaExp;

    /**
     * 表达式上下文
     */
    @JSONField(serialize = false)
    private static final ParserContext PARSER_CONTEXT = new ParserContext();

    static {
        /** 导入 {@link Math} 中的全部静态方法 */
        PARSER_CONTEXT.addImport(Math.class);
        for (Method method : Math.class.getMethods()) {
            int mod = method.getModifiers();
            if (Modifier.isStatic(mod) && Modifier.isPublic(mod)) {
                String name = method.getName();
                PARSER_CONTEXT.addImport(name, method);
            }
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ConsumeType getType() {
        return type;
    }

    public void setType(ConsumeType type) {
        this.type = type;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @JSONField(serialize = false)
    public int calculateValue(Map<String, Object> context) {
        if (value != 0) {
            return value;
        }
        if (formulaExp == null) {
            synchronized (this) {
                if (formula == null) {
                    return 0;
                }
                if (formulaExp == null) {
                    formulaExp = MVEL.compileExpression(formula, PARSER_CONTEXT);
                }
            }
        }
        return MVEL.executeExpression(formulaExp, context, Integer.class);
    }

    @JSONField(serialize = false)
    public static ConsumeResource valueOf(ConsumeType type, String code, int value) {
        ConsumeResource resource = new ConsumeResource();
        resource.setType(type);
        resource.setCode(code);
        resource.setValue(value);
        return resource;
    }
}
