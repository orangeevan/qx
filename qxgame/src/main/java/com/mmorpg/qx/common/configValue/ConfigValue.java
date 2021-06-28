package com.mmorpg.qx.common.configValue;

import com.mmorpg.qx.common.logger.SysLoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.core.convert.ConversionService;

import com.alibaba.fastjson.annotation.JSONField;
import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Inject;
import com.haipaite.common.resource.anno.Resource;
import com.haipaite.common.utility.JsonUtils;

/**
 * 值配置对象<br/>
 * 用于配置系统中的一些配置值
 *
 * @author frank
 */
@Resource
@SuppressWarnings("rawtypes")
public class ConfigValue<T> {

    private static final Logger logger = SysLoggerFactory.getLogger(ConfigValue.class);
    @Inject
    private static ConversionService conversionService;

    /**
     * 标识
     */
    @Id
    private String id;
    /**
     * 配置值
     */
    private String content;
    /**
     * 配置值格式类型
     */
    private ConfigValueType type;
    /**
     * 值类型参数
     */
    private Class clz;

    /**
     * 值
     */
    private transient volatile T value;

    /**
     * 获取配置值
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @JSONField(serialize = false)
    public T getValue() {
        if (value != null) {
            return value;
        }
        synchronized (this) {
            if (value == null) {
                try {
                    switch (type) {
                        case NORMAL:
                            value = (T) conversionService.convert(content, clz);
                            break;
                        case STR:
                            value = (T) content;
                            break;
                        case JSON:
                            if (StringUtils.startsWith(content, "[")) {
                                if (!clz.isArray()) {
                                    value = (T) JsonUtils.string2Object(content, clz);
                                } else {
                                    value = (T) JsonUtils.string2List(content, clz).toArray();
                                }
                            } else {
                                value = (T) JsonUtils.string2Object(content, clz);
                            }
                            break;
                        case ARRAY:
                            value = (T) JsonUtils.string2List(content, clz).toArray();
                            break;
                        case INT:
                            value = (T) JsonUtils.string2Object(content, Integer.class);
                    }
                } catch (Exception e) {
                    FormattingTuple message = MessageFormatter.format("值配置对象[{}]的配置值[{}]配置有误", id, content);
                    logger.error(message.getMessage(), e);
                    throw new IllegalArgumentException(message.getMessage(), e);
                }
            }
        }
        return value;
    }

    // Getter and Setter ...

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Class getClz() {
        return clz;
    }

    public ConfigValueType getType() {
        return type;
    }

    public void setType(ConfigValueType type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClz(Class clz) {
        this.clz = clz;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JSONField(serialize = false)
    public void setValue(T value) {
        this.value = value;
    }

}
