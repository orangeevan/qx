package com.mmorpg.qx.common.configValue;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author wang ke
 * @description:需要单独配置常量数据
 * @since 10:30 2020-08-07
 */
@Component
public class ConfigValueManager implements ResourceReload {
    @Static
    private Storage<String, ConfigValue> configValueStorage;

    private static ConfigValueManager configValueManager;

    @PostConstruct
    private void init() {
        configValueManager = this;
    }

    public Object getConfigValue(String key) {
        return configValueStorage.get(key, true).getValue();
    }

    public static ConfigValueManager getInstance() {
        return configValueManager;
    }

    @Override
    public void reload() {

    }

    @Override
    public Class<?> getResourceClass() {
        return ConfigValue.class;
    }

    public int getIntConfigValue(String key) {
        return (int) getConfigValue(key);
    }

    public String getStrConfigValue(String key) {
        return (String) getConfigValue(key);
    }

    public Object[] getArrayConfigValue(String key) {
       return (Object[]) getConfigValue(key);
    }
}
