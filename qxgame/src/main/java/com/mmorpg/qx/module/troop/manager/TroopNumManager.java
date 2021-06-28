package com.mmorpg.qx.module.troop.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.mmorpg.qx.common.configValue.ConfigValueManager;
import com.mmorpg.qx.module.troop.resource.TroopNumResource;
import org.springframework.stereotype.Component;

/**
 * @author zhang peng
 * @since 10:29 2021/4/9
 */
@Component
public class TroopNumManager implements ResourceReload {

    @Static
    private Storage<Integer, TroopNumResource> troopNumStorage;

    /**
     * 编队卡牌数量限制
     *
     * @param level 玩家等级
     * @return
     */
    public int getCardNum(int level) {
        return troopNumStorage.getAll().stream().filter(t -> t.getLevel() == level)
                .findFirst().map(TroopNumResource::getCardNum).orElse(-1);
    }

    /***
     * 编队数量限制 普通和天梯相同
     * @return
     */
    public int getTroopNumLimit() {
        return ConfigValueManager.getInstance().getIntConfigValue("MaxTroopNum");
    }

    @Override
    public void reload() {

    }

    @Override
    public Class<?> getResourceClass() {
        return TroopNumResource.class;
    }
}
