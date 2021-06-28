package com.mmorpg.qx.module.equipment.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.identify.manager.IdentifyManager;
import com.mmorpg.qx.module.equipment.model.EquipItem;
import com.mmorpg.qx.module.equipment.resource.EquipmentResource;
import com.mmorpg.qx.module.object.controllers.EquipController;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * @author wang ke
 * @description: 装备管理器
 * @since 13:32 2020-11-02
 */
@Component
public class EquipmentManager implements ResourceReload {

    @Static
    private Storage<Integer, EquipmentResource> equipmentManager;

    @PostConstruct
    private void init() {
        initResources();
    }

    public static EquipmentManager getInstance() {
        return BeanService.getBean(EquipmentManager.class);
    }

    public EquipmentResource getEquipResource(int resourceId) {
        return equipmentManager.get(resourceId, false);
    }

    public EquipItem genEquipItem(EquipmentResource resource) {
        long id = IdentifyManager.getInstance().getNextIdentify(IdentifyManager.IdentifyType.MONSTER);
        EquipItem equipItem = new EquipItem(id, resource, new EquipController(), null);
        return equipItem;
    }

    public Collection<EquipmentResource> getAllEquips() {
        return equipmentManager.getAll();
    }

    @Override
    public void reload() {
        initResources();
    }

    @Override
    public Class<?> getResourceClass() {
        return EquipmentResource.class;
    }

    private void initResources() {
        equipmentManager.getAll().forEach(EquipmentResource::init);
    }
}
